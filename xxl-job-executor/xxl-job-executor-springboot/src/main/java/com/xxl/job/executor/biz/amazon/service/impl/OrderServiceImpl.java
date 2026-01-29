package com.xxl.job.executor.biz.amazon.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.amazon.entity.OrderDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderMapper;
import com.xxl.job.executor.biz.amazon.service.OrderService;
import com.xxl.job.executor.biz.cookie.entity.SiteCookiesDO;
import com.xxl.job.executor.biz.cookie.service.SiteCookiesService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * <p>
 * 亚马逊订单(最新快照，高频查询字段) 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDO> implements OrderService {

    public final String getOrderUrl = "https://www.sellfox.com/api/order/pageList.json";
    private static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    @Resource
    private SiteCookiesService siteCookiesService;

    public JSONObject getOrderParams(){
        return new JSONObject() {{
            put("dateType", "purchase");
            put("dateStart", "2025-12-21");
            put("dateEnd", "2026-01-21");
            put("searchType", "amazonOrderId");
            put("searchContent", "");
            put("searchMode", "exact");
            put("purchaseCostAndFee", 1);
            put("pageSize", 200);
            put("pageNo", 1);
            put("isHistory", 0);
            put("devIds", "");
            put("refundOrder", "");
            put("returnOrder", "");
            put("replaceOrder", "");
            put("promotion", "");
            put("evaluation", "");
            put("hasEvaluationCost", "");
            put("removeOrder", "");
            put("fulfillmentOrder", "");
            put("isBusinessOrder", "");
            put("evaluationIds", "");
            put("uploadFeedStatus", "");
            put("orderReviewStatus", "");
            put("commentColor", "");
            put("unlimitedTime", false);
            put("minSaleNum", "");
            put("maxSaleNum", "");
            put("isNegative", 0);
            put("orderBy", "purchase");
            put("desc", 0);
            put("searchMoreList", null);
        }};
    }

    @Override
    public boolean getAmazonOrderByTime(LocalDate startDate,LocalDate endDate) {
        JSONObject params = getOrderParams();
        params.put("dateStart", startDate.toString());
        params.put("dateEnd", endDate.toString());
        log.info("开始执行 sellfox 查询订单, 查询时间范围为: {} ~ {}", params.get("dateStart"), params.get("dateEnd"));
        SiteCookiesDO siteCookiesDO = siteCookiesService.getSiteCookiesByName("sellfox");
        String cookies = siteCookiesDO.getCookies();
        while (true) {
            JSONObject result = JSONObject.parseObject(getSellFoxData(getOrderUrl, params, cookies));
            String code = result.getString("code");
            if (StringUtils.isNotBlank(code) && code.equals("0")) {
                JSONObject data = result.getJSONObject("data");

                int pageNo = data.getIntValue("pageNo");
                int totalPage = data.getIntValue("totalPage");

                log.info("获取共计 {} 页数据，当前正在处理第 {} 页", totalPage, pageNo);
                // 这里数据解析出来就直接去处理就行，这里查询比较慢，可以在查询的空隙去处理这200条数据
                JSONArray list = data.getJSONArray("rows");

                 saveQuerySellfoxOrder(list);

                // 判断是否继续下一页
                if (pageNo < totalPage) {
                    params.put("pageNo", pageNo + 1);
                } else {
                    break;
                }
            } else {
                break;
            }
            break;
        }
        return false;
    }

    private void saveQuerySellfoxOrder(JSONArray list) {
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            System.out.println(item.toString());
            break;
        }

    }


    public String getSellFoxData(String url, JSONObject params,String cookies) {

        RequestBody body = RequestBody.create(params.toString(), mediaType);


        JSONArray array = JSONArray.parseArray(cookies);
        List<String> c = new ArrayList<>();
        for (Object ac : array) {
            JSONObject acJson = (JSONObject) ac;
            c.add(acJson.getString("name") + "=" + acJson.getString("value"));
        }
        cookies = String.join(";", c);
        // -------------------------------
        // 构建请求（Headers 来源于截图）
        // -------------------------------
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("authority", "www.sellfox.com")
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9")
                .addHeader("content-type", "application/json")
                .addHeader("origin", "https://www.sellfox.com")
                .addHeader("referer", "https://www.sellfox.com/anzwup-web-main/web/warehouse/packingManagement/index.html")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36")
                .addHeader("Cookie", cookies)
                .build();

        // -------------------------------
        // 执行请求
        // -------------------------------
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP error: " + response.code());
            }
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}