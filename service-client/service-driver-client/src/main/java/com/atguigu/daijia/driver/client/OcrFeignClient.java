package com.atguigu.daijia.driver.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.vo.driver.DriverLicenseOcrVo;
import com.atguigu.daijia.model.vo.driver.IdCardOcrVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "service-driver")
public interface OcrFeignClient {

    /**
     * 如果服务接口需要支持文件上传功能，
     * 则必须指定consumes = MediaType.MULTIPART_FORM_DATA_VALUE来确保正确解析客户端发送的文件数据。
     *
     * 在远程调用端配置consumes可以确保Feign知道如何处理multipart/form-data类型的请求体。
     *
     */

    /**
     * 身份证识别
     * @param file
     * @return
     */
    @PostMapping(value = "/ocr/idCardOcr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<IdCardOcrVo> idCardOcr(@RequestPart("file") MultipartFile file);

    /**
     * 驾驶证识别
     * @param file
     * @return
     */
    @PostMapping(value = "/ocr/driverLicenseOcr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<DriverLicenseOcrVo> driverLicenseOcr(@RequestPart("file") MultipartFile file);

}