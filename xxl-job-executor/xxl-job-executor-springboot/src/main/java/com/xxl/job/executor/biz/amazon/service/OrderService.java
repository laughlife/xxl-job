package com.xxl.job.executor.biz.amazon.service;

import java.time.LocalDate;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.amazon.entity.OrderDO;

/**
 * <p>
 * 亚马逊订单(最新快照，高频查询字段) 服务类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
public interface OrderService extends IService<OrderDO> {

    /**
     * 根据开始时间和截止时间，获取指定时间的订单数据
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @return 同步结果
     */
    boolean getAmazonOrderByTime(LocalDate startDate,LocalDate endDate);
}
