package com.xxl.job.admin.mapper;

import com.xxl.job.admin.python.XxlJobPython;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @author xuxueli 2019-05-04 16:44:59
 */
@Mapper
public interface XxlJobPythonMapper {
	
	public List<XxlJobPython> findAll();

	public List<XxlJobPython> pageList(@Param("offset") int offset,
									   @Param("pagesize") int pagesize,
									   @Param("name") String name,
									   @Param("version") String version);

	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("name") String name,
							 @Param("version") String version);

	public XxlJobPython loadById(@Param("id") int id);

	public int save(XxlJobPython xxlJobPython);

	public int update(XxlJobPython xxlJobPython);

	public int delete(@Param("id") int id);

	/**
	 * 按版本查重
	 * @param version
	 * @return
	 */
	public XxlJobPython loadByVersion(@Param("version") String version);


}
