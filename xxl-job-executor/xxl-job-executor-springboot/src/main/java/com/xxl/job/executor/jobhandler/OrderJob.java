package com.xxl.job.executor.jobhandler;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.executor.biz.amazon.service.OrderService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class OrderJob {
    
    @Resource
    OrderService orderService;
    

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("syncOrderJobHandler")
    public void syncOrderJobHandler() throws Exception {
        XxlJobHelper.log("XXL-JOB, 开始同步订单信息.");
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);

        boolean syncStatus = orderService.getAmazonOrderByTime(startDate,endDate);
        if(syncStatus){
            XxlJobHelper.log("syncOrderJobHandler, 订单同步成功.");
            log.info("订单同步成功。");
        }
        XxlJobHelper.handleFail("syncOrderJobHandler,订单处理失败。");
        log.info("订单同步失败。");
    }

}
