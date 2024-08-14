package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.service.OcrService;
import com.atguigu.daijia.model.vo.driver.DriverLicenseOcrVo;
import com.atguigu.daijia.model.vo.driver.IdCardOcrVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "腾讯云识别接口管理")
@RestController
@RequiredArgsConstructor
@RequestMapping(value="/ocr")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OcrController {

    private final OcrService ocrService;

    /**
     * 通过OCR技术识别并提取身份证上的信息
     *
     * @param file 上传的身份证图片文件
     * @return 返回识别结果，包含身份证上的相关信息
     */
    @Operation(summary = "身份证识别")
    // @LoginCheck
    @PostMapping("/idCardOcr")
    public Result<IdCardOcrVo> uploadDriverLicenseOcr(@RequestPart("file") MultipartFile file) {
        return Result.ok(ocrService.idCardOcr(file));
    }

    /**
     * 驾驶证识别接口
     *
     * @param file 驾驶证图片文件，通过multipart/form-data方式上传
     * @return 识别到的相关信息
     */
    @Operation(summary = "驾驶证识别")
    // @LoginCheck
    @PostMapping("/driverLicenseOcr")
    public Result<DriverLicenseOcrVo> driverLicenseOcr(@RequestPart("file") MultipartFile file) {
        return Result.ok(ocrService.driverLicenseOcr(file));
    }

}

