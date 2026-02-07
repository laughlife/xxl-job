# Amazon订单JSON解析存储设计方案

## 一、概述

本方案针对从 Sellfox API 获取的 Amazon 订单 JSON 数据进行解析和存储设计。

### 1.1 核心设计原则

1. **变更检测机制**: 使用 `lastUpdateDate` 字段判断订单是否发生变化，若与数据库一致则跳过更新
2. **只改不增策略**: 订单相关表采用更新策略，不保留历史快照
3. **评价独立管理**: 评价相关字段单独建表 `amazon_order_evaluation`
4. **类型严格转换**: JSON 中的字符串类型日期/数值字段转换为正确的数据库类型

## 二、表结构设计

### 2.1 表清单（共6张表）

| 表名 | 用途 | 更新策略 |
|------|------|----------|
| `amazon_order` | 订单主表（高频查询字段） | 按 lastUpdateDate 判断，有变化则更新 |
| `amazon_order_ext` | 订单扩展表（低频字段） | 按 lastUpdateDate 判断，有变化则更新 |
| `amazon_order_product` | 订单商品表（高频字段） | 随订单更新 |
| `amazon_order_product_ext` | 订单商品扩展表（低频字段） | 随订单更新 |
| `amazon_buyer` | 买家维表 | 按 email 去重，存在则跳过 |
| `amazon_order_evaluation` | 订单评价表 | **新建表**，随订单更新 |

## 三、JSON字段映射分析

### 3.1 订单主表 amazon_order

| JSON字段 | JSON类型 | DB字段 | DB类型 | 状态 |
|----------|----------|--------|--------|------|
| id | Long | source_id | BIGINT | **新增** |
| puid | Long | puid | BIGINT | ✅已有 |
| shopId | Long | shop_id | BIGINT | ✅已有 |
| amazonOrderId | String | amazon_order_id | VARCHAR(32) | ✅已有 |
| purchaseDate | String | purchase_date | DATETIME | ✅已有 |
| lastUpdateDate | String | last_update_date | DATETIME | ✅已有(变更判断字段) |
| orderStatus | String | order_status | VARCHAR(32) | ✅已有 |
| orderTotalCurrency | String | order_total_currency | VARCHAR(8) | ✅已有 |
| orderTotalAmount | Decimal | order_total_amount | DECIMAL(12,4) | ✅已有 |
| marketplaceId | String | marketplace_id | VARCHAR(32) | ✅已有 |
| orderType | String | order_type | VARCHAR(32) | ✅已有 |
| fulfillmentChannel | String | fulfillment_channel | VARCHAR(16) | ✅已有 |
| shopName | String | shop_name | VARCHAR(128) | ✅已有 |
| marketplaceCN | String | marketplace_cn | VARCHAR(32) | ✅已有 |
| orderProfit | Decimal | order_profit | DECIMAL(12,4) | ✅已有 |
| profit | Decimal | profit | DECIMAL(12,4) | ✅已有 |
| profitRate | Decimal | profit_rate | DECIMAL(8,4) | ✅已有 |
| isReturnOrder | Integer | is_return_order | TINYINT | ✅已有 |
| isBusinessOrder | Integer | is_business_order | TINYINT | ✅已有 |
| isPrime | Integer | is_prime | TINYINT | ✅已有 |
| paymentsDate | String | payments_date | DATETIME | ✅已有 |
| buyerEmail | String | buyer_id | BIGINT | **新增**(关联buyer表) |

### 3.2 订单扩展表 amazon_order_ext

