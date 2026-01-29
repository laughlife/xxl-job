package com.xxl.job.executor.biz.amazon.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 亚马逊订单商品(高频字段，按订单快照存档)
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Getter
@Setter
@ToString
@TableName("amazon_order_product")
public class OrderProductDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Amazon订单号
     */
    @TableField("amazon_order_id")
    private String amazonOrderId;

    /**
     * 你系统商品明细ID
     */
    @TableField("source_item_id")
    private Long sourceItemId;

    /**
     * Amazon订单商品ID
     */
    @TableField("order_item_id")
    private String orderItemId;

    /**
     * 业务PUID
     */
    @TableField("puid")
    private Long puid;

    /**
     * 店铺ID
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 内部商品ID
     */
    @TableField("commodity_id")
    private Long commodityId;

    /**
     * 内部商品SKU
     */
    @TableField("commodity_sku")
    private String commoditySku;

    /**
     * 内部商品名称
     */
    @TableField("commodity_name")
    private String commodityName;

    /**
     * ASIN
     */
    @TableField("asin")
    private String asin;

    /**
     * 父ASIN
     */
    @TableField("parent_asin")
    private String parentAsin;

    /**
     * 卖家SKU
     */
    @TableField("seller_sku")
    private String sellerSku;

    /**
     * ListingId
     */
    @TableField("listing_id")
    private String listingId;

    /**
     * 商品标题
     */
    @TableField("title")
    private String title;

    /**
     * 商品图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 商品链接URL
     */
    @TableField("asin_url")
    private String asinUrl;

    /**
     * 下单数量
     */
    @TableField("quantity_ordered")
    private Integer quantityOrdered;

    /**
     * 已发货数量
     */
    @TableField("quantity_shipped")
    private Integer quantityShipped;

    /**
     * 商品金额币种
     */
    @TableField("item_price_currency")
    private String itemPriceCurrency;

    /**
     * 商品金额
     */
    @TableField("item_price_amount")
    private BigDecimal itemPriceAmount;

    /**
     * 商品税额
     */
    @TableField("item_tax_amount")
    private BigDecimal itemTaxAmount;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;
}
