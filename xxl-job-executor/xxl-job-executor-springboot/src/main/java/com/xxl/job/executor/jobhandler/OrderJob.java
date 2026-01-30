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
     * 同步订单任务
     * 参数说明：
     * - "2天" 或 "2" : 同步最近2天的订单
     * - "30天" 或 "30" : 同步最近30天的订单
     * - 无参数或其他值 : 默认同步最近2天的订单
     */
    @XxlJob("syncOrderJobHandler")
    public void syncOrderJobHandler() throws Exception {
        log.info("XXL-JOB, 开始同步订单信息.");
        // 获取任务参数
        String jobParam = XxlJobHelper.getJobParam();
        
        // 根据参数确定天数，默认为2天
        int days = 2;
        if (jobParam != null && !jobParam.trim().isEmpty()) {
            String param = jobParam.trim();
            if ("30".equals(param) || "30天".equals(param)) {
                days = 30;
                log.info("接收到参数: {}, 同步最近30天的订单", jobParam);
            } else if ("2".equals(param) || "2天".equals(param)) {
                days = 2;
                log.info("接收到参数: {}, 同步最近2天的订单", jobParam);
            } else {
                log.info("接收到未识别参数: {}, 使用默认值同步最近2天的订单", jobParam);
            }
        } else {
            log.info("未接收到参数, 使用默认值同步最近2天的订单");
        }
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        log.info("同步时间范围: {} 至 {}", startDate, endDate);

        boolean syncStatus = orderService.getAmazonOrderByTime(startDate, endDate);
        if (syncStatus) {
            log.info("syncOrderJobHandler, 订单同步成功.");
        } else {
            XxlJobHelper.handleFail("syncOrderJobHandler, 订单处理失败。");
            log.info("订单同步失败。");
        }
    }

}