| JSON字段 | JSON类型 | DB字段 | DB类型 | 状态 |
|----------|----------|--------|--------|------|
| earliestShipDate | String | earliest_ship_date | DATETIME | ✅已有 |
| latestShipDate | String | latest_ship_date | DATETIME | ✅已有 |
| earliestDeliveryDate | String | earliest_delivery_date | DATETIME | ✅已有 |
| latestDeliveryDate | String | latest_delivery_date | DATETIME | ✅已有 |
| isReplacementOrder | Integer | is_replacement_order | TINYINT | ✅已有 |
| isVineOrder | Integer | is_vine_order | TINYINT | ✅已有 |
| customOrder | Integer | custom_order | TINYINT | ✅已有 |
| promotionFlag | Boolean | promotion_flag | TINYINT | ✅已有 |
| paymentMethodDetails | String | payment_method_details | VARCHAR(256) | ✅已有 |
| orderFlag | Integer | order_flag | INT | ✅已有 |
| isHistory | Integer | is_history | TINYINT | **新增** |
| isBuyerRequestedCancel | Integer | is_buyer_requested_cancel | TINYINT | ✅已有 |
| refundStatus | Integer | refund_status | TINYINT | ✅已有 |
| orderReviewStatus | String | order_review_status | VARCHAR(32) | ✅已有 |
| uploadFeedStatus | Integer | upload_feed_status | INT | ✅已有 |
| isCalculating | Boolean | is_calculating | TINYINT | **新增** |
| lowCostStore | Integer | low_cost_store | TINYINT | **新增** |
| taxNumber | String | tax_number | VARCHAR(64) | **新增** |
| capitalCurrency | String | capital_currency | VARCHAR(8) | **新增** |
| commissionCurrency | String | commission_currency | VARCHAR(8) | **新增** |
| capitalDate | String | capital_date | DATETIME | **新增**(空串转NULL) |
| commissionDate | String | commission_date | DATETIME | **新增**(空串转NULL) |
| fbmCostOrigin | Decimal | fbm_cost_origin | DECIMAL(12,4) | **新增** |
| fbmCost | Decimal | fbm_cost | DECIMAL(12,4) | **新增** |

### 3.3 订单评价表 amazon_order_evaluation（新建）

| JSON字段 | JSON类型 | DB字段 | DB类型 | 说明 |
|----------|----------|--------|--------|------|
| - | - | id | BIGINT | 主键自增 |
| amazonOrderId | String | amazon_order_id | VARCHAR(32) | 订单号 |
| evaluation | Integer | evaluation | INT | 评价数 |
| evaluationCost | String | evaluation_cost | DECIMAL(12,4) | 评价成本(转数值) |
| evaluationCurrency | String | evaluation_currency | VARCHAR(8) | 评价币种 |
| evaluationCapital | String | evaluation_capital | DECIMAL(12,4) | 评价资金(转数值) |
| evaluationCommission | String | evaluation_commission | DECIMAL(12,4) | 评价佣金(转数值) |
| evaluationIds | Array | evaluation_ids | TEXT | 评价ID列表JSON |
| evaluationPayStatus | Integer | evaluation_pay_status | TINYINT | 评价支付状态 |
| - | - | create_time | DATETIME | 创建时间 |
| - | - | update_time | DATETIME | 更新时间 |

### 3.4 订单商品表 amazon_order_product

| JSON字段 | JSON类型 | DB字段 | DB类型 | 状态 |
|----------|----------|--------|--------|------|
| id | Long | source_item_id | BIGINT | ✅已有 |
| orderItemId | String | order_item_id | VARCHAR(64) | **新增** |
| puid | Long | puid | BIGINT | ✅已有 |
| shopId | Long | shop_id | BIGINT | ✅已有 |
| amazonOrderId | String | amazon_order_id | VARCHAR(32) | ✅已有 |
| commodityId | Long | commodity_id | BIGINT | ✅已有 |
| commoditySku | String | commodity_sku | VARCHAR(64) | ✅已有 |
| commodityName | String | commodity_name | VARCHAR(256) | ✅已有 |
| title | String | title | VARCHAR(512) | ✅已有 |
| asin | String | asin | VARCHAR(16) | ✅已有 |
| parentAsin | String | parent_asin | VARCHAR(16) | ✅已有 |
| sellerSku | String | seller_sku | VARCHAR(64) | ✅已有 |
| listingId | String | listing_id | VARCHAR(32) | ✅已有 |
| imageUrl | String | image_url | VARCHAR(512) | ✅已有 |
| asinUrl | String | asin_url | VARCHAR(256) | ✅已有 |
| quantityOrdered | Integer | quantity_ordered | INT | ✅已有 |
| quantityShipped | Integer | quantity_shipped | INT | ✅已有 |
| itemPriceCurrency | String | item_price_currency | VARCHAR(8) | ✅已有 |
| itemPriceAmount | Decimal | item_price_amount | DECIMAL(12,4) | ✅已有 |
| itemTaxAmount | Decimal | item_tax_amount | DECIMAL(12,4) | ✅已有 |

