package com.xxl.job.executor.biz.amazon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.amazon.entity.OrderProductDO;

/**
 * 亚马逊订单商品 服务类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
public interface OrderProductService extends IService<OrderProductDO> {

    /**
     * 根据订单号和商品ID查询
     */
    OrderProductDO getByOrderIdAndSourceItemId(String amazonOrderId, Long sourceItemId);
}
