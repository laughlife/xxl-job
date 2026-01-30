package com.xxl.job.executor.biz.amazon.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.amazon.entity.BuyerDO;
import com.xxl.job.executor.biz.amazon.entity.OrderDO;
import com.xxl.job.executor.biz.amazon.entity.OrderEvaluationDO;
import com.xxl.job.executor.biz.amazon.entity.OrderExtDO;
import com.xxl.job.executor.biz.amazon.entity.OrderProductDO;
import com.xxl.job.executor.biz.amazon.entity.OrderProductExtDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderMapper;
import com.xxl.job.executor.biz.amazon.service.BuyerService;
import com.xxl.job.executor.biz.amazon.service.OrderEvaluationService;
import com.xxl.job.executor.biz.amazon.service.OrderExtService;
import com.xxl.job.executor.biz.amazon.service.OrderProductExtService;
import com.xxl.job.executor.biz.amazon.service.OrderProductService;
import com.xxl.job.executor.biz.amazon.service.OrderService;
import com.xxl.job.executor.biz.cookie.entity.SiteCookiesDO;
import com.xxl.job.executor.biz.cookie.service.SiteCookiesService;
import com.xxl.job.executor.util.BeanUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 亚马逊订单服务实现类
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDO> implements OrderService {

    public final String getOrderUrl = "https://www.sellfox.com/api/order/pageList.json";
    private static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private SiteCookiesService siteCookiesService;
    @Resource
    private BuyerService buyerService;
    @Resource
    private OrderExtService orderExtService;
    @Resource
    private OrderEvaluationService orderEvaluationService;
    @Resource
    private OrderProductService orderProductService;
    @Resource
    private OrderProductExtService orderProductExtService;

    public JSONObject getOrderParams() {
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
    public boolean getAmazonOrderByTime(LocalDate startDate, LocalDate endDate) {
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
                
                JSONArray list = data.getJSONArray("rows");
                saveQuerySellfoxOrder(list);

                if (pageNo < totalPage) {
                    params.put("pageNo", pageNo + 1);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return true;
    }

    /**
     * 保存从Sellfox查询的订单数据
     * 核心逻辑：通过 lastUpdateDate 判断订单是否有变化
     * 优化：使用批量查询和批量插入提升性能
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveQuerySellfoxOrder(JSONArray list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        // 1. 批量收集所有订单号
        List<String> amazonOrderIds = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            amazonOrderIds.add(list.getJSONObject(i).getString("amazonOrderId"));
        }

        // 2. 批量查询现有订单（一次查询）
        List<OrderDO> existingOrders = list(new LambdaQueryWrapper<OrderDO>()
                .in(OrderDO::getAmazonOrderId, amazonOrderIds));
        Map<String, OrderDO> existingOrderMap = existingOrders.stream()
                .collect(java.util.stream.Collectors.toMap(OrderDO::getAmazonOrderId, o -> o));

        // 3. 批量收集买家邮箱并查询
        List<String> buyerEmails = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String email = list.getJSONObject(i).getString("buyerEmail");
            if (StringUtils.isNotBlank(email)) {
                buyerEmails.add(email);
            }
        }
        List<BuyerDO> existingBuyers = buyerService.list(new LambdaQueryWrapper<BuyerDO>()
                .in(BuyerDO::getBuyerEmail, buyerEmails));
        Map<String, BuyerDO> existingBuyerMap = existingBuyers.stream()
                .collect(java.util.stream.Collectors.toMap(BuyerDO::getBuyerEmail, b -> b));

        // 4. 批量查询订单扩展
        List<OrderExtDO> existingExts = orderExtService.list(new LambdaQueryWrapper<OrderExtDO>()
                .in(OrderExtDO::getAmazonOrderId, amazonOrderIds));
        Map<String, OrderExtDO> existingExtMap = existingExts.stream()
                .collect(java.util.stream.Collectors.toMap(OrderExtDO::getAmazonOrderId, e -> e));

        // 5. 批量查询订单评价
        List<OrderEvaluationDO> existingEvals = orderEvaluationService.list(new LambdaQueryWrapper<OrderEvaluationDO>()
                .in(OrderEvaluationDO::getAmazonOrderId, amazonOrderIds));
        Map<String, OrderEvaluationDO> existingEvalMap = existingEvals.stream()
                .collect(java.util.stream.Collectors.toMap(OrderEvaluationDO::getAmazonOrderId, e -> e));

        // 准备批量插入/更新的数据
        List<BuyerDO> buyersToInsert = new ArrayList<>();
        List<OrderDO> ordersToInsert = new ArrayList<>();
        List<OrderDO> ordersToUpdate = new ArrayList<>();
        List<OrderExtDO> extsToInsert = new ArrayList<>();
        List<OrderExtDO> extsToUpdate = new ArrayList<>();
        List<OrderEvaluationDO> evalsToInsert = new ArrayList<>();
        List<OrderEvaluationDO> evalsToUpdate = new ArrayList<>();
        
        // 用于去重：记录本批次已处理的订单号
        java.util.Set<String> processedOrderIds = new java.util.HashSet<>();

        // 6. 遍历处理每条订单
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            String amazonOrderId = item.getString("amazonOrderId");
            
            // 跳过本批次已处理的订单（去重）
            if (processedOrderIds.contains(amazonOrderId)) {
                continue;
            }
            processedOrderIds.add(amazonOrderId);
            
            LocalDateTime lastUpdateDate = parseDateTime(item.getString("lastUpdateDate"));

            // 检查订单是否需要更新
            OrderDO existingOrder = existingOrderMap.get(amazonOrderId);
            if (existingOrder != null && existingOrder.getLastUpdateDate() != null 
                    && existingOrder.getLastUpdateDate().equals(lastUpdateDate)) {
                continue;
            }

            // 处理买家信息
            String buyerEmail = item.getString("buyerEmail");
            Long buyerId = null;
            if (StringUtils.isNotBlank(buyerEmail)) {
                BuyerDO existingBuyer = existingBuyerMap.get(buyerEmail);
                if (existingBuyer != null) {
                    buyerId = existingBuyer.getId();
                } else {
                    BuyerDO buyer = new BuyerDO();
                    buyer.setBuyerEmail(buyerEmail);
                    buyer.setBuyerName(item.getString("buyerName"));
                    buyer.setCreatedAt(LocalDateTime.now());
                    buyersToInsert.add(buyer);
                    existingBuyerMap.put(buyerEmail, buyer); // 防止重复
                }
            }

            // 准备订单数据
            OrderDO order = buildOrder(item, buyerId, existingOrder);
            if (existingOrder != null) {
                ordersToUpdate.add(order);
            } else {
                ordersToInsert.add(order);
            }

            // 准备订单扩展数据
            OrderExtDO ext = buildOrderExt(item, existingExtMap.get(amazonOrderId));
            if (existingExtMap.containsKey(amazonOrderId)) {
                extsToUpdate.add(ext);
            } else {
                extsToInsert.add(ext);
            }

            // 准备订单评价数据
            OrderEvaluationDO eval = buildOrderEvaluation(item, existingEvalMap.get(amazonOrderId));
            if (existingEvalMap.containsKey(amazonOrderId)) {
                evalsToUpdate.add(eval);
            } else {
                evalsToInsert.add(eval);
            }
        }

        // 7. 批量保存买家（先保存买家，获取ID后才能保存订单）
        if (!buyersToInsert.isEmpty()) {
            buyerService.saveBatch(buyersToInsert);
            // 更新买家ID映射
            for (BuyerDO buyer : buyersToInsert) {
                existingBuyerMap.put(buyer.getBuyerEmail(), buyer);
            }
        }

        // 8. 更新订单中的买家ID
        for (OrderDO order : ordersToInsert) {
            if (order.getBuyerId() == null) {
                String buyerEmail = null;
                for (int i = 0; i < list.size(); i++) {
                    if (list.getJSONObject(i).getString("amazonOrderId").equals(order.getAmazonOrderId())) {
                        buyerEmail = list.getJSONObject(i).getString("buyerEmail");
                        break;
                    }
                }
                if (buyerEmail != null) {
                    BuyerDO buyer = existingBuyerMap.get(buyerEmail);
                    if (buyer != null) {
                        order.setBuyerId(buyer.getId());
                    }
                }
            }
        }

        // 9. 批量插入和更新（使用saveOrUpdateBatch处理可能的重复）
        List<OrderDO> allOrders = new ArrayList<>();
        allOrders.addAll(ordersToInsert);
        allOrders.addAll(ordersToUpdate);
        if (!allOrders.isEmpty()) {
            saveOrUpdateBatch(allOrders);
        }
        
        List<OrderExtDO> allExts = new ArrayList<>();
        allExts.addAll(extsToInsert);
        allExts.addAll(extsToUpdate);
        if (!allExts.isEmpty()) {
            orderExtService.saveOrUpdateBatch(allExts);
        }
        
        List<OrderEvaluationDO> allEvals = new ArrayList<>();
        allEvals.addAll(evalsToInsert);
        allEvals.addAll(evalsToUpdate);
        if (!allEvals.isEmpty()) {
            orderEvaluationService.saveOrUpdateBatch(allEvals);
        }

        // 10. 批量处理订单商品
        batchSaveOrderProducts(list);

        log.info("批量处理完成，共处理 {} 条订单", list.size());
    }

    /**
     * 批量保存订单商品
     */
    private void batchSaveOrderProducts(JSONArray list) {
        // 收集所有商品的订单号和商品ID
        List<String> orderIds = new ArrayList<>();
        List<Long> sourceItemIds = new ArrayList<>();
        
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            String amazonOrderId = item.getString("amazonOrderId");
            JSONArray orderItems = item.getJSONArray("orderItemVoList");
            if (orderItems != null && !orderItems.isEmpty()) {
                for (int j = 0; j < orderItems.size(); j++) {
                    orderIds.add(amazonOrderId);
                    sourceItemIds.add(orderItems.getJSONObject(j).getLong("id"));
                }
            }
        }

        if (orderIds.isEmpty()) {
            return;
        }

        // 批量查询现有商品
        List<OrderProductDO> existingProducts = orderProductService.list(new LambdaQueryWrapper<OrderProductDO>()
                .in(OrderProductDO::getAmazonOrderId, orderIds));
        Map<String, OrderProductDO> existingProductMap = existingProducts.stream()
                .collect(java.util.stream.Collectors.toMap(
                    p -> p.getAmazonOrderId() + "_" + p.getSourceItemId(), p -> p));

        // 批量查询现有商品扩展
        List<OrderProductExtDO> existingProductExts = orderProductExtService.list(new LambdaQueryWrapper<OrderProductExtDO>()
                .in(OrderProductExtDO::getAmazonOrderId, orderIds));
        Map<String, OrderProductExtDO> existingProductExtMap = existingProductExts.stream()
                .collect(java.util.stream.Collectors.toMap(
                    p -> p.getAmazonOrderId() + "_" + p.getSourceItemId(), p -> p));

        List<OrderProductDO> productsToInsert = new ArrayList<>();
        List<OrderProductDO> productsToUpdate = new ArrayList<>();
        List<OrderProductExtDO> productExtsToInsert = new ArrayList<>();
        List<OrderProductExtDO> productExtsToUpdate = new ArrayList<>();

        // 遍历处理商品
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            String amazonOrderId = item.getString("amazonOrderId");
            JSONArray orderItems = item.getJSONArray("orderItemVoList");
            
            if (orderItems != null && !orderItems.isEmpty()) {
                for (int j = 0; j < orderItems.size(); j++) {
                    JSONObject productItem = orderItems.getJSONObject(j);
                    Long sourceItemId = productItem.getLong("id");
                    String key = amazonOrderId + "_" + sourceItemId;

                    // 处理商品
                    OrderProductDO existing = existingProductMap.get(key);
                    OrderProductDO product = buildOrderProduct(productItem, amazonOrderId, existing);
                    if (existing != null) {
                        productsToUpdate.add(product);
                    } else {
                        productsToInsert.add(product);
                    }

                    // 处理商品扩展
                    OrderProductExtDO existingExt = existingProductExtMap.get(key);
                    OrderProductExtDO productExt = buildOrderProductExt(productItem, amazonOrderId, existingExt);
                    if (existingExt != null) {
                        productExtsToUpdate.add(productExt);
                    } else {
                        productExtsToInsert.add(productExt);
                    }
                }
            }
        }

        // 批量保存（使用saveOrUpdateBatch处理可能的重复）
        List<OrderProductDO> allProducts = new ArrayList<>();
        allProducts.addAll(productsToInsert);
        allProducts.addAll(productsToUpdate);
        if (!allProducts.isEmpty()) {
            orderProductService.saveOrUpdateBatch(allProducts);
        }
        
        List<OrderProductExtDO> allProductExts = new ArrayList<>();
        allProductExts.addAll(productExtsToInsert);
        allProductExts.addAll(productExtsToUpdate);
        if (!allProductExts.isEmpty()) {
            orderProductExtService.saveOrUpdateBatch(allProductExts);
        }
    }

    /**
     * 构建订单对象
     */
    private OrderDO buildOrder(JSONObject item, Long buyerId, OrderDO existingOrder) {
        OrderDO order = BeanUtils.toBean(item, OrderDO.class);
        // 设置特殊字段
        if (existingOrder != null) {
            order.setId(existingOrder.getId());
        }
        order.setSourceId(item.getLong("id"));
        order.setBuyerId(buyerId);
        order.setMarketplaceCn(item.getString("marketplaceCN"));
        order.setPurchaseDate(parseDateTime(item.getString("purchaseDate")));
        order.setLastUpdateDate(parseDateTime(item.getString("lastUpdateDate")));
        order.setPaymentsDate(parseDateTime(item.getString("paymentsDate")));
        order.setIsReturnOrder(parseBoolean(item.get("isReturnOrder")));
        order.setIsBusinessOrder(parseBoolean(item.get("isBusinessOrder")));
        order.setIsPrime(parseBoolean(item.get("isPrime")));
        order.setUpdateTime(LocalDateTime.now());
        if (existingOrder == null) {
            order.setCreateTime(LocalDateTime.now());
        }
        return order;
    }

    /**
     * 构建订单扩展对象
     */
    private OrderExtDO buildOrderExt(JSONObject item, OrderExtDO existing) {
        OrderExtDO ext = BeanUtils.toBean(item, OrderExtDO.class);
        // 设置特殊字段
        if (existing != null) {
            ext.setId(existing.getId());
        }
        ext.setAmazonOrderId(item.getString("amazonOrderId"));
        ext.setLastUpdateDate(parseDateTime(item.getString("lastUpdateDate")));
        ext.setCapturedAt(LocalDateTime.now());
        ext.setEarliestShipDate(parseDateTime(item.getString("earliestShipDate")));
        ext.setLatestShipDate(parseDateTime(item.getString("latestShipDate")));
        ext.setEarliestDeliveryDate(parseDateTime(item.getString("earliestDeliveryDate")));
        ext.setLatestDeliveryDate(parseDateTime(item.getString("latestDeliveryDate")));
        ext.setIsBuyerRequestedCancel(parseBoolean(item.get("isBuyerRequestedCancel")));
        ext.setIsReplacementOrder(parseBoolean(item.get("isReplacementOrder")));
        ext.setIsVineOrder(parseBoolean(item.get("isVineOrder")));
        ext.setCustomOrder(parseBoolean(item.get("customOrder")));
        ext.setPromotionFlag(parseBoolean(item.get("promotionFlag")));
        ext.setIsHistory(parseBoolean(item.get("isHistory")));
        ext.setIsCalculating(parseBoolean(item.get("isCalculating")));
        ext.setLowCostStore(parseBoolean(item.get("lowCostStore")));
        ext.setCapitalDate(parseDateTime(item.getString("capitalDate")));
        ext.setCommissionDate(parseDateTime(item.getString("commissionDate")));
        // JSON字段特殊处理：空字符串转null
        ext.setPromotionIds(parseJsonString(item.getString("promotionIds")));
        ext.setRawJson(item.toString());
        ext.setUpdateTime(LocalDateTime.now());
        if (existing == null) {
            ext.setCreateTime(LocalDateTime.now());
        }
        return ext;
    }

    /**
     * 构建订单评价对象
     */
    private OrderEvaluationDO buildOrderEvaluation(JSONObject item, OrderEvaluationDO existing) {
        OrderEvaluationDO eval = BeanUtils.toBean(item, OrderEvaluationDO.class);
        // 设置特殊字段
        if (existing != null) {
            eval.setId(existing.getId());
        }
        eval.setAmazonOrderId(item.getString("amazonOrderId"));
        eval.setEvaluationCost(parseDecimal(item.getString("evaluationCost")));
        eval.setEvaluationCapital(parseDecimal(item.getString("evaluationCapital")));
        eval.setEvaluationCommission(parseDecimal(item.getString("evaluationCommission")));
        JSONArray evalIds = item.getJSONArray("evaluationIds");
        eval.setEvaluationIds(evalIds != null ? evalIds.toString() : null);
        if (existing == null) {
            eval.setCreateTime(LocalDateTime.now());
        }
        eval.setUpdateTime(LocalDateTime.now());
        return eval;
    }

    /**
     * 构建订单商品对象
     */
    private OrderProductDO buildOrderProduct(JSONObject item, String amazonOrderId, OrderProductDO existing) {
        OrderProductDO product = BeanUtils.toBean(item, OrderProductDO.class);
        // 设置特殊字段
        if (existing != null) {
            product.setId(existing.getId());
        }
        product.setAmazonOrderId(amazonOrderId);
        product.setSourceItemId(item.getLong("id"));
        product.setUpdateTime(LocalDateTime.now());
        if (existing == null) {
            product.setCreateTime(LocalDateTime.now());
        }
        return product;
    }

    /**
     * 构建订单商品扩展对象
     */
    private OrderProductExtDO buildOrderProductExt(JSONObject item, String amazonOrderId, OrderProductExtDO existing) {
        OrderProductExtDO ext = BeanUtils.toBean(item, OrderProductExtDO.class);
        // 设置特殊字段
        if (existing != null) {
            ext.setId(existing.getId());
        }
        ext.setAmazonOrderId(amazonOrderId);
        ext.setSourceItemId(item.getLong("id"));
        ext.setShippingChargeAmount(item.getBigDecimal("shippingCharge"));
        ext.setHeadTripShare(parseBoolean(item.get("headTripShare")));
        ext.setCapitalDate(parseDateTime(item.getString("capitalDate")));
        ext.setCommissionDate(parseDateTime(item.getString("commissionDate")));
        ext.setRawJson(item.toString());
        ext.setUpdateTime(LocalDateTime.now());
        if (existing == null) {
            ext.setCreateTime(LocalDateTime.now());
        }
        return ext;
    }

    // ==================== 工具方法 ====================

    /**
     * 解析JSON字符串，空串或非法JSON返回null
     */
    private String parseJsonString(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        // 检查是否是有效的JSON格式（数组或对象）
        String trimmed = jsonStr.trim();
        if ((trimmed.startsWith("[") && trimmed.endsWith("]")) 
                || (trimmed.startsWith("{") && trimmed.endsWith("}"))) {
            return jsonStr;
        }
        return null;
    }

    /**
     * 解析日期时间字符串，空串返回null
     */
    private LocalDateTime parseDateTime(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("日期解析失败: {}", dateStr);
            return null;
        }
    }

    /**
     * 解析数值字符串，空串或null返回0
     */
    private BigDecimal parseDecimal(String numStr) {
        if (StringUtils.isBlank(numStr)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(numStr);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 解析布尔值
     */
    private Boolean parseBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value) != 0;
        }
        return false;
    }

    public String getSellFoxData(String url, JSONObject params, String cookies) {
        RequestBody body = RequestBody.create(params.toString(), mediaType);
        JSONArray array = JSONArray.parseArray(cookies);
        List<String> c = new ArrayList<>();
        for (Object ac : array) {
            JSONObject acJson = (JSONObject) ac;
            c.add(acJson.getString("name") + "=" + acJson.getString("value"));
        }
        cookies = String.join(";", c);

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

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP error: " + response.code());
            }
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            log.error("请求Sellfox失败", e);
            return null;
        }
    }
}
