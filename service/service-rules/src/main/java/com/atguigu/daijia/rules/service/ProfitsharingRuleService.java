package com.atguigu.daijia.rules.service;

import com.atguigu.daijia.model.form.rules.ProfitsharingRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.ProfitsharingRuleResponseVo;

public interface ProfitsharingRuleService {

    /**
     * 计算系统分账费用
     *
     * @param profitsharingRuleRequestForm 分账规则请求表，包含订单金额及分账规则等必要信息
     * @return 各分账方的费用分配详情
     */
    ProfitsharingRuleResponseVo calculateOrderProfitsharingFee(ProfitsharingRuleRequestForm profitsharingRuleRequestForm);
}
