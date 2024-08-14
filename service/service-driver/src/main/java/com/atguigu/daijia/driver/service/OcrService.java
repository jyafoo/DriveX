package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.driver.IdCardOcrVo;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {


    /**
     * 身份证识别接口
     * 该接口用于处理身份证的光学字符识别（OCR），从上传的身份证图片中提取相关信息
     *
     * @param file 用户上传的身份证图片文件
     * @return OCR识别后的身份证信息
     */
    IdCardOcrVo idCardOcr(MultipartFile file);


}
