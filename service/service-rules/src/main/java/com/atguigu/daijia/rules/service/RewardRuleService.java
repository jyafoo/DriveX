package com.atguigu.daijia.rules.service;

import com.atguigu.daijia.model.form.rules.RewardRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.RewardRuleResponseVo;

public interface RewardRuleService {

    /**
     * 计算订单奖励费用
     *
     * @param rewardRuleRequestForm 奖励规则请求表单，包含计算奖励费用所需的所有参数
     * @return 奖励费用等信息
     */
    RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm rewardRuleRequestForm);
}
