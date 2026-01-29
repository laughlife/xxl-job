package com.xxl.job.executor.jobhandler;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.xxl.job.executor.biz.amazon.service.OrderService;

/**
 * OrderJob 集成测试类
 * 使用 Mockito 进行测试
 */
@ExtendWith(MockitoExtension.class)
class OrderJobIntegrationTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderJob orderJob;

    @Test
    void testSyncOrderJobHandler_Success() throws Exception {
        // 模拟订单同步成功
        when(orderService.getAmazonOrderByTime(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(true);

        // 执行任务
        orderJob.syncOrderJobHandler();

        // 验证方法被调用，参数为昨天到今天
        verify(orderService, times(1))
                .getAmazonOrderByTime(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testSyncOrderJobHandler_Failure() throws Exception {
        // 模拟订单同步失败
        when(orderService.getAmazonOrderByTime(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(false);

        // 执行任务
        orderJob.syncOrderJobHandler();

        // 验证方法被调用
        verify(orderService, times(1))
                .getAmazonOrderByTime(any(LocalDate.class), any(LocalDate.class));
    }
}
