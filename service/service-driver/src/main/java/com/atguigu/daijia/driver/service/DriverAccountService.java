package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.entity.driver.DriverAccount;
import com.atguigu.daijia.model.form.driver.TransferForm;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DriverAccountService extends IService<DriverAccount> {


    /**
     * 执行账户间的转账操作。
     *
     * @param transferForm 包含转账信息的表单，包括源账户、目标账户和转账金额。
     * @return 返回一个结果对象，其中包含一个布尔值，指示转账是否成功。
     */
    Boolean transfer(TransferForm transferForm);
}
