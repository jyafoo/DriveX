package com.atguigu.daijia.driver.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件到Minio
     *
     * @param file 多部分请求中的文件部分
     * @return 返回上传结果的字符串
     */
    String upload(MultipartFile file);
}
