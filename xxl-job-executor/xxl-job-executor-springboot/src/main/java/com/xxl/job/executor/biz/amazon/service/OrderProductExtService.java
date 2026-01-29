package com.xxl.job.executor.biz.amazon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.amazon.entity.OrderProductExtDO;

/**
 * 亚马逊订单商品扩展 服务类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
public interface OrderProductExtService extends IService<OrderProductExtDO> {

    /**
     * 根据订单号和商品ID查询
     */
    OrderProductExtDO getByOrderIdAndSourceItemId(String amazonOrderId, Long sourceItemId);
}
