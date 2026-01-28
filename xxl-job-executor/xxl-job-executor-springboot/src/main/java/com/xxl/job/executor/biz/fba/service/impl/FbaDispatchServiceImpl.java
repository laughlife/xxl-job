package com.xxl.job.executor.biz.fba.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.executor.biz.fba.entity.FbaDispatchDO;
import com.xxl.job.executor.biz.fba.entity.FbaDispatchItemDO;
import com.xxl.job.executor.biz.fba.entity.FbaShipmentDO;
import com.xxl.job.executor.biz.fba.mapper.FbaDispatchItemMapper;
import com.xxl.job.executor.biz.fba.mapper.FbaDispatchMapper;
import com.xxl.job.executor.biz.fba.mapper.FbaShipmentMapper;
import com.xxl.job.executor.biz.fba.service.FbaDispatchService;
import com.xxl.job.executor.mybatis.core.query.LambdaQueryWrapperX;
import com.xxl.job.executor.mybatis.util.object.BeanUtils;
import com.xxl.job.executor.util.Md5Utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * FBA发件/子单表 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Service
@Slf4j
@Valid
public class FbaDispatchServiceImpl extends ServiceImpl<FbaDispatchMapper, FbaDispatchDO> implements FbaDispatchService {

    @Resource
    private FbaDispatchMapper fbaDispatchMapper;

    @Resource
    private FbaDispatchItemMapper fbaDispatchItemMapper;

    @Resource
    private FbaShipmentMapper fbaShipmentMapper;

    @Override
    public void analyzeLmtDownloadData() {

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(3);

        List<FbaDispatchDO> unanalyzedDispatches = fbaDispatchMapper.selectList(
                new LambdaQueryWrapperX<FbaDispatchDO>().between(FbaDispatchDO::getCreateTime, startTime, endTime)
        );

        if (CollUtil.isEmpty(unanalyzedDispatches)) {
            XxlJobHelper.log("没有需要分析的erp_fba_dispatch数据");
        } else {
            // 遍历数据，解析raw_json并更新字段
            for (FbaDispatchDO dispatch : unanalyzedDispatches) {
                try {
                    // 解析raw_json
                    JSONObject rawJson = JSONObject.parseObject(dispatch.getRawJson());
                    if (rawJson == null) {
                        XxlJobHelper.log("raw_json解析失败，ID: {}", dispatch.getId());
                        continue;
                    }

                    // 将解析的内容复制到对应的字段中（排除id/source/jobNo/analyzed/synced）
                    mapJsonToDispatch(dispatch, rawJson);

                    // 将analyzed字段修改为1
                    dispatch.setAnalyzed(1);
                    fbaDispatchMapper.updateById(dispatch);

                    XxlJobHelper.log("成功分析并更新erp_fba_dispatch，fba_shipment_id: {},job_no: {}", dispatch.getFbaShipmentId(), dispatch.getJobNo());
                } catch (Exception e) {
                    XxlJobHelper.log("分析erp_fba_dispatch失败，fba_shipment_id: {},job_no: {}", dispatch.getFbaShipmentId(), dispatch.getJobNo(), e);
                    // 记录错误并继续处理下一条记录
                }
            }
        }

    }

