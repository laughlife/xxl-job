# Amazon订单数据库表结构文档

## 一、表清单

| 序号 | 表名 | 说明 |
|------|------|------|
| 1 | amazon_order | 订单主表 |
| 2 | amazon_order_ext | 订单扩展表 |
| 3 | amazon_order_evaluation | 订单评价表 |
| 4 | amazon_order_product | 订单商品表 |
| 5 | amazon_order_product_ext | 订单商品扩展表 |
| 6 | amazon_buyer | 买家信息表 |

## 二、表关系图

```
                              ┌─────────────────┐
                              │  amazon_buyer   │
                              │    (买家表)      │
                              └────────┬────────┘
                                       │ 1
                                       │
                                       │ N
┌─────────────────┐           ┌────────┴────────┐           ┌─────────────────┐
│ amazon_order_ext│◄──────────│  amazon_order   │──────────►│amazon_order_    │
│   (订单扩展表)   │     1:1   │    (订单主表)    │    1:1    │  evaluation     │
└─────────────────┘           └────────┬────────┘           │  (订单评价表)    │
                                       │                    └─────────────────┘
                                       │ 1
                                       │
                                       │ N
                              ┌────────┴────────┐
                              │ amazon_order_   │
                              │    product      │
                              │  (订单商品表)    │
                              └────────┬────────┘
                                       │ 1
                                       │
                                       │ 1
                              ┌────────┴────────┐
                              │ amazon_order_   │
                              │  product_ext    │
                              │(订单商品扩展表)  │
                              └─────────────────┘
```

## 三、表关系说明

| 主表 | 从表 | 关系 | 关联字段 |
|------|------|------|----------|
| amazon_buyer | amazon_order | 1:N | buyer.id = order.buyer_id |
| amazon_order | amazon_order_ext | 1:1 | order.amazon_order_id = ext.amazon_order_id |
| amazon_order | amazon_order_evaluation | 1:1 | order.amazon_order_id = evaluation.amazon_order_id |
| amazon_order | amazon_order_product | 1:N | order.amazon_order_id = product.amazon_order_id |
| amazon_order_product | amazon_order_product_ext | 1:1 | product.amazon_order_id + product.source_item_id |

## 四、表字段详情

### 4.1 amazon_order（订单主表）

存储订单核心信息，高频查询字段。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT | 主键ID |
| source_id | BIGINT | Sellfox系统订单ID |
| amazon_order_id | VARCHAR(32) | Amazon订单号（唯一键） |
| buyer_id | BIGINT | 买家ID，关联amazon_buyer表 |
| puid | BIGINT | 业务PUID |
| shop_id | BIGINT | 店铺ID |
| shop_name | VARCHAR(128) | 店铺名称 |
| marketplace_id | VARCHAR(32) | 站点MarketplaceId |
| marketplace_cn | VARCHAR(32) | 站点中文名 |
| purchase_date | DATETIME | 下单时间 |
| last_update_date | DATETIME | 最后更新时间（变更判断字段） |
| payments_date | DATETIME | 回款/结算时间 |
| order_status | VARCHAR(32) | 订单状态 |
| order_type | VARCHAR(32) | 订单类型 |
| fulfillment_channel | VARCHAR(16) | 配送渠道 |
| order_total_currency | VARCHAR(8) | 订单总金额币种 |
| order_total_amount | DECIMAL(12,4) | 订单总金额 |
| order_profit | DECIMAL(12,4) | 订单利润 |
| profit | DECIMAL(12,4) | 利润 |
| profit_rate | DECIMAL(8,4) | 利润率 |
| is_return_order | TINYINT | 是否退货订单 |
| is_business_order | TINYINT | 是否企业订单 |
| is_prime | TINYINT | 是否Prime订单 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 是否删除 |
| updater | VARCHAR(64) | 更新者 |
| creator | VARCHAR(64) | 创建者 |

### 4.2 amazon_order_ext（订单扩展表）

