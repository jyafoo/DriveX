package com.atguigu.daijia.dispatch.xxl.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DispatchJobHandler {
    @XxlJob("firstJobHandler1")
    public void firstJobHandler() {
        log.info("xxl-job项目集成测试");
    }
}