### 3.5 订单商品扩展表 amazon_order_product_ext

| JSON字段 | JSON类型 | DB字段 | DB类型 | 状态 |
|----------|----------|--------|--------|------|
| id | Long | source_item_id | BIGINT | ✅已有 |
| fnsku | String | fnsku | VARCHAR(16) | ✅已有 |
| promotionIds | String | promotion_ids | VARCHAR(256) | ✅已有 |
| withheldTaxAmount | Decimal | withheld_tax_amount | DECIMAL(12,4) | ✅已有 |
| shippingCharge | Decimal | shipping_charge_amount | DECIMAL(12,4) | ✅已有 |
| shippingTaxAmount | Decimal | shipping_tax_amount | DECIMAL(12,4) | ✅已有 |
| giftWrapAmount | Decimal | gift_wrap_amount | DECIMAL(12,4) | ✅已有 |
| giftWrapTaxAmount | Decimal | gift_wrap_tax_amount | DECIMAL(12,4) | ✅已有 |
| promotionDiscount | Decimal | promotion_discount | DECIMAL(12,4) | **新增** |
| promotionDiscountCurrency | String | promotion_discount_currency | VARCHAR(8) | ✅已有 |
| promotionDiscountAmount | Decimal | promotion_discount_amount | DECIMAL(12,4) | ✅已有 |
| otherAmount | Decimal | other_amount | DECIMAL(12,4) | ✅已有 |
| fbaPerUnitFulfillmentFee | Decimal | fba_per_unit_fulfillment_fee | DECIMAL(12,4) | ✅已有 |
| commission | Decimal | commission | DECIMAL(12,4) | ✅已有 |
| amazonBackToArticle | Decimal | amazon_back_to_article | DECIMAL(12,4) | **新增** |
| mergePurchaseCost | Decimal | merge_purchase_cost | DECIMAL(12,4) | **新增** |
| purchaseCost | Decimal | purchase_cost | DECIMAL(12,4) | **新增** |
| headTripCost | Decimal | head_trip_cost | DECIMAL(12,4) | **新增** |
| headTripShare | Boolean | head_trip_share | TINYINT | **新增** |
| fbmShipCost | Decimal | fbm_ship_cost | DECIMAL(12,4) | **新增** |
| capitalCurrency | String | capital_currency | VARCHAR(8) | **新增** |
| capitalDate | String | capital_date | DATETIME | **新增**(空串转NULL) |
| commissionCurrency | String | commission_currency | VARCHAR(8) | **新增** |
| commissionDate | String | commission_date | DATETIME | **新增**(空串转NULL) |
| evaluation | Integer | evaluation | INT | **新增** |

### 3.6 买家维表 amazon_buyer（无需变更）

| JSON字段 | DB字段 | 状态 |
|----------|--------|------|
| buyerEmail | buyer_email | ✅已有 |
| buyerName | buyer_name | ✅已有 |

## 四、数据存储流程

### 4.1 核心流程图

```
┌─────────────────────────────────────────────────────────────┐
│                    Sellfox API 返回 JSON                     │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  遍历订单列表，对每条订单执行以下流程：                          │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  1. 根据 amazonOrderId 查询数据库中的 lastUpdateDate           │
└─────────────────────────────┬───────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              │ lastUpdateDate 是否一致？       │
              └───────────────┬───────────────┘
                    │                   │
                   YES                  NO
                    │                   │
                    ▼                   ▼
            ┌───────────┐      ┌────────────────────┐
            │  跳过更新  │      │  执行更新流程       │
            └───────────┘      └─────────┬──────────┘
                                         │
         ┌───────────────────────────────┼───────────────────────────────┐
         │                               │                               │
         ▼                               ▼                               ▼
┌─────────────────┐           ┌─────────────────┐           ┌─────────────────┐
│ 2. 保存/更新     │           │ 3. 保存/更新     │           │ 4. 保存/更新     │
│   amazon_buyer  │           │  amazon_order   │           │ amazon_order_ext│
│  (email去重)    │           │ (按amazonOrderId)│           │ (按amazonOrderId)│
└─────────────────┘           └─────────────────┘           └─────────────────┘
                                         │
                                         ▼
                              ┌─────────────────┐
                              │ 5. 保存/更新     │
                              │ amazon_order_   │
                              │   evaluation    │
                              │ (按amazonOrderId)│
                              └─────────────────┘
                                         │
                                         ▼
                              ┌─────────────────────────────────────┐
                              │ 6. 遍历 orderItemVoList 商品列表     │
                              └─────────────────┬───────────────────┘
                                                │
                        ┌───────────────────────┴───────────────────────┐
                        ▼                                               ▼
              ┌─────────────────────┐                       ┌─────────────────────┐
              │ 7. 保存/更新         │                       │ 8. 保存/更新         │
              │ amazon_order_product│                       │ amazon_order_       │
              │ (按amazonOrderId +  │                       │   product_ext       │
              │    sourceItemId)    │                       │ (按amazonOrderId +  │
              └─────────────────────┘                       │    sourceItemId)    │
                                                            └─────────────────────┘
```

