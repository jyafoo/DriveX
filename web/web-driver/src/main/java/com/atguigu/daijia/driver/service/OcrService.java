package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.driver.DriverLicenseOcrVo;
import com.atguigu.daijia.model.vo.driver.IdCardOcrVo;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {


    /**
     * 通过OCR技术识别并提取身份证上的信息
     *
     * @param file 上传的身份证图片文件
     * @return 返回识别结果，包含身份证上的相关信息
     */
    IdCardOcrVo idCardOcr(MultipartFile file);

    /**
     * 驾驶证识别接口
     *
     * @param file 驾驶证图片文件，通过multipart/form-data方式上传
     * @return 识别到的相关信息
     */
    DriverLicenseOcrVo driverLicenseOcr(MultipartFile file);
}
