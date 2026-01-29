package com.xxl.job.executor.biz.amazon.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.amazon.entity.OrderProductExtDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderProductExtMapper;
import com.xxl.job.executor.biz.amazon.service.OrderProductExtService;

/**
 * 亚马逊订单商品扩展 服务实现类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
public class OrderProductExtServiceImpl extends ServiceImpl<OrderProductExtMapper, OrderProductExtDO> implements OrderProductExtService {

    @Override
    public OrderProductExtDO getByOrderIdAndSourceItemId(String amazonOrderId, Long sourceItemId) {
        return getOne(new LambdaQueryWrapper<OrderProductExtDO>()
                .eq(OrderProductExtDO::getAmazonOrderId, amazonOrderId)
                .eq(OrderProductExtDO::getSourceItemId, sourceItemId));
    }
}
