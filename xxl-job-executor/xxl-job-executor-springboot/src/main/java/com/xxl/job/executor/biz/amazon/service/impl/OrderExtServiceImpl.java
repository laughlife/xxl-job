package com.xxl.job.executor.biz.amazon.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.amazon.entity.OrderExtDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderExtMapper;
import com.xxl.job.executor.biz.amazon.service.OrderExtService;

/**
 * 亚马逊订单扩展 服务实现类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
public class OrderExtServiceImpl extends ServiceImpl<OrderExtMapper, OrderExtDO> implements OrderExtService {

    @Override
    public OrderExtDO getByAmazonOrderId(String amazonOrderId) {
        return getOne(new LambdaQueryWrapper<OrderExtDO>()
                .eq(OrderExtDO::getAmazonOrderId, amazonOrderId));
    }
}