存储订单低频字段和扩展信息。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| amazon_order_id | VARCHAR(32) | Amazon订单号（唯一键） |
| last_update_date | DATETIME | 最后更新时间 |
| captured_at | DATETIME | 快照入库时间 |
| comment | VARCHAR(512) | 订单备注 |
| comment_color | VARCHAR(16) | 备注颜色 |
| earliest_ship_date | DATETIME | 最早发货时间 |
| latest_ship_date | DATETIME | 最晚发货时间 |
| earliest_delivery_date | DATETIME | 最早送达时间 |
| latest_delivery_date | DATETIME | 最晚送达时间 |
| refund_status | TINYINT | 退款状态 |
| refund_date | DATETIME | 退款时间 |
| is_buyer_requested_cancel | TINYINT | 买家是否请求取消 |
| is_replacement_order | TINYINT | 是否补发单 |
| replaced_order_id | VARCHAR(32) | 被替换订单号 |
| is_vine_order | TINYINT | 是否Vine订单 |
| custom_order | TINYINT | 是否自定义订单 |
| promotion_flag | TINYINT | 是否有促销 |
| promotion_ids | VARCHAR(256) | 促销ID集合 |
| order_flag | INT | 订单标记 |
| order_review_status | VARCHAR(32) | 索评状态 |
| upload_feed_status | INT | Feed上传状态 |
| invoice_status_description | VARCHAR(128) | 发票状态描述 |
| payment_method_details | VARCHAR(256) | 支付方式数组 |
| raw_json | TEXT | 完整原始订单JSON |
| is_history | TINYINT | 是否历史订单 |
| is_calculating | TINYINT | 是否正在计算中 |
| low_cost_store | TINYINT | 低成本店铺标记 |
| tax_number | VARCHAR(64) | 税号 |
| capital_currency | VARCHAR(8) | 资金币种 |
| commission_currency | VARCHAR(8) | 佣金币种 |
| capital_date | DATETIME | 资金日期 |
| commission_date | DATETIME | 佣金日期 |
| fbm_cost_origin | DECIMAL(12,4) | FBM原始成本 |
| fbm_cost | DECIMAL(12,4) | FBM成本 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 是否删除 |
| updater | VARCHAR(64) | 更新者 |
| creator | VARCHAR(64) | 创建者 |

### 4.3 amazon_order_evaluation（订单评价表）

存储订单评价相关信息。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| amazon_order_id | VARCHAR(32) | Amazon订单号（唯一键） |
| evaluation | INT | 评价数 |
| evaluation_cost | DECIMAL(12,4) | 评价成本 |
| evaluation_currency | VARCHAR(8) | 评价币种 |
| evaluation_capital | DECIMAL(12,4) | 评价资金 |
| evaluation_commission | DECIMAL(12,4) | 评价佣金 |
| evaluation_ids | TEXT | 评价ID列表JSON |
| evaluation_pay_status | TINYINT | 评价支付状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 4.4 amazon_order_product（订单商品表）

存储订单商品核心信息，高频查询字段。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| amazon_order_id | VARCHAR(32) | Amazon订单号 |
| source_item_id | BIGINT | 源系统商品明细ID |
| order_item_id | VARCHAR(64) | Amazon订单商品ID |
| puid | BIGINT | 业务PUID |
| shop_id | BIGINT | 店铺ID |
| commodity_id | BIGINT | 内部商品ID |
| commodity_sku | VARCHAR(64) | 内部商品SKU |
| commodity_name | VARCHAR(256) | 内部商品名称 |
| asin | VARCHAR(16) | ASIN |
| parent_asin | VARCHAR(16) | 父ASIN |
| seller_sku | VARCHAR(64) | 卖家SKU |
| listing_id | VARCHAR(32) | ListingId |
| title | VARCHAR(512) | 商品标题 |
| image_url | VARCHAR(512) | 商品图片URL |
| asin_url | VARCHAR(256) | 商品链接URL |
| quantity_ordered | INT | 下单数量 |
| quantity_shipped | INT | 已发货数量 |
| item_price_currency | VARCHAR(8) | 商品金额币种 |
| item_price_amount | DECIMAL(12,4) | 商品金额 |
| item_tax_amount | DECIMAL(12,4) | 商品税额 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 是否删除 |
| updater | VARCHAR(64) | 更新者 |
| creator | VARCHAR(64) | 创建者 |

