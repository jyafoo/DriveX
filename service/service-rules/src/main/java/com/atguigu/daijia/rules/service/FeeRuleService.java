package com.atguigu.daijia.rules.service;

import com.atguigu.daijia.model.form.rules.FeeRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponseVo;

public interface FeeRuleService {

    /**
     * 计算订单费用
     *
     * @param calculateOrderFeeForm 订单费用计算表单
     * @return 订单费用计算结果
     */
    FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm calculateOrderFeeForm);
}
