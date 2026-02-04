package com.xxl.job.executor.biz.sellfox.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.sellfox.config.SellfoxConfig;
import com.xxl.job.executor.biz.sellfox.entity.SellfoxUserDO;
import com.xxl.job.executor.biz.sellfox.mapper.SellfoxUserMapper;
import com.xxl.job.executor.biz.sellfox.service.SellfoxUserService;
import com.xxl.job.executor.biz.token.entity.TokenDO;
import com.xxl.job.executor.biz.token.mapper.TokenMapper;
import com.xxl.job.executor.biz.token.service.SellfoxToken;
import com.xxl.job.executor.util.BeanUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;

/**
 * <p>
 * 赛狐用户表 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
@Slf4j
@Service
public class SellfoxUserServiceImpl extends ServiceImpl<SellfoxUserMapper, SellfoxUserDO> implements SellfoxUserService {

    @Resource
    private SellfoxUserMapper sellfoxUserMapper;

    @Resource
    SellfoxToken sellfoxToken;

    @Resource
    private TokenMapper tokenMapper;


    @Override
    public String syncSellfoxUser() {
        // 首先清空表
        sellfoxUserMapper.truncateTable();

        String accessToken = getSellfoxAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return "同步赛狐用户失败：未获取到 accessToken";
        }

        int totalCount = 0;
        int pageNo = 1, pageSize = 100;
        while (true) {
            JSONObject req = new JSONObject();
            req.put("pageNo", pageNo);
            req.put("pageSize", pageSize);
            req.put("state", 1);

            BatchResult result = fetchAndPersistSellfoxUser(accessToken, req, SellfoxConfig.getSellfoxUserUrl);
            totalCount += result.count();
            if (!result.hasNext()) break;

            pageNo++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        return String.format("同步赛狐用户成功，共同步 %d 条", totalCount);
    }

    private record BatchResult(int count, boolean hasNext) {}

    private BatchResult fetchAndPersistSellfoxUser(String accessToken, JSONObject req, String request_url) {
        HttpUrl url = SellfoxConfig.buildSignedUrl(request_url, accessToken);
        try {
            String respBody = SellfoxConfig.postJson(url, req);
            return analyzeResponse(respBody);
        } catch (IOException e) {
            log.error("赛狐接口异常，url={}", url, e);
            return new BatchResult(0, false);
        }
    }

    private BatchResult analyzeResponse(String respBody) {
        if (StringUtils.isBlank(respBody)) return new BatchResult(0, false);

        JSONObject resp = JSONObject.parseObject(respBody);
        String code = resp.getString("code");
        String msg = resp.getString("msg");
        JSONObject data = resp.getJSONObject("data");
        if (!"0".equals(code)) {
            log.warn("赛狐返回非成功：code={}, msg={}", code, msg);
            return new BatchResult(0, false);
        }

        if (data == null) return new BatchResult(0, false);

        JSONArray rows = data.getJSONArray("rows");
        int count = (rows != null) ? rows.size() : 0;
        if (rows != null) {
            rows.forEach(row -> {
                JSONObject rowObj = (JSONObject) row;
                SellfoxUserDO sellfoxUser = BeanUtils.toBean(rowObj, SellfoxUserDO.class);

                // 解析 roleIds
                JSONArray roleIdsArr = rowObj.getJSONArray("roleIds");
                JSONArray roleNamesArr = rowObj.getJSONArray("roleNames");

                // 转换为用分号分隔的字符串
                String roleIds = roleIdsArr != null ? roleIdsArr.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(";")) : "";
                String roleNames = roleNamesArr != null ? roleNamesArr.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(";")) : "";

                sellfoxUser.setRoleIds(roleIds);
                sellfoxUser.setRoleNames(roleNames);

                sellfoxUserMapper.insert(sellfoxUser);
            });
        }

        int pageNo = data.getIntValue("pageNo");
        int totalPage = data.getIntValue("totalPage");
        return new BatchResult(count, pageNo < totalPage);
    }


    private String getSellfoxAccessToken() {
        List<TokenDO> tokenDOList = tokenMapper.selectList("name", "赛狐");
        if (tokenDOList == null || tokenDOList.isEmpty()) return "";
        TokenDO token = tokenDOList.get(0);
        if (token.getExpiresTime() < System.currentTimeMillis()) {
            sellfoxToken.getOrRefreshToken();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return getSellfoxAccessToken();
        }
        return StringUtils.defaultString(token.getAccessToken());
    }
}