### 4.2 更新策略说明

| 表名 | 唯一键 | 策略 |
|------|--------|------|
| amazon_order | amazon_order_id | INSERT ON DUPLICATE KEY UPDATE |
| amazon_order_ext | amazon_order_id | INSERT ON DUPLICATE KEY UPDATE |
| amazon_order_evaluation | amazon_order_id | INSERT ON DUPLICATE KEY UPDATE |
| amazon_order_product | amazon_order_id + source_item_id | INSERT ON DUPLICATE KEY UPDATE |
| amazon_order_product_ext | amazon_order_id + source_item_id | INSERT ON DUPLICATE KEY UPDATE |
| amazon_buyer | buyer_email | INSERT IGNORE（存在则跳过） |

### 4.3 类型转换规则

| 场景 | JSON值 | 转换规则 | DB值 |
|------|--------|----------|------|
| 日期字符串 | "2026-01-28 00:00:47" | 解析为 LocalDateTime | 2026-01-28 00:00:47 |
| 空日期字符串 | "" | 转为 NULL | NULL |
| 数值字符串 | "0" | 解析为 BigDecimal | 0.0000 |
| 布尔值 | false | 转为 0/1 | 0 |
| 数组 | ["Standard"] | 转为 JSON 字符串 | "[\"Standard\"]" |

## 五、DDL 变更脚本

### 5.1 amazon_order 表变更

```sql
-- 新增 source_id 字段
ALTER TABLE amazon_order ADD COLUMN source_id BIGINT COMMENT 'Sellfox系统订单ID';

-- 新增 buyer_id 关联字段
ALTER TABLE amazon_order ADD COLUMN buyer_id BIGINT COMMENT '买家ID，关联amazon_buyer表';

-- 添加唯一索引（如果不存在）
ALTER TABLE amazon_order ADD UNIQUE INDEX uk_amazon_order_id (amazon_order_id);
```

### 5.2 amazon_order_ext 表变更

```sql
ALTER TABLE amazon_order_ext 
ADD COLUMN is_history TINYINT DEFAULT 0 COMMENT '是否历史订单',
ADD COLUMN is_calculating TINYINT DEFAULT 0 COMMENT '是否正在计算中',
ADD COLUMN low_cost_store TINYINT DEFAULT 0 COMMENT '低成本店铺标记',
ADD COLUMN tax_number VARCHAR(64) DEFAULT '' COMMENT '税号',
ADD COLUMN capital_currency VARCHAR(8) DEFAULT '' COMMENT '资金币种',
ADD COLUMN commission_currency VARCHAR(8) DEFAULT '' COMMENT '佣金币种',
ADD COLUMN capital_date DATETIME DEFAULT NULL COMMENT '资金日期',
ADD COLUMN commission_date DATETIME DEFAULT NULL COMMENT '佣金日期',
ADD COLUMN fbm_cost_origin DECIMAL(12,4) DEFAULT 0 COMMENT 'FBM原始成本',
ADD COLUMN fbm_cost DECIMAL(12,4) DEFAULT 0 COMMENT 'FBM成本';

-- 添加唯一索引（如果不存在）
ALTER TABLE amazon_order_ext ADD UNIQUE INDEX uk_amazon_order_id (amazon_order_id);
```

### 5.3 amazon_order_evaluation 表（新建）

