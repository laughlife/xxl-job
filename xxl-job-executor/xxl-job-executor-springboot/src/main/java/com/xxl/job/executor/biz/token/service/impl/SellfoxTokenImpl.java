package com.xxl.job.executor.biz.token.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xxl.job.executor.biz.token.entity.TokenDO;
import com.xxl.job.executor.biz.token.mapper.TokenMapper;
import com.xxl.job.executor.biz.token.service.SellfoxToken;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class SellfoxTokenImpl implements SellfoxToken {


    @Value("${sellfox.clientId}")
    private String clientId;

    @Value("${sellfox.clientSecret}")
    private String clientSecret;

    @Value("${sellfox.getTokenUrl}")
    private String getTokenUrl;

    @Resource
    private TokenMapper tokenMapper;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    @Override
    public boolean getOrRefreshToken() {
        TokenDO tokenDO = tokenMapper.selectOne(TokenDO::getName, "赛狐");
        //查找token信息，如果没有，就获取相对应的token信息
        if (tokenDO == null) {
            tokenDO = new TokenDO();
            tokenDO.setName("赛狐");
            initToken(tokenDO);
        }
        if (tokenDO.getAccessToken() == null) {
            // 获取token
            initToken(tokenDO);
        }
        // 如果token已过期，或者20分钟内已过期，就重新获取
        if (tokenDO.getExpiresTime() == null || tokenDO.getExpiresTime() < System.currentTimeMillis() + 20 * 60 * 1000) {
            initToken(tokenDO);
        }
        return true;
    }


    private void initToken(TokenDO tokenDO) {
        // 构建带参数且自动编码的 URL
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(getTokenUrl))
                .newBuilder()
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("client_secret", clientSecret)
                .addQueryParameter("grant_type", "client_credentials") // 按文档固定值
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求赛狐 Token 接口失败，HTTP {}", response.code());
                return;
            }
            String bodyString = Objects.requireNonNull(response.body()).string();
            System.out.println(bodyString);
            JSONObject body = JSON.parseObject(bodyString);
            if (body.getInteger("code") != 0) {
                log.error("请求赛狐 Token 错误，错误信息：{}", body.getString("msg"));
                return;
            }
            JSONObject data = body.getJSONObject("data");
            Long nowTime = System.currentTimeMillis();
            //封装tokenDO
            tokenDO.setAccessToken(data.getString("access_token"));
            tokenDO.setExpiresTime(nowTime + data.getLong("expires_in"));
            tokenDO.setSaveTime(nowTime);
            tokenMapper.insertOrUpdate(tokenDO);
        } catch (IOException e) {
            log.error("请求赛狐 Token 接口异常", e);
        }
    }
}
