package com.xxl.job.executor.biz.fba.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.executor.biz.cookie.service.SiteCookiesService;
import com.xxl.job.executor.biz.fba.entity.FbaAddressDO;
import com.xxl.job.executor.biz.fba.entity.FbaShipmentDO;
import com.xxl.job.executor.biz.fba.entity.FbaShipmentDetailsDO;
import com.xxl.job.executor.biz.fba.entity.FbaShipmentItemDO;
import com.xxl.job.executor.biz.fba.entity.FbaSpdBoxDO;
import com.xxl.job.executor.biz.fba.mapper.FbaAddressMapper;
import com.xxl.job.executor.biz.fba.mapper.FbaShipmentDetailsMapper;
import com.xxl.job.executor.biz.fba.mapper.FbaShipmentItemMapper;
import com.xxl.job.executor.biz.fba.mapper.FbaShipmentMapper;
import com.xxl.job.executor.biz.fba.mapper.FbaSpdBoxMapper;
import com.xxl.job.executor.biz.fba.service.FbaShipmentService;
import com.xxl.job.executor.mybatis.core.query.LambdaQueryWrapperX;
import com.xxl.job.executor.util.BeanUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <p>
 * 货件信息 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Service
public class FbaShipmentServiceImpl extends ServiceImpl<FbaShipmentMapper, FbaShipmentDO> implements FbaShipmentService {

    private static final String SELLFOX_SHIPMENT_PAGE_URL = "https://www.sellfox.com/api/inbound/shipment/page.json";
    private static final String SELLFOX_SHIPMENT_DETAIL_URL = "https://www.sellfox.com/api/inbound/shipment/detail.json";
    private static final String SELLFOX_SITE_NAME = "sellfox";

    @Resource
    private SiteCookiesService siteCookiesService;

    @Resource
    private FbaAddressMapper fbaAddressMapper;

    @Resource
    private FbaShipmentItemMapper fbaShipmentItemMapper;

    @Resource
    private FbaSpdBoxMapper fbaSpdBoxMapper;

    @Resource
    private FbaShipmentMapper fbaShipmentMapper;

