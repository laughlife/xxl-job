package com.xxl.job.executor.biz.amazon.service.impl;

import com.xxl.job.executor.biz.amazon.entity.OrderProductExtDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderProductExtMapper;
import com.xxl.job.executor.biz.amazon.service.OrderProductExtService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 亚马逊订单商品扩展(低频字段+完整JSON，按快照存档) 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
public class OrderProductExtServiceImpl extends ServiceImpl<OrderProductExtMapper, OrderProductExtDO> implements OrderProductExtService {

}
