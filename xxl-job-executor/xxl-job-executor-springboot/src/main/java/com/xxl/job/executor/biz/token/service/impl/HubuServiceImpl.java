package com.xxl.job.executor.biz.token.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.fzzixun.openapi.sdk.client.OpenClient;
import com.fzzixun.openapi.sdk.request.AppTokenRequest;
import com.fzzixun.openapi.sdk.response.AppTokenResponse;
import com.fzzixun.openapi.sdk.response.CommonResponse;
import com.xxl.job.executor.biz.token.entity.TokenDO;
import com.xxl.job.executor.biz.token.mapper.TokenMapper;
import com.xxl.job.executor.biz.token.service.HubuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Validated
public class HubuServiceImpl implements HubuService {
    @Resource
    private TokenMapper tokenMapper;

    @Value("${hubu.apiurl}")
    private String hubuApiUrl;

    @Value("${hubu.appid}")
    private String hubuAppId;

    @Value("${hubu.appsecret}")
    private String hubuAppSecret;

    @Override
    public boolean getOrRefreshToken() {
        List tokens = tokenMapper.selectList("name", "虎佈");
        int count = tokens.size();
        if (count == 0) {
            return fetchAndStoreToken();
        }
        //hierarchy
        TokenDO token = (TokenDO) tokens.get(0);
        long currentTime = System.currentTimeMillis();
        if (currentTime < token.getExpiresTime() - 60 * 60 * 1000) {
            Date date = new Date(token.getExpiresTime());
            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            log.info("虎佈token处于有效期，到期时间为:{}", format);
            return true; // Token 仍然有效
        }
        TokenDO newToken = getTokenByNet();
        log.info("更新虎佈token");
        return newToken != null && tokenMapper.updateByName(newToken) > 0;
    }

    private boolean fetchAndStoreToken() {
        TokenDO token = getTokenByNet();
        return token != null && tokenMapper.insert(token) > 0;
    }

    private TokenDO getTokenByNet() {
        TokenDO token = new TokenDO();
        AppTokenRequest request = new AppTokenRequest();
        OpenClient client = new OpenClient(hubuApiUrl, hubuAppId, hubuAppSecret);
        CommonResponse response = client.execute(request);
        if (response.isSuccess()) {
            // 返回结果
            String data = response.getData();
            JSONObject dj = JSONObject.parseObject(data);
            token.setName("虎佈");

            AppTokenResponse appTokenResponse = response.getDataObj(AppTokenResponse.class);
            token.setAccessToken(appTokenResponse.getAppAuthToken());

            int expireTime = dj.getIntValue("expiresIn");
            long currentTime = System.currentTimeMillis();
            long expiresTime = currentTime + expireTime * 1000;
            token.setSaveTime(currentTime);
            token.setExpiresTime(expiresTime);
        } else {
            log.error("虎佈ERP获取token失败");
            log.error(response.getBody());
            log.error(response.getErrorMsg());
        }
        return token;
    }
}
