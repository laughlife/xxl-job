package com.xxl.job.executor.biz.sellfox.mapper;

import org.apache.ibatis.annotations.Delete;

import com.xxl.job.executor.biz.sellfox.entity.SellfoxUserDO;
import com.xxl.job.executor.mybatis.mapper.BaseMapperX;

/**
 * <p>
 * 赛狐用户表 Mapper 接口
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
public interface SellfoxUserMapper extends BaseMapperX<SellfoxUserDO> {
    
    @Delete("TRUNCATE TABLE erp_sellfox_user")
    void truncateTable();
}
