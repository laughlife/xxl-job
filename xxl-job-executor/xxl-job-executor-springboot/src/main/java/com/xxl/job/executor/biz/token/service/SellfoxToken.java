package com.xxl.job.executor.biz.token.service;

public interface SellfoxToken {
    /**
     * 获取或刷新 赛狐 令牌
     */
    boolean getOrRefreshToken();
}
