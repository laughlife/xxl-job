package com.xxl.job.executor.biz.token.service;

public interface HubuService {

    /**
     * 获取或刷新 token，领星、虎佈，后面可能还会增加上紫鸟
     * @return
     */
    boolean getOrRefreshToken();
}
