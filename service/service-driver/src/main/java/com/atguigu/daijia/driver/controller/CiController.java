package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.service.CiService;
import com.atguigu.daijia.model.vo.order.TextAuditingVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "腾讯云CI审核接口管理")
@RestController
@RequestMapping(value="/cos")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CiController {


    private CiService ciService;

    /**
     * 文本审核接口
     * 该接口用于对给定的文本内容进行审核，以检测其中是否包含敏感或违规信息
     *
     * @param content 待审核的文本内容
     * @return 返回审核结果，包括是否通过审核及审核详情
     */
    @Operation(summary = "文本审核")
    @PostMapping("/textAuditing")
    public Result<TextAuditingVo> textAuditing(@RequestBody String content) {
        return Result.ok(ciService.textAuditing(content));
    }

}

