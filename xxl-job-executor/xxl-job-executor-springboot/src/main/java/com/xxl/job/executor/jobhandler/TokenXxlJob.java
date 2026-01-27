package com.xxl.job.executor.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.executor.biz.token.mapper.TokenMapper;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.xxl.job.executor.service.YourBusinessService;

/**
 * 数据库交互任务示例
 */
@Component
@Slf4j
public class TokenXxlJob {

    // 1. 注入你的业务Service或Mapper
    // @Autowired
    // private YourBusinessService yourBusinessService;

    @Autowired
    private TokenMapper tokenMapper;
    /**
     * 示例：处理过期订单
     */
    @XxlJob("refreshTokenJobHandler")
    public void refreshTokenJobHandler() throws Exception {
        XxlJobHelper.log("开始刷新Token信息...");

        // 2. 获取调度中心传递的参数（例如处理日期）
        // String param = XxlJobHelper.getJobParam();
        // XxlJobHelper.log("任务参数: " + param);

        try {
            
        } catch (Exception e) {
            log.error("任务执行异常", e);
            XxlJobHelper.handleFail("任务执行失败: " + e.getMessage());
        }
    }

    public boolean getOrRefreshToken() {
        List tokens = tokenMapper.selectList("name", "虎佈");
        int count = tokens.size();
        if (count == 0) {
            return fetchAndStoreToken();
        }
        //hierarchy
        TokenDO token = (TokenDO) tokens.get(0);
        long currentTime = System.currentTimeMillis();
        if (currentTime < token.getExpiresTime() - 60 * 60 * 1000) {
            Date date = new Date(token.getExpiresTime());
            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            log.info("虎佈token处于有效期，到期时间为:{}", format);
            return true; // Token 仍然有效
        }
        TokenDO newToken = getTokenByNet();
        log.info("更新虎佈token");
        return newToken != null && tokenMapper.updateByName(newToken) > 0;
        return false;
    }



}