    /**
     * 将JSON对象映射到FbaDispatchDO字段
     */
    private void mapJsonToDispatch(FbaDispatchDO dispatch, JSONObject rawJson) {
        // 使用 BeanUtils.toBean 自动映射字段
        FbaDispatchDO mapped = BeanUtils.toBean(rawJson, FbaDispatchDO.class);

        // 复制所有字段，但排除 id、source、jobNo、analyzed、synced、rawJson
        // 使用 Hutool 的 BeanUtil.copyProperties 忽略指定字段
        BeanUtil.copyProperties(mapped, dispatch, "id", "source", "jobNo", "analyzed", "synced", "rawJson");

        // 解码特殊字段（驼峰命名）
        String[] decodeFields = {
            "reTel", "reZip", "reName", "reCompany", "reAddr",
            "reCity", "reAddr2", "reAddr3", "reState", "reCountry",
            "decValue", "feeCount", "hubInName", "hubInCode"
        };

        for (String fieldName : decodeFields) {
            if (rawJson.containsKey(fieldName)) {
                String fieldValue = rawJson.getString(fieldName);
                if (StringUtils.isNotBlank(fieldValue)) {
                    // 提取 {_raw=WEUP05} 中的 WEUP05
                    String extractedValue = extractRawValue(fieldValue);
                    if (StringUtils.isNotBlank(extractedValue)) {
                        // 根据字段名设置对应的属性
                        setDecodedField(dispatch, fieldName, extractedValue);
                    }
                }
            }
        }

        // 处理 manifestSettleDTOList - 加密字符串，需要解密后提取_raw的值
        if (rawJson.containsKey("manifestSettleDTOList")) {
            String encryptedValue = rawJson.getString("manifestSettleDTOList");
            if (StringUtils.isNotBlank(encryptedValue)) {
                Map<String, Object> decryptedMap = Md5Utils.decryptTokenToObj(encryptedValue);
                if (decryptedMap != null && decryptedMap.containsKey("_raw")) {
                    Object rawValue = decryptedMap.get("_raw");
                    if (rawValue != null) {
                        dispatch.setManifestSettleDtoList(rawValue.toString());
                    }
                }
            }
        }

        // 处理 problemInfoDTOList - 本身就是JSON对象，直接转为字符串存储
        if (rawJson.containsKey("problemInfoDTOList")) {
            Object problemInfoObj = rawJson.get("problemInfoDTOList");
            if (problemInfoObj != null) {
                dispatch.setProblemInfoDtoList(JSONObject.toJSONString(problemInfoObj));
            }
        }

        // 将 refno 赋值给 transferOrderNo
        if (rawJson.containsKey("refno")) {
            String refno = rawJson.getString("refno");
            if (StringUtils.isNotBlank(refno)) {
                dispatch.setTransferOrderNo(refno);
            }
        }

        // 根据 jobNo 查询 fbaShipmentId
        String jobNo = dispatch.getJobNo();
        if (StringUtils.isNotBlank(jobNo)) {
            // 如果 jobNo 中存在 - 连接符，截取 - 之前的数据
            String amazonShipmentId = jobNo.contains("-") ? jobNo.substring(0, jobNo.indexOf("-")) : jobNo;

            // 查询 erp_fba_shipment 表
            FbaShipmentDO fbaShipment = fbaShipmentMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FbaShipmentDO>()
                            .eq(FbaShipmentDO::getAmazonShipmentId, amazonShipmentId)
            );
            if (fbaShipment != null) {
                dispatch.setFbaShipmentId(fbaShipment.getId());
            }
        }

