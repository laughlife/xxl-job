package com.xxl.job.executor.biz.sellfox.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
public class SellfoxConfig {

    public static final String SELLFOX_NAME = "赛狐";

    // 赛狐API相关信息
    public static final String clientId = "368027";

    public static final String clientSecret = "2951eb88-f581-48cf-8b8b-8d5c40e37e49";

    public static final String API_BASE_URL = "https://openapi.sellfox.com";

    public static final String API_MAIN_BASE_URL = "https://www.sellfox.com";

    // 同步产品列表
    public static final String getProductUrl = "/api/commodity/pageList.json";

    // 同步赛狐用户信息
    public static final String getSellfoxUserUrl = "/api/account/getSubUserPage.json";

    // 同步产品分类
    public static final String getProductTypeUrl = "/api/category/getList.json";

    //获取供应商列表
    public static final String getSupplierUrl = "/api/supplier/pageList.json";

    //添加或修改供应商
    public static final String addOrUpdateSupplierUrl = "/api/supplier/createOrUpdate.json";
    // 采购订单创建
    public static final String createPurchaseUrl = "/api/purchase/create.json";

    //查询辅料
    public static final String getMaterialUrl = "/api/commodity/aux/pageList.json";

    // 获取仓库列表
    public static final String getWarehouseUrl = "/api/warehouseManage/warehouseList.json";
    // 获取发货单列表
    public static final String getShippingOrderUrl = "/api/fba/shippingOrder/pageList.json";

    // 获取仓库订单列表
    public static final String getWarehousePurchaseUrl = "/api/purchase/page.json";

    // 获取装箱信息列表
    public static final String getPackingInfoUrl = "/api/packing/task/page.json";

    // 获取订单列表
    public static final String getOrderListUrl = "/api/order/pageList.json";

    public static final String getTrackingInfoUrl = "/api/fbaShippingOrderTracking/pageList.json";

    public static String getShippingOrderTrackUrl = "/api/fbaShippingOrderTracking/getShippingOrderTrack.json";

    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final String SELLFOX_SITE_NAME = "sellfox";

    private static final OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
    public static String getShopUrl = "/shop/getAllShopSite.json";
    public static String updateFreightUrl = "/api/orderLogisticsProfit/updateFreight.json";
    public static String updateWeightUrl = "/api/orderLogisticsProfit/updateWeight.json";

    /**
     * 生成签名主方法
     *
     * @return
     */
    public static String genarateSign(Map<String, Object> params) throws Exception {
        // 参数排序
        String data = params.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .sorted()
                .collect(Collectors.joining("&"));
        // HmacSHA256签名, 【密钥】(需要填写跟clientId配对的密钥)
        log.info("sign-url：{}", data);
        return hmacsha256(clientSecret, data);
    }

    /**
     * HmacSHA256签名
     *
     * @param key  密钥 (需要填写跟clientId配对的密钥)
     * @param data 被签名字符串
     */
    public static String hmacsha256(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secret_key);
        return new String(Hex.encodeHex(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * 构建带签名的 URL（POST）
     */
    public static HttpUrl buildSignedUrl(String apiPath, String accessToken) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonce = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 99999));

        Map<String, Object> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("client_id", clientId);
        params.put("timestamp", timestamp);
        params.put("method", "post");
        params.put("nonce", nonce);
        params.put("url", apiPath);

        String sign;
        try {
            sign = genarateSign(params);
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }

        return Objects.requireNonNull(HttpUrl.parse(SellfoxConfig.API_BASE_URL + apiPath))
                .newBuilder()
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("timestamp", timestamp)
                .addQueryParameter("nonce", nonce)
                .addQueryParameter("sign", sign)
                .build();
    }

    public static HttpUrl buildRequestUrl(String apiPath) {
        return Objects.requireNonNull(HttpUrl.parse(SellfoxConfig.API_MAIN_BASE_URL + apiPath)).newBuilder().build();
    }

    /**
     * POST JSON 并返回响应文本
     */
    public static String postJson(HttpUrl url, JSONObject body) throws IOException {
        log.info("POST: {}", url);
        log.info("POST-BODY: {}", body.toJSONString());
        Request request = new Request.Builder().url(url)
                .post(RequestBody.create(body.toJSONString(), SellfoxConfig.JSON_TYPE))
                .header("Content-Type", "application/json")
                .build();

        try (Response resp = httpClient.newCall(request).execute()) {
            log.info("POST-RESP: {}", resp);
            if (!resp.isSuccessful()) {
                log.error("调用赛狐接口失败，HTTP={}，url={}", resp.code(), url);
            }
            ResponseBody rb = resp.body();
            return rb != null ? rb.string() : "";
        }
    }

    public static String postJsonWithCookie(HttpUrl url, JSONObject body, String cookie) throws IOException {
        log.info("POST: {}", url);
        if (body != null) {
            log.info("POST-BODY: {}", body.toJSONString());
        } else {
            log.info("POST-BODY: <empty>");
        }

        // 将 body 参数添加到 URL 中
        HttpUrl.Builder urlBuilder = url.newBuilder();
        if (body != null) {
            // 将 JSON 对象的键值对添加为 URL 查询参数
            for (String key : body.keySet()) {
                Object value = body.get(key);
                if (value != null) {
                    urlBuilder.addQueryParameter(key, value.toString());
                }
            }
        }

        // 创建不包含请求体的 POST 请求
        Request request = new Request.Builder()
                .url(urlBuilder.build())  // 使用修改后的 URL
                .post(RequestBody.create("", JSON_TYPE))  // 空的请求体
                .addHeader("Cookie", cookie)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response resp = httpClient.newCall(request).execute()) {
            log.info("POST-RESP: {}", resp);

            if (!resp.isSuccessful()) {
                log.error("调用主赛狐接口失败，HTTP={}，url={}", resp.code(), urlBuilder.build());
            }
            ResponseBody rb = resp.body();
            return rb != null ? rb.string() : "";
        }
    }

    public static String getJsonWithCookie(HttpUrl baseUrl, JSONObject params, String cookie) throws IOException {
        log.info("GET: {}", baseUrl);
        if (params != null) {
            log.info("GET-PARAMS: {}", params.toJSONString());
        } else {
            log.info("GET-PARAMS: <empty>");
        }

        // 构建带参数的URL
        HttpUrl.Builder urlBuilder = baseUrl.newBuilder();
        if (params != null) {
            // 将JSON对象的键值对添加为URL查询参数
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value != null) {
                    urlBuilder.addQueryParameter(key, value.toString());
                }
            }
        }

        HttpUrl url = urlBuilder.build();

        // 创建GET请求
        Request request = new Request.Builder()
                .url(url)
                .get()  // 明确指定GET请求
                .addHeader("Cookie", cookie)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response resp = httpClient.newCall(request).execute()) {
            log.info("GET-RESP: {}", resp);

            if (!resp.isSuccessful()) {
                log.error("调用API失败，HTTP={}，url={}", resp.code(), url);
            }

            ResponseBody rb = resp.body();
            return rb != null ? rb.string() : "";
        }
    }

}
