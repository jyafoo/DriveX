package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.driver.IdCardOcrVo;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {


    IdCardOcrVo idCardOcr(MultipartFile file);
}