        // 解析ManifestSettleDtoList中储存的json对象
        parseAndSaveManifestSettleItems(dispatch);

    }


    /**
     * 从 {_raw=WEUP05} 格式中提取 WEUP05
     */
    private String extractRawValue(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return null;
        }

        // 使用 Md5Utils.decryptTokenToObj 解密
        Map<String, Object> decryptedMap = Md5Utils.decryptTokenToObj(rawValue);
        if (decryptedMap != null && decryptedMap.containsKey("_raw")) {
            Object rawObj = decryptedMap.get("_raw");
            if (rawObj != null) {
                return rawObj.toString();
            }
        }

        return null;
    }


    /**
     * 解析并保存 ManifestSettleDtoList 到 erp_fba_dispatch_item 表
     */
    private void parseAndSaveManifestSettleItems(FbaDispatchDO dispatch) {
        String manifestChildListJson = dispatch.getManifestChildList();
        if (StringUtils.isBlank(manifestChildListJson)) {
            return;
        }

        try {
            JSONArray itemArray = JSONArray.parseArray(manifestChildListJson);
            if (itemArray == null || itemArray.isEmpty()) {
                return;
            }

            // 获取现有的子运单明细
            List<FbaDispatchItemDO> existingItems = fbaDispatchItemMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FbaDispatchItemDO>()
                            .eq(FbaDispatchItemDO::getDispatchId, dispatch.getId())
            );

            // 将现有数据按 id 分组，便于比较
            Map<Long, FbaDispatchItemDO> existingMap = existingItems.stream()
                    .collect(java.util.stream.Collectors.toMap(FbaDispatchItemDO::getId, item -> item, (a, b) -> a));

            // 解析新数据
            Set<Long> newItemIds = new HashSet<>();
            for (int i = 0; i < itemArray.size(); i++) {
                JSONObject itemJson = itemArray.getJSONObject(i);
                Long itemId = itemJson.getLong("id");
                if (itemId == null) {
                    continue;
                }

                newItemIds.add(itemId);

                // 使用 BeanUtils 构建 FbaDispatchItemDO 对象
                FbaDispatchItemDO item = BeanUtils.toBean(itemJson, FbaDispatchItemDO.class);
                item.setId(itemId);
                item.setDispatchId(dispatch.getId());
                item.setFbaShipmentId(dispatch.getFbaShipmentId());

                // 处理特殊字段
                // downLoadUrls -> downloadUrls 字段名不匹配，需要手动设置
                item.setDownloadUrls(itemJson.getString("downLoadUrls"));

                // isdel 转换为 Boolean
                Integer isdelValue = itemJson.getInteger("isdel");
                item.setIsdel(isdelValue != null && isdelValue == 1);

                // 解析 createDatetime 毫秒时间戳
                Long createDatetimeMillis = itemJson.getLong("createDatetime");
                if (createDatetimeMillis != null) {
                    item.setCreateDatetime(java.time.LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(createDatetimeMillis),
                            java.time.ZoneId.systemDefault()
                    ));
                }

                // 判断是新增还是更新
                if (existingMap.containsKey(itemId)) {
                    // 更新
                    fbaDispatchItemMapper.updateById(item);
                } else {
                    // 新增
                    fbaDispatchItemMapper.insert(item);
                }
            }

            // 删除不在新数据中的旧数据
            for (Long existingId : existingMap.keySet()) {
                if (!newItemIds.contains(existingId)) {
                    fbaDispatchItemMapper.deleteById(existingId);
                }
            }

            log.info("解析并保存 ManifestSettleDtoList 完成，dispatchId: {}, 处理条数: {}",
                    dispatch.getId(), itemArray.size());

        } catch (Exception e) {
            log.error("解析 ManifestSettleDtoList 失败，dispatchId: {}", dispatch.getId(), e);
        }
    }


    /**
     * 根据字段名设置解码后的值
     */
    private void setDecodedField(FbaDispatchDO dispatch, String fieldName, String value) {
        switch (fieldName) {
            case "reTel":
                dispatch.setReTel(value);
                break;
            case "reZip":
                dispatch.setReZip(value);
                break;
            case "reName":
                dispatch.setReName(value);
                break;
            case "reCompany":
                dispatch.setReCompany(value);
                break;
            case "reAddr":
                dispatch.setReAddr(value);
                break;
            case "reCity":
                dispatch.setReCity(value);
                break;
            case "reAddr2":
                dispatch.setReAddr2(value);
                break;
            case "reAddr3":
                dispatch.setReAddr3(value);
                break;
            case "reState":
                dispatch.setReState(value);
                break;
            case "reCountry":
                dispatch.setReCountry(value);
                break;
            case "decValue":
                dispatch.setDecValue(value);
                break;
            case "feeCount":
                try {
                    dispatch.setFeeCount(value);
                } catch (NumberFormatException e) {
                    log.warn("解码feeCount字段失败，值: {}", value);
                }
                break;
            case "hubInName":
                dispatch.setHubInName(value);
                break;
            case "hubInCode":
                dispatch.setHubInCode(value);
                break;
            default:
                log.warn("未知的解码字段名: {}", fieldName);
        }
    }


}
