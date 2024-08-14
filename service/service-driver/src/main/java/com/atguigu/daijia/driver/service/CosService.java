package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface CosService {


    /**
     * 文件上传接口
     *
     * @param file 待上传的文件，使用MultipartFile类型接收
     * @param path 文件上传的路径
     * @return 返回上传结果，包含上传文件的详细信息
     */
    CosUploadVo upload(MultipartFile file, String path);


    /**
     * 获取图片的回顯路径
     *
     * @param path 图片的文件路径
     * @return 图片的URL路径
     */
    String getImageUrl(String path);
}
