package com.xxl.job.executor.config;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * 自定义 Logback Appender，将 log.info() 等日志自动转发到 XXL-JOB 执行日志
 * 只有在 XXL-JOB 任务执行上下文中才会记录
 */
public class XxlJobLogAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        // 只有在 XXL-JOB 任务执行上下文中才记录
        if (XxlJobContext.getXxlJobContext() != null) {
            String message = event.getFormattedMessage();
            XxlJobHelper.log("[{}] {}", event.getLevel(), message);
        }
    }
}
