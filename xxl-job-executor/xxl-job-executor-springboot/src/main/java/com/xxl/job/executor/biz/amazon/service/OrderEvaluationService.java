package com.xxl.job.executor.biz.amazon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.amazon.entity.OrderEvaluationDO;

/**
 * 亚马逊订单评价 服务接口
 *
 * @author Li Wei
 * @since 2026-01-29
 */
public interface OrderEvaluationService extends IService<OrderEvaluationDO> {

    /**
     * 根据订单号查询评价
     */
    OrderEvaluationDO getByAmazonOrderId(String amazonOrderId);

    /**
     * 保存或更新评价
     */
    void saveOrUpdateByOrderId(OrderEvaluationDO evaluation);
}
