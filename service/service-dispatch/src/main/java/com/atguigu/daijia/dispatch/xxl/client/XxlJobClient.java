package com.atguigu.daijia.dispatch.xxl.client;

import com.alibaba.fastjson2.JSONObject;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.dispatch.xxl.config.XxlJobClientConfig;
import com.atguigu.daijia.model.entity.dispatch.XxlJobInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author jyafoo
 * @version 1.0
 * @since 2024/8/20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XxlJobClient {
    private final XxlJobClientConfig xxlJobClientConfig;


    private final RestTemplate restTemplate;

    @SneakyThrows
    public Long addJob(String executorHandler, String param, String corn, String desc){
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setJobGroup(xxlJobClientConfig.getJobGroupId());
        xxlJobInfo.setJobDesc(desc);
        xxlJobInfo.setAuthor("qy");
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(corn);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(executorHandler);
        xxlJobInfo.setExecutorParam(param);
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setMisfireStrategy("FIRE_ONCE_NOW");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getAddUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            log.info("增加xxl执行任务成功,返回信息:{}", response.getBody().toJSONString());
            //content为任务id
            return response.getBody().getLong("content");
        }
        log.info("调用xxl增加执行任务失败:{}", response.getBody().toJSONString());
        throw new GuiguException(ResultCodeEnum.DATA_ERROR);
    }

    public Boolean startJob(Long jobId) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getStartJobUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            log.info("启动xxl执行任务成功:{},返回信息:{}", jobId, response.getBody().toJSONString());
            return true;
        }
        log.info("启动xxl执行任务失败:{},返回信息:{}", jobId, response.getBody().toJSONString());
        throw new GuiguException(ResultCodeEnum.DATA_ERROR);
    }

    public Boolean stopJob(Long jobId) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getStopJobUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            log.info("停止xxl执行任务成功:{},返回信息:{}", jobId, response.getBody().toJSONString());
            return true;
        }
        log.info("停止xxl执行任务失败:{},返回信息:{}", jobId, response.getBody().toJSONString());
        throw new GuiguException(ResultCodeEnum.DATA_ERROR);
    }

    public Boolean removeJob(Long jobId) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getRemoveUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            log.info("删除xxl执行任务成功:{},返回信息:{}", jobId, response.getBody().toJSONString());
            return true;
        }
        log.info("删除xxl执行任务失败:{},返回信息:{}", jobId, response.getBody().toJSONString());
        throw new GuiguException(ResultCodeEnum.DATA_ERROR);
    }

    /**
     * 添加并启动定时任务
     *
     * @param executorHandler 任务处理器
     * @param param 任务参数
     * @param corn 定时表达式
     * @param desc 任务描述
     * @return 任务ID
     * @throws GuiguException 如果添加并启动任务失败
     */
    public Long addAndStart(String executorHandler, String param, String corn, String desc) {
        // 创建一个新的任务信息对象
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        // 设置任务所属组ID
        xxlJobInfo.setJobGroup(xxlJobClientConfig.getJobGroupId());
        // 设置任务描述
        xxlJobInfo.setJobDesc(desc);
        // 设置任务作者
        xxlJobInfo.setAuthor("qy");
        // 设置调度类型为CRON
        xxlJobInfo.setScheduleType("CRON");
        // 设置定时表达式
        xxlJobInfo.setScheduleConf(corn);
        // 设置任务类型为BEAN
        xxlJobInfo.setGlueType("BEAN");
        // 设置任务处理器
        xxlJobInfo.setExecutorHandler(executorHandler);
        // 设置任务参数
        xxlJobInfo.setExecutorParam(param);
        // 设置任务路由策略为第一个
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        // 设置任务阻塞处理策略为串行执行
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        // 设置触发失败策略为立即触发一次
        xxlJobInfo.setMisfireStrategy("FIRE_ONCE_NOW");
        // 设置任务执行超时时间，默认为0，表示不限制
        xxlJobInfo.setExecutorTimeout(0);
        // 设置任务执行失败重试次数，默认为0，表示不重试
        xxlJobInfo.setExecutorFailRetryCount(0);

        // 创建HTTP请求头
        HttpHeaders headers = new HttpHeaders();
        // 设置请求体类型为JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 创建HTTP请求实体
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        // 构建请求URL
        String url = xxlJobClientConfig.getAddAndStartUrl();
        // 发送HTTP请求，添加并启动任务
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        // 判断请求是否成功，且服务端返回的代码表示成功
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            // 日志记录成功信息
            log.info("增加并开始执行xxl任务成功,返回信息:{}", response.getBody().toJSONString());
            // 返回任务ID
            return response.getBody().getLong("content");
        }
        // 日志记录失败信息
        log.info("增加并开始执行xxl任务失败:{}", response.getBody().toJSONString());
        // 抛出异常表示添加并启动任务失败
        throw new GuiguException(ResultCodeEnum.DATA_ERROR);
    }

}