### 4.5 amazon_order_product_ext（订单商品扩展表）

存储订单商品低频字段和扩展信息。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| amazon_order_id | VARCHAR(32) | Amazon订单号 |
| source_item_id | BIGINT | 源系统商品明细ID |
| fnsku | VARCHAR(16) | FNSKU |
| ioss_number | VARCHAR(32) | IOSS号 |
| promotion_ids | VARCHAR(256) | 促销ID字符串 |
| withheld_tax_amount | DECIMAL(12,4) | 预扣税金额 |
| shipping_charge_amount | DECIMAL(12,4) | 运费 |
| shipping_tax_amount | DECIMAL(12,4) | 运费税 |
| gift_wrap_amount | DECIMAL(12,4) | 礼品包装费 |
| gift_wrap_tax_amount | DECIMAL(12,4) | 礼品包装税 |
| promotion_discount | DECIMAL(12,4) | 促销折扣 |
| promotion_discount_currency | VARCHAR(8) | 折扣币种 |
| promotion_discount_amount | DECIMAL(12,4) | 折扣金额 |
| other_amount | DECIMAL(12,4) | 其他金额 |
| fba_per_unit_fulfillment_fee | DECIMAL(12,4) | FBA单件履约费 |
| commission | DECIMAL(12,4) | 佣金 |
| amazon_back_to_article | DECIMAL(12,4) | 亚马逊退款 |
| merge_purchase_cost | DECIMAL(12,4) | 合并采购成本 |
| purchase_cost | DECIMAL(12,4) | 采购成本 |
| head_trip_cost | DECIMAL(12,4) | 头程费用 |
| head_trip_share | TINYINT | 头程分摊 |
| fbm_ship_cost | DECIMAL(12,4) | FBM运费 |
| capital_currency | VARCHAR(8) | 资金币种 |
| capital_date | DATETIME | 资金日期 |
| commission_currency | VARCHAR(8) | 佣金币种 |
| commission_date | DATETIME | 佣金日期 |
| evaluation | INT | 评价数 |
| carrier | VARCHAR(64) | 承运商 |
| track_no | VARCHAR(64) | 运单号 |
| shipment_date | DATETIME | 发货时间 |
| estimated_arrival_date | DATETIME | 预计到达时间 |
| raw_json | TEXT | 完整原始商品行JSON |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 是否删除 |
| updater | VARCHAR(64) | 更新者 |
| creator | VARCHAR(64) | 创建者 |

### 4.6 amazon_buyer（买家信息表）

存储买家基本信息，作为维表使用。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| buyer_email | VARCHAR(128) | 买家邮箱（唯一键） |
| buyer_name | VARCHAR(128) | 买家名称 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 五、索引说明

| 表名 | 索引名 | 索引字段 | 索引类型 |
|------|--------|----------|----------|
| amazon_order | uk_amazon_order_id | amazon_order_id | UNIQUE |
| amazon_order_ext | uk_amazon_order_id | amazon_order_id | UNIQUE |
| amazon_order_evaluation | uk_amazon_order_id | amazon_order_id | UNIQUE |
| amazon_order_product | uk_order_item | amazon_order_id, source_item_id | UNIQUE |
| amazon_order_product_ext | uk_order_item | amazon_order_id, source_item_id | UNIQUE |
| amazon_buyer | uk_buyer_email | buyer_email | UNIQUE |

---

**文档版本**: v1.0  
**创建时间**: 2026-01-29
