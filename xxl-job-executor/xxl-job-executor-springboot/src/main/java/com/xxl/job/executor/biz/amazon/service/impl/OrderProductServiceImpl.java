package com.xxl.job.executor.biz.amazon.service.impl;

import com.xxl.job.executor.biz.amazon.entity.OrderProductDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderProductMapper;
import com.xxl.job.executor.biz.amazon.service.OrderProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 亚马逊订单商品(高频字段，按订单快照存档) 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
public class OrderProductServiceImpl extends ServiceImpl<OrderProductMapper, OrderProductDO> implements OrderProductService {

}
