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
 * FBA地址表
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Getter
@Setter
@ToString
@TableName("erp_fba_address")
public class FbaAddressDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 公司名
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 电话
     */
    @TableField("phone_number")
    private String phoneNumber;

    /**
     * 邮件
     */
    @TableField("email")
    private String email;

    /**
     * 地址1
     */
    @TableField("address_line1")
    private String addressLine1;

    /**
     * 地址2
     */
    @TableField("address_line2")
    private String addressLine2;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 省份
     */
    @TableField("district_or_county")
    private String districtOrCounty;

    /**
     * 城市简拼
     */
    @TableField("state_or_province_code")
    private String stateOrProvinceCode;

    /**
     * 邮编
     */
    @TableField("postal_code")
    private String postalCode;

    /**
     * 国家
     */
    @TableField("country_code")
    private String countryCode;

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
     * 创建者（这个是运营，谁的订单，这里就是谁）
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
