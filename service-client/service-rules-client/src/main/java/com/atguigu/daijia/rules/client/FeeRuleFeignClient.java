package com.atguigu.daijia.rules.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.rules.FeeRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-rules")
public interface FeeRuleFeignClient {

    /**
     * 计算订单费用
     *
     * @param calculateOrderFeeForm 订单费用计算表单
     * @return 订单费用计算结果
     */
    @PostMapping("/rules/fee/calculateOrderFee")
    Result<FeeRuleResponseVo> calculateOrderFee(@RequestBody FeeRuleRequestForm calculateOrderFeeForm);

}