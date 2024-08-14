package com.atguigu.daijia.driver.service.impl;

import com.alibaba.fastjson2.JSON;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.client.CosFeignClient;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosServiceImpl implements CosService {

    private final CosFeignClient cosFeignClient;

    @Override
    public CosUploadVo uploadFile(MultipartFile file, String path) {
        return cosFeignClient.upload(file,path).getData();
    }
}
