package com.xxl.job.executor.biz.fba.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * FBA追踪信息
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Getter
@Setter
@ToString
@TableName("erp_fba_spd_box")
public class FbaSpdBoxDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 货件ID
     */
    @TableField("shipment_id")
    private Long shipmentId;

    /**
     * 箱号
     */
    @TableField("box_id")
    private String boxId;

    /**
     * 物流追踪号
     */
    @TableField("tracking_id")
    private String trackingId;

    /**
     * 追踪号状态
     */
    @TableField("tracking_number_validation_status")
    private String trackingNumberValidationStatus;

    /**
     * 索引
     */
    @TableField("idx")
    private Integer idx;

    /**
     * 需同步
     */
    @TableField("need_sync")
    private Integer needSync;

    /**
     * 是否同步
     */
    @TableField("synced")
    private String synced;

    /**
     * 实重
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 重量单位
     */
    @TableField("weight_unit")
    private String weightUnit;

    /**
     * 材重
     */
    @TableField("actual_weight")
    private BigDecimal actualWeight;

    /**
     * 计费重量
     */
    @TableField("charge_weight")
    private BigDecimal chargeWeight;

    /**
     * 体积
     */
    @TableField("volume")
    private BigDecimal volume;

    /**
     * 长
     */
    @TableField("length")
    private BigDecimal length;

    /**
     * 长度单位
     */
    @TableField("length_unit")
    private String lengthUnit;

    /**
     * 宽
     */
    @TableField("width")
    private BigDecimal width;

    /**
     * 高
     */
    @TableField("height")
    private BigDecimal height;

    /**
     * 围长
     */
    @TableField("circ_length")
    private BigDecimal circLength;

    /**
     * 创建者（这个是运营，谁的订单，这里就是谁）
     */
    @TableField("creator")
    private String creator;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

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
}