    @Resource 
    private FbaShipmentDetailsMapper fbaShipmentDetailsMapper;

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    public String syncFbaShipment() {
        XxlJobHelper.log("开始同步FBA货件信息...");

        // 1. 获取Cookie
        String cookies = siteCookiesService.getSiteCookieStrByName(SELLFOX_SITE_NAME);
        if (cookies == null || cookies.isEmpty()) {
            XxlJobHelper.log("获取赛狐Cookie失败");
            return "获取赛狐Cookie失败,任务执行失败。";
        }

        int totalShipments = 0;
        int totalItems = 0;
        int totalBoxes = 0;

        try {
            // 2. 计算时间范围（默认最近15天）
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(15);
            String startTime = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String endTime = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // 3. 分页获取货件列表
            int pageNo = 1;
            int pageSize = 200;
            boolean hasMore = true;

            while (hasMore) {
                XxlJobHelper.log("正在获取第 {} 页货件列表...", pageNo);
                JSONObject pageResult = fetchShipmentPage(cookies, pageNo, pageSize, startTime, endTime);

                if (pageResult == null) {
                    XxlJobHelper.log("获取货件列表失败，pageNo={}", pageNo);
                    break;
                }

                Integer code = pageResult.getInteger("code");
                if (code == null || code != 0) {
                    XxlJobHelper.log("赛狐返回失败：code={}, msg={}", code, pageResult.getString("msg"));
                    break;
                }

                JSONObject data = pageResult.getJSONObject("data");
                if (data == null) {
                    XxlJobHelper.log("货件数据为空");
                    break;
                }

                JSONArray rows = data.getJSONArray("rows");
                if (rows == null || rows.isEmpty()) {
                    XxlJobHelper.log("第 {} 页无数据", pageNo);
                    break;
                }

                // 4. 遍历每个货件
                for (int i = 0; i < rows.size(); i++) {
                    JSONObject rowJson = rows.getJSONObject(i);
                    Long shipmentId = rowJson.getLong("id");
                    String amazonShipmentId = rowJson.getString("amazonShipmentId");

                    if (shipmentId == null || StringUtils.isBlank(amazonShipmentId)) {
                        continue;
                    }

                    // 4.1 保存发货地址 (source)
                    Long sourceAddrId = null;
                    JSONObject source = rowJson.getJSONObject("source");
                    if (source != null && !source.isEmpty()) {
                        sourceAddrId = saveOrUpdateAddress(source);
                    }

                    // 4.2 保存目的地址 (destination)
                    Long destAddrId = null;
                    JSONObject destination = rowJson.getJSONObject("destination");
                    if (destination != null && !destination.isEmpty()) {
                        destAddrId = saveOrUpdateAddress(destination);
                    }

                    // 4.3 保存货件主表
                    int shipmentCount = saveShipment(rowJson, shipmentId, sourceAddrId, destAddrId);
                    totalShipments += shipmentCount;

                    // 4.4 保存SPD箱子信息
                    JSONObject spdTrackingDetail = rowJson.getJSONObject("spdTrackingDetail");
                    if (spdTrackingDetail != null) {
                        JSONArray spdTrackingItems = spdTrackingDetail.getJSONArray("spdTrackingItems");
                        if (spdTrackingItems != null && !spdTrackingItems.isEmpty()) {
                            int boxCount = saveSpdBoxes(shipmentId, spdTrackingItems);
                            totalBoxes += boxCount;
                        }
                    } else {
                        // spdTrackingDetail为空时，通过transportationOption接口获取箱子信息
                        JSONArray transportationBoxes = fetchTransportationOption(cookies, amazonShipmentId);
                        if (transportationBoxes != null && !transportationBoxes.isEmpty()) {
                            int boxCount = saveSpdBoxes(shipmentId, transportationBoxes);
                            totalBoxes += boxCount;
                            XxlJobHelper.log("通过transportationOption接口获取并保存 {} 个箱子，amazonShipmentId: {}",
                                    boxCount, amazonShipmentId);
                        }
                    }

                    // 4.5 获取详情，保存货件明细 (items)
                    JSONObject detailResult = fetchShipmentDetail(cookies, amazonShipmentId);
                    if (detailResult != null) {
                        Integer detailCode = detailResult.getInteger("code");
                        if (detailCode != null && detailCode == 0) {
                            JSONObject detailData = detailResult.getJSONObject("data");
                            if (detailData != null) {
                                JSONArray items = detailData.getJSONArray("items");
                                if (items != null && !items.isEmpty()) {
                                    int itemCount = saveShipmentItems(shipmentId, items);
                                    totalItems += itemCount;
                                }
                            }
                        }
                    }
                }

                // 判断是否还有下一页
                Long totalSize = data.getLong("totalSize");
                if (totalSize == null || (long) pageNo * pageSize >= totalSize) {
                    hasMore = false;
                } else {
                    pageNo++;
                }

                // 避免请求过快
                Thread.sleep(100);
            }

            String result = String.format("同步完成，货件: %d, 明细: %d, 箱子: %d", totalShipments, totalItems, totalBoxes);
            XxlJobHelper.log(result);
            return result;
        } catch (Exception e) {
            XxlJobHelper.log("任务执行异常: {}", e.getMessage());
            return "任务执行失败: " + e.getMessage();
        }
    }