```sql
CREATE TABLE amazon_order_evaluation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    amazon_order_id VARCHAR(32) NOT NULL COMMENT 'Amazon订单号',
    evaluation INT DEFAULT 0 COMMENT '评价数',
    evaluation_cost DECIMAL(12,4) DEFAULT 0 COMMENT '评价成本',
    evaluation_currency VARCHAR(8) DEFAULT 'USD' COMMENT '评价币种',
    evaluation_capital DECIMAL(12,4) DEFAULT 0 COMMENT '评价资金',
    evaluation_commission DECIMAL(12,4) DEFAULT 0 COMMENT '评价佣金',
    evaluation_ids TEXT COMMENT '评价ID列表JSON',
    evaluation_pay_status TINYINT DEFAULT 0 COMMENT '评价支付状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX uk_amazon_order_id (amazon_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='亚马逊订单评价表';
```

### 5.4 amazon_order_product 表变更

```sql
ALTER TABLE amazon_order_product 
ADD COLUMN order_item_id VARCHAR(64) COMMENT 'Amazon订单商品ID';

-- 添加联合唯一索引（如果不存在）
ALTER TABLE amazon_order_product ADD UNIQUE INDEX uk_order_item (amazon_order_id, source_item_id);
```

### 5.5 amazon_order_product_ext 表变更

```sql
ALTER TABLE amazon_order_product_ext 
ADD COLUMN source_item_id BIGINT COMMENT 'Sellfox系统商品明细ID',
ADD COLUMN promotion_discount DECIMAL(12,4) DEFAULT 0 COMMENT '促销折扣',
ADD COLUMN amazon_back_to_article DECIMAL(12,4) DEFAULT 0 COMMENT '亚马逊退款',
ADD COLUMN merge_purchase_cost DECIMAL(12,4) DEFAULT 0 COMMENT '合并采购成本',
ADD COLUMN purchase_cost DECIMAL(12,4) DEFAULT 0 COMMENT '采购成本',
ADD COLUMN head_trip_cost DECIMAL(12,4) DEFAULT 0 COMMENT '头程费用',
ADD COLUMN head_trip_share TINYINT DEFAULT 0 COMMENT '头程分摊',
ADD COLUMN fbm_ship_cost DECIMAL(12,4) DEFAULT 0 COMMENT 'FBM运费',
ADD COLUMN capital_currency VARCHAR(8) DEFAULT '' COMMENT '资金币种',
ADD COLUMN capital_date DATETIME DEFAULT NULL COMMENT '资金日期',
ADD COLUMN commission_currency VARCHAR(8) DEFAULT '' COMMENT '佣金币种',
ADD COLUMN commission_date DATETIME DEFAULT NULL COMMENT '佣金日期',
ADD COLUMN evaluation INT DEFAULT 0 COMMENT '评价数';

-- 添加联合唯一索引（如果不存在）
ALTER TABLE amazon_order_product_ext ADD UNIQUE INDEX uk_order_item (amazon_order_id, source_item_id);
```

### 5.6 amazon_buyer 表变更

```sql
-- 添加唯一索引（如果不存在）
ALTER TABLE amazon_buyer ADD UNIQUE INDEX uk_buyer_email (buyer_email);
```

## 六、代码实现方案

### 6.1 核心处理方法

```java
/**
 * 保存从Sellfox查询的订单数据
 * 核心逻辑：通过 lastUpdateDate 判断订单是否有变化
 */
private void saveQuerySellfoxOrder(JSONArray list) {
    for (int i = 0; i < list.size(); i++) {
        JSONObject item = list.getJSONObject(i);
        String amazonOrderId = item.getString("amazonOrderId");
        String lastUpdateDateStr = item.getString("lastUpdateDate");
        LocalDateTime lastUpdateDate = parseDateTime(lastUpdateDateStr);
        
        // 1. 检查订单是否需要更新
        OrderDO existingOrder = orderMapper.selectByAmazonOrderId(amazonOrderId);
        if (existingOrder != null && existingOrder.getLastUpdateDate().equals(lastUpdateDate)) {
            log.debug("订单 {} 无变化，跳过更新", amazonOrderId);
            continue;
        }
        
        // 2. 保存/更新买家信息
        Long buyerId = saveBuyer(item);
        
        // 3. 保存/更新订单主表
        saveOrUpdateOrder(item, buyerId);
        
        // 4. 保存/更新订单扩展表
        saveOrUpdateOrderExt(item);
        
        // 5. 保存/更新订单评价表
        saveOrUpdateOrderEvaluation(item);
        
        // 6. 处理订单商品列表
        JSONArray orderItems = item.getJSONArray("orderItemVoList");
        if (orderItems != null && !orderItems.isEmpty()) {
            for (int j = 0; j < orderItems.size(); j++) {
                JSONObject productItem = orderItems.getJSONObject(j);
                saveOrUpdateOrderProduct(productItem, amazonOrderId);
                saveOrUpdateOrderProductExt(productItem, amazonOrderId);
            }
        }
        
        log.info("订单 {} 更新完成", amazonOrderId);
    }
}
```

