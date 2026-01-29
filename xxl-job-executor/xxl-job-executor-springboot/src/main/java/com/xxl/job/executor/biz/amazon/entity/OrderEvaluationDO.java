package com.xxl.job.executor.biz.amazon.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 亚马逊订单评价表
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Getter
@Setter
@ToString
@TableName("amazon_order_evaluation")
public class OrderEvaluationDO implements Serializable {

    private static final long serialVersionUID = 1L;

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
