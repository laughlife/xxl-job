package com.xxl.job.executor.jobhandler;

import com.xxl.job.executor.biz.amazon.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * OrderJob 测试类
 */
class OrderJobTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderJob orderJob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSyncOrderJobHandler_Success() throws Exception {
        // 模拟订单同步成功
        when(orderService.getAmazonOrderByTime(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(true);

        // 执行任务
        orderJob.syncOrderJobHandler();

        // 验证方法被调用
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
