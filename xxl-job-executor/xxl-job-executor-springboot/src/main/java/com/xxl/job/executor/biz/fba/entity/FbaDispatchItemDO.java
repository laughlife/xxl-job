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
import java.time.LocalDateTime;

/**
 * <p>
 * 子运单明细表
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Getter
@Setter
@ToString
@TableName("erp_fba_dispatch_item")
public class FbaDispatchItemDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 派送单ID
     */
    @TableField("dispatch_id")
    private Long dispatchId;

    /**
     * FBA货件ID
     */
    @TableField("fba_shipment_id")
    private Long fbaShipmentId;

    /**
     * 主运单号
     */
    @TableField("no")
    private String no;

    /**
     * 运单ID
     */
    @TableField("no_id")
    private Long noId;

    /**
     * 运单类型
     */
    @TableField("no_type")
    private String noType;

    /**
     * 公司ID
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 子运单号
     */
    @TableField("child_jobno")
    private String childJobno;

    /**
     * 下载地址列表
     */
    @TableField("download_urls")
    private String downloadUrls;

    /**
     * 顺序
     */
    @TableField("idx")
    private Integer idx;

    /**
     * 是否已解析
     */
    @TableField("analyzed")
    private Boolean analyzed;

    /**
     * 是否已同步
     */
    @TableField("synced")
    private Boolean synced;

    /**
     * 是否删除
     */
    @TableField("isdel")
    private Boolean isdel;

    /**
     * 创建时间
     */
    @TableField("create_datetime")
    private LocalDateTime createDatetime;

    /**
     * 创建人姓名
     */
    @TableField("create_user_name")
    private String createUserName;

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
