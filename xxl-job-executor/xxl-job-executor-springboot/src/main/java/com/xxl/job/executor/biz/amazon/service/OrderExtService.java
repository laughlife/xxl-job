package com.xxl.job.executor.biz.amazon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.amazon.entity.OrderExtDO;

/**
 * 亚马逊订单扩展 服务类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
public interface OrderExtService extends IService<OrderExtDO> {

    /**
     * 根据订单号查询
     */
    OrderExtDO getByAmazonOrderId(String amazonOrderId);
}
