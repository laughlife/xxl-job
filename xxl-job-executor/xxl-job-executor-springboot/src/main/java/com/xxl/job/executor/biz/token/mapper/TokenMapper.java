package com.xxl.job.executor.biz.token.mapper;

import com.xxl.job.executor.biz.token.entity.TokenDO;
import com.xxl.job.executor.mybatis.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * token表 Mapper 接口
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-27
 */
public interface TokenMapper extends BaseMapperX<TokenDO> {

    @Update("update ruiyi_token set access_token=#{token.accessToken}, refresh_token=#{token.refreshToken}, save_time=#{token.saveTime}, expires_time=#{token.expiresTime} where name=#{token.name}")
    int updateByName(@Param("token")TokenDO token);

    @Select("select access_token from ruiyi_token where name = #{name} ")
    String getAccessTokenByName(@Param("name")String name);
}
