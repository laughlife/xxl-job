package com.xxl.job.executor.biz.amazon.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.amazon.entity.OrderProductDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderProductMapper;
import com.xxl.job.executor.biz.amazon.service.OrderProductService;

/**
 * 亚马逊订单商品 服务实现类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
public class OrderProductServiceImpl extends ServiceImpl<OrderProductMapper, OrderProductDO> implements OrderProductService {

    @Override
    public OrderProductDO getByOrderIdAndSourceItemId(String amazonOrderId, Long sourceItemId) {
        return getOne(new LambdaQueryWrapper<OrderProductDO>()
                .eq(OrderProductDO::getAmazonOrderId, amazonOrderId)
                .eq(OrderProductDO::getSourceItemId, sourceItemId));
    }
}