    /**
     * 获取货件详情
     */
    private JSONObject fetchShipmentDetail(String cookies, String amazonShipmentId) {
        try {
            String url = SELLFOX_SHIPMENT_DETAIL_URL + "?amazonShipmentId=" + amazonShipmentId;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("accept", "application/json, text/plain, */*")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .addHeader("Cookie", cookies)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    XxlJobHelper.log("请求货件详情失败，HTTP状态码: {}", response.code());
                    return null;
                }
                String respBody = response.body() != null ? response.body().string() : "";
                return JSONObject.parseObject(respBody);
            }
        } catch (Exception e) {
            XxlJobHelper.log("请求货件详情异常: {}，错误: {}", amazonShipmentId, e.getMessage());
            return null;
        }
    }

    /**
     * 保存或更新地址
     */
    private Long saveOrUpdateAddress(JSONObject addressJson) {
        if (addressJson == null || addressJson.isEmpty()) {
            return null;
        }

        FbaAddressDO address = BeanUtils.toBean(addressJson, FbaAddressDO.class);

        String addressLine1 = address.getAddressLine1();
        String postalCode = address.getPostalCode();
        String name = address.getName();

        if (StringUtils.isBlank(addressLine1) && StringUtils.isBlank(postalCode) && StringUtils.isBlank(name)) {
            return null;
        }

        FbaAddressDO existing = fbaAddressMapper.selectOne(
                new LambdaQueryWrapper<FbaAddressDO>()
                        .eq(StringUtils.isNotBlank(name), FbaAddressDO::getName, name)
                        .eq(StringUtils.isNotBlank(addressLine1), FbaAddressDO::getAddressLine1, addressLine1)
                        .eq(StringUtils.isNotBlank(postalCode), FbaAddressDO::getPostalCode, postalCode)
                        .last("LIMIT 1")
        );

        if (existing != null) {
            address.setId(existing.getId());
            fbaAddressMapper.updateById(address);
            return existing.getId();
        } else {
            fbaAddressMapper.insert(address);
            return address.getId();
        }
    }

    /**
     * 获取货件分页列表
     */
    private JSONObject fetchShipmentPage(String cookies, int pageNo, int pageSize, String startTime, String endTime) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("dateType", "createTime");
            requestBody.put("startTime", startTime);
            requestBody.put("endTime", endTime);
            requestBody.put("searchType", "amazonShipmentId");
            requestBody.put("searchContent", "");
            requestBody.put("shopIds", new JSONArray());
            requestBody.put("marketplaceIds", new JSONArray());
            requestBody.put("status", new JSONArray());
            requestBody.put("createIds", new JSONArray());
            requestBody.put("shipmentType", "");
            requestBody.put("fbaDimension", "Shipment");
            requestBody.put("searchMode", "blur");
            requestBody.put("isFilterBySearchValue", false);
            requestBody.put("desc", false);
            requestBody.put("orderBy", "");
            requestBody.put("pageNo", pageNo);
            requestBody.put("pageSize", pageSize);

            RequestBody body = RequestBody.create(
                    requestBody.toJSONString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SELLFOX_SHIPMENT_PAGE_URL)
                    .post(body)
                    .addHeader("accept", "application/json, text/plain, */*")
                    .addHeader("content-type", "application/json")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .addHeader("Cookie", cookies)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    XxlJobHelper.log("请求货件列表失败，HTTP状态码: {}", response.code());
                    return null;
                }
                String respBody = response.body() != null ? response.body().string() : "";
                return JSONObject.parseObject(respBody);
            }
        } catch (Exception e) {
            XxlJobHelper.log("请求货件列表异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 保存货件主表
     */
    private int saveShipment(JSONObject rowJson, Long shipmentId, Long sourceAddrId, Long destAddrId) {
        FbaShipmentDO shipment = BeanUtils.toBean(rowJson, FbaShipmentDO.class);
        shipment.setId(shipmentId);
        shipment.setSourceAddrId(sourceAddrId);
        shipment.setDestAddrId(destAddrId);

        // 处理 ltlTrackingDetail 中的字段
        JSONObject ltlTrackingDetail = rowJson.getJSONObject("ltlTrackingDetail");
        if (ltlTrackingDetail != null) {
            shipment.setLtlBillOfLadingNumber(ltlTrackingDetail.getString("billOfLadingNumber"));
            shipment.setLtlFreightBillNumber(ltlTrackingDetail.getString("freightBillNumber"));
        }

        // 处理 destination 中的 postalCode 和 email
        JSONObject destination = rowJson.getJSONObject("destination");
        if (destination != null) {
            shipment.setPostalCode(destination.getString("postalCode"));
            shipment.setEmail(destination.getString("email"));
        }

        shipment.setRawJson(rowJson.toJSONString());

        FbaShipmentDO existing = baseMapper.selectById(shipmentId);
        if (existing != null) {
            baseMapper.updateById(shipment);
            return 0;
        } else {
            baseMapper.insert(shipment);
            return 1;
        }
    }

    /**
     * 保存货件明细
     */
    private int saveShipmentItems(Long shipmentId, JSONArray items) {
        int count = 0;
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            String msku = itemJson.getString("msku");

            FbaShipmentItemDO item = BeanUtils.toBean(itemJson, FbaShipmentItemDO.class);
            item.setShipmentId(shipmentId);

            // 处理数组字段转字符串
            JSONArray prepInstructionList = itemJson.getJSONArray("prepInstructionList");
            if (prepInstructionList != null) {
                item.setPrepInstructionList(prepInstructionList.toJSONString());
            }
            JSONArray shipSnList = itemJson.getJSONArray("shipSnList");
            if (shipSnList != null) {
                item.setShipSnList(shipSnList.toJSONString());
            }
            JSONArray cartonList = itemJson.getJSONArray("cartonList");
            if (cartonList != null) {
                item.setCartonList(cartonList.toJSONString());
            }

            item.setRawJson(itemJson.toJSONString());

            FbaShipmentItemDO existing = fbaShipmentItemMapper.selectOne(
                    new LambdaQueryWrapper<FbaShipmentItemDO>()
                            .eq(FbaShipmentItemDO::getShipmentId, shipmentId)
                            .eq(StringUtils.isNotBlank(msku), FbaShipmentItemDO::getMsku, msku)
            );

            if (existing != null) {
                item.setId(existing.getId());
                fbaShipmentItemMapper.updateById(item);
            } else {
                item.setId(null);
                fbaShipmentItemMapper.insert(item);
                count++;
            }
        }
        return count;
    }

    /**
     * 保存SPD箱子信息
     */
    private int saveSpdBoxes(Long shipmentId, JSONArray spdTrackingItems) {
        int count = 0;
        for (int i = 0; i < spdTrackingItems.size(); i++) {
            JSONObject boxJson = spdTrackingItems.getJSONObject(i);
            String boxId = boxJson.getString("boxId");

            if (StringUtils.isBlank(boxId)) {
                continue;
            }

            FbaSpdBoxDO box = BeanUtils.toBean(boxJson, FbaSpdBoxDO.class);
            box.setShipmentId(shipmentId);

            FbaSpdBoxDO existing = fbaSpdBoxMapper.selectOne(
                    new LambdaQueryWrapper<FbaSpdBoxDO>()
                            .eq(FbaSpdBoxDO::getShipmentId, shipmentId)
                            .eq(FbaSpdBoxDO::getBoxId, boxId)
            );

            if (existing != null) {
                box.setId(existing.getId());
                fbaSpdBoxMapper.updateById(box);
            } else {
                box.setId(null);
                fbaSpdBoxMapper.insert(box);
                count++;
            }
        }

        // 检查synced字段
        FbaShipmentDO shipment = baseMapper.selectById(shipmentId);
        if (shipment != null && shipment.getSynced() != null && shipment.getSynced() == 1) {
            return count;
        }

        // 判断是否所有tracking_id都不为空且长度大于15
        List<FbaSpdBoxDO> spdBoxes = fbaSpdBoxMapper.selectList(
                new LambdaQueryWrapperX<FbaSpdBoxDO>()
                        .eq(FbaSpdBoxDO::getShipmentId, shipmentId)
        );

        if (CollUtil.isNotEmpty(spdBoxes)) {
            boolean allTrackingValid = true;
            for (FbaSpdBoxDO box : spdBoxes) {
                String trackingId = box.getTrackingId();
                if (StringUtils.isBlank(trackingId) || trackingId.length() <= 15) {
                    allTrackingValid = false;
                    break;
                }
            }

            if (allTrackingValid) {
                FbaShipmentDO updateShipment = new FbaShipmentDO();
                updateShipment.setId(shipmentId);
                updateShipment.setSynced(1);
                baseMapper.updateById(updateShipment);
            }
        }

        return count;
    }

    /**
     * 获取运输选项（箱子信息）
     */
    private JSONArray fetchTransportationOption(String cookies, String amazonShipmentId) {
        try {
            String url = "https://www.sellfox.com/api/inbound/transportationOption.json?amazonShipmentId=" + amazonShipmentId;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("accept", "application/json, text/plain, */*")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .addHeader("Cookie", cookies)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    XxlJobHelper.log("请求transportationOption失败，HTTP状态码: {}，amazonShipmentId: {}",
                            response.code(), amazonShipmentId);
                    return null;
                }
                String respBody = response.body() != null ? response.body().string() : "";
                JSONObject result = JSONObject.parseObject(respBody);

                if (result == null) {
                    return null;
                }

                Integer code = result.getInteger("code");
                if (code == null || code != 0) {
                    XxlJobHelper.log("请求transportationOption返回异常，code: {}，msg: {}，amazonShipmentId: {}",
                            code, result.getString("msg"), amazonShipmentId);
                    return null;
                }

                return result.getJSONArray("data");
            }
        } catch (Exception e) {
            XxlJobHelper.log("请求transportationOption异常，amazonShipmentId: {}，错误: {}", amazonShipmentId, e.getMessage());
            return null;
        }
    }

    @Override
    public String analyzeFbaShipment() {
        XxlJobHelper.log("开始分析FBA货件信息...");
        
        // 步骤一：检查货件的synced状态
        List<FbaShipmentDO> shipments = fbaShipmentMapper.selectList();
        if (CollUtil.isEmpty(shipments)) {
            XxlJobHelper.log("没有需要分析的货件数据");
            return "分析完成，没有需要分析的货件数据";
        }
        
        XxlJobHelper.log("查询到 {} 条货件记录，开始分析...", shipments.size());

        int updatedCount = 0;
        int noBoxCount = 0;
        for (FbaShipmentDO shipment : shipments) {
            Long shipmentId = shipment.getId();

            // 判断是否所有tracking_id都不为空且长度大于15，如果是则更新synced状态
            List<FbaSpdBoxDO> spdBoxes = fbaSpdBoxMapper.selectList(
                    new LambdaQueryWrapper<FbaSpdBoxDO>()
                            .eq(FbaSpdBoxDO::getShipmentId, shipmentId)
            );

            if (CollUtil.isNotEmpty(spdBoxes)) {
                boolean allTrackingValid = true;
                for (FbaSpdBoxDO box : spdBoxes) {
                    String trackingId = box.getTrackingId();
                    if (StringUtils.isBlank(trackingId) || trackingId.length() <= 15) {
                        allTrackingValid = false;
                        break;
                    }
                }

                if (allTrackingValid) {
                    FbaShipmentDO updateShipment = new FbaShipmentDO();
                    updateShipment.setId(shipmentId);
                    updateShipment.setSynced(1);
                    fbaShipmentMapper.updateById(updateShipment);
                    updatedCount++;
                }
            } else {
                FbaShipmentDO updateShipment = new FbaShipmentDO();
                updateShipment.setId(shipmentId);
                updateShipment.setSynced(0);
                fbaShipmentMapper.updateById(updateShipment);
                noBoxCount++;
            }
        }
        XxlJobHelper.log("步骤一完成：检测货件synced状态，更新了 {} 条记录，无箱子记录 {} 条", updatedCount, noBoxCount);

        // 步骤二：分析需要解析的FbaShipmentDetailsDO数据
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(3);
        List<FbaShipmentDetailsDO> unanalyzedDetails = fbaShipmentDetailsMapper.selectList(
                new LambdaQueryWrapperX<FbaShipmentDetailsDO>().between(FbaShipmentDetailsDO::getCreateTime, startTime, endTime)
        );
        
        int detailsProcessed = 0;
        int detailsFailed = 0;
        
        if (CollUtil.isEmpty(unanalyzedDetails)) {
            XxlJobHelper.log("步骤二完成：没有需要分析的erp_fba_shipment_details数据");
        } else {
            XxlJobHelper.log("查询到 {} 条待分析的details记录", unanalyzedDetails.size());
            
            // 遍历数据，解析raw_json并更新字段
            for (FbaShipmentDetailsDO details : unanalyzedDetails) {
                try {
                    // 解析raw_json
                    JSONObject rawJson = JSONObject.parseObject(details.getRawJson());
                    if (rawJson == null) {
                        XxlJobHelper.log("raw_json解析失败，ID: {}", details.getId());
                        detailsFailed++;
                        continue;
                    }

                    // 将解析的内容复制到对应的字段中
                    mapJsonToDetails(details, rawJson);

                    // 将analyzed字段修改为1
                    details.setAnalyzed(1);
                    fbaShipmentDetailsMapper.updateById(details);
                    detailsProcessed++;

                } catch (Exception e) {
                    XxlJobHelper.log("分析erp_fba_shipment_details失败，ID: {}，错误: {}", details.getId(), e.getMessage());
                    detailsFailed++;
                }
            }
            XxlJobHelper.log("步骤二完成：解析details记录，成功 {} 条，失败 {} 条", detailsProcessed, detailsFailed);
        }
        
        String result = String.format("分析完成，货件synced更新: %d, 无箱子: %d, details解析成功: %d, 失败: %d", 
                updatedCount, noBoxCount, detailsProcessed, detailsFailed);
        XxlJobHelper.log(result);
        
        return result;
    }

    /**
     * 将JSON对象映射到FbaShipmentDetailsDO字段
     */
    private void mapJsonToDetails(FbaShipmentDetailsDO details, JSONObject rawJson) {
        // 使用 BeanUtils.toBean 自动映射字段
        FbaShipmentDetailsDO mapped = BeanUtils.toBean(rawJson, FbaShipmentDetailsDO.class);

        // 复制所有字段，但排除 id、rawJson、analyzed、synced
        // 使用 Hutool 的 BeanUtil.copyProperties 忽略指定字段
        BeanUtil.copyProperties(mapped, details, "id", "rawJson", "analyzed", "synced");

        // 如果 jobNo 没有自动映射（JSON字段名为jobno），手动设置
        if (StringUtils.isBlank(details.getJobNo()) && rawJson.containsKey("jobno")) {
            details.setJobNo(rawJson.getString("jobno"));
        }
    }


    
}
