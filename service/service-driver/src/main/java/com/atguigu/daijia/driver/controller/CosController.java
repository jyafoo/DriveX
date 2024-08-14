package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "腾讯云cos上传接口管理")
@RestController
@RequestMapping(value="/cos")
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class CosController {

    private final CosService cosService;

    /**
     * 文件上传接口
     *
     * @param file 待上传的文件，使用MultipartFile类型接收
     * @param path 文件上传的路径
     * @return 返回上传结果，包含上传文件的详细信息
     */
    @Operation(summary = "上传")
    @PostMapping("/upload")
    public Result<CosUploadVo> upload(@RequestPart("file") MultipartFile file, @RequestParam("path") String path) {
        return Result.ok(cosService.upload(file, path));
    }

}

