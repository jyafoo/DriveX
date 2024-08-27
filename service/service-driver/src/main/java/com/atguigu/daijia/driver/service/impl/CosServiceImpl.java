package com.atguigu.daijia.driver.service.impl;

import com.alibaba.fastjson2.JSON;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.config.TencentCloudProperties;
import com.atguigu.daijia.driver.service.CiService;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class CosServiceImpl implements CosService {

    private final TencentCloudProperties tencentCloudProperties;
    private final CiService ciService;

    /**
     * 创建并返回一个配置了私有凭证的COSClient实例
     * 该方法使用腾讯云的凭证（SecretId和SecretKey）以及指定的区域来创建COSClient
     *
     * @return 配置了私有凭证和区域的COSClient实例
     */
    private COSClient getPrivateCOSClient() {
        // 创建COS凭证实例，使用腾讯云的SecretId和SecretKey进行初始化
        COSCredentials cred = new BasicCOSCredentials(tencentCloudProperties.getSecretId(), tencentCloudProperties.getSecretKey());
        // 创建客户端配置实例，并指定所使用的区域
        ClientConfig clientConfig = new ClientConfig(new Region(tencentCloudProperties.getRegion()));
        // 配置使用HTTPS协议，以增强通信安全
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 使用凭证和客户端配置创建COSClient实例
        COSClient cosClient = new COSClient(cred, clientConfig);
        // 返回创建的COSClient实例
        return cosClient;
    }


    /**
     * https://console.cloud.tencent.com/cos
     * <link>https://cloud.tencent.com/document/product/436/10199</link>
     *
     * @param file
     * @param path
     * @return
     */
    @SneakyThrows
    @Override
    public CosUploadVo upload(MultipartFile file, String path) {
        COSClient cosClient = this.getPrivateCOSClient();

        // 元数据信息
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        meta.setContentEncoding("UTF-8");
        meta.setContentType(file.getContentType());

        // 向存储桶中保存文件
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")); // 文件后缀名
        String uploadPath = "/javaproject/drivex/" + path + "/" + UUID.randomUUID().toString().replaceAll("-", "") + fileType;
        PutObjectRequest putObjectRequest = new PutObjectRequest(tencentCloudProperties.getBucketPrivate(), uploadPath, file.getInputStream(), meta);
        putObjectRequest.setStorageClass(StorageClass.Standard);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest); // 上传文件
        log.info(JSON.toJSONString(putObjectResult));
        cosClient.shutdown();

        //图片审核
        Boolean imageAuditing = ciService.imageAuditing(uploadPath);
        if(!imageAuditing) {
            //删除违规图片
            cosClient.deleteObject(tencentCloudProperties.getBucketPrivate(),uploadPath);
            throw new GuiguException(ResultCodeEnum.IMAGE_AUDITION_FAIL);
        }

        // 封装返回对象
        CosUploadVo cosUploadVo = new CosUploadVo();
        cosUploadVo.setUrl(uploadPath);

        //图片临时访问url，回显使用
        cosUploadVo.setShowUrl(this.getImageUrl(uploadPath));

        return cosUploadVo;
    }

    @Override
    public String getImageUrl(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }

        COSClient cosClient = getPrivateCOSClient();
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(
                        tencentCloudProperties.getBucketPrivate(),
                        path,
                        HttpMethodName.GET);
        // 设置临时URL有效期为15分钟
        Date expiration = new DateTime().plusMinutes(15).toDate();
        request.setExpiration(expiration);
        URL url = cosClient.generatePresignedUrl(request);

        cosClient.shutdown();

        return url.toString();
    }
}