### 6.2 类型转换工具方法

```java
/**
 * 解析日期时间字符串，空串返回null
 */
private LocalDateTime parseDateTime(String dateStr) {
    if (StringUtils.isBlank(dateStr)) {
        return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return LocalDateTime.parse(dateStr, formatter);
}

/**
 * 解析数值字符串，空串或null返回0
 */
private BigDecimal parseDecimal(String numStr) {
    if (StringUtils.isBlank(numStr)) {
        return BigDecimal.ZERO;
    }
    return new BigDecimal(numStr);
}

/**
 * 解析布尔值为整数
 */
private Integer parseBoolean(Object value) {
    if (value == null) {
        return 0;
    }
    if (value instanceof Boolean) {
        return (Boolean) value ? 1 : 0;
    }
    if (value instanceof Integer) {
        return (Integer) value;
    }
    return 0;
}
```

### 6.3 需要新增的实体类

#### OrderEvaluationDO.java

```java
@Getter
@Setter
@ToString
@TableName("amazon_order_evaluation")
public class OrderEvaluationDO implements Serializable {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("amazon_order_id")
    private String amazonOrderId;
    
    @TableField("evaluation")
    private Integer evaluation;
    
    @TableField("evaluation_cost")
    private BigDecimal evaluationCost;
    
    @TableField("evaluation_currency")
    private String evaluationCurrency;
    
    @TableField("evaluation_capital")
    private BigDecimal evaluationCapital;
    
    @TableField("evaluation_commission")
    private BigDecimal evaluationCommission;
    
    @TableField("evaluation_ids")
    private String evaluationIds;
    
    @TableField("evaluation_pay_status")
    private Integer evaluationPayStatus;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
```

## 七、实施步骤清单

### 7.1 数据库变更（按顺序执行）

- [ ] 1. 备份现有数据
- [ ] 2. 执行 amazon_order 表 ALTER 语句
- [ ] 3. 执行 amazon_order_ext 表 ALTER 语句
- [ ] 4. 执行 amazon_order_evaluation 表 CREATE 语句
- [ ] 5. 执行 amazon_order_product 表 ALTER 语句
- [ ] 6. 执行 amazon_order_product_ext 表 ALTER 语句
- [ ] 7. 执行 amazon_buyer 表索引添加语句

### 7.2 代码变更

- [ ] 1. 新增 OrderEvaluationDO 实体类
- [ ] 2. 新增 OrderEvaluationMapper 接口
- [ ] 3. 新增 OrderEvaluationMapper.xml
- [ ] 4. 新增 OrderEvaluationService 接口和实现类
- [ ] 5. 更新 OrderDO 实体类（新增 source_id, buyer_id 字段）
- [ ] 6. 更新 OrderExtDO 实体类（新增字段）
- [ ] 7. 更新 OrderProductDO 实体类（新增 order_item_id 字段）
- [ ] 8. 更新 OrderProductExtDO 实体类（新增字段）
- [ ] 9. 更新各 Mapper.xml 文件
- [ ] 10. 重构 OrderServiceImpl.saveQuerySellfoxOrder 方法

### 7.3 测试验证

- [ ] 1. 单元测试：类型转换方法
- [ ] 2. 集成测试：单条订单保存
- [ ] 3. 集成测试：订单更新（lastUpdateDate 变化）
- [ ] 4. 集成测试：订单跳过（lastUpdateDate 不变）
- [ ] 5. 性能测试：批量订单处理

---

**文档版本**: v2.0  
**更新时间**: 2026-01-29  
**变更说明**: 
1. 采用 lastUpdateDate 作为变更判断依据，只改不增
2. 新增 amazon_order_evaluation 评价独立表
3. 字符串类型日期/数值字段转换为正确的数据库类型
