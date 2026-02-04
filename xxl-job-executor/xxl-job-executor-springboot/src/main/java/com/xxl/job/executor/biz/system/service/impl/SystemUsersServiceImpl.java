package com.xxl.job.executor.biz.system.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.sellfox.entity.SellfoxUserDO;
import com.xxl.job.executor.biz.sellfox.mapper.SellfoxUserMapper;
import com.xxl.job.executor.biz.system.entity.DeptDO;
import com.xxl.job.executor.biz.system.entity.SystemUsersDO;
import com.xxl.job.executor.biz.system.mapper.DeptMapper;
import com.xxl.job.executor.biz.system.mapper.SystemUsersMapper;
import com.xxl.job.executor.biz.system.service.DeptService;
import com.xxl.job.executor.biz.system.service.SystemUsersService;
import com.xxl.job.executor.biz.token.entity.TokenDO;
import com.xxl.job.executor.biz.token.mapper.TokenMapper;
import com.xxl.job.executor.biz.token.service.TokenService;
import com.xxl.job.executor.mybatis.core.query.LambdaQueryWrapperX;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
@Service
@Slf4j
public class SystemUsersServiceImpl extends ServiceImpl<SystemUsersMapper, SystemUsersDO>
    implements SystemUsersService {
  static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
  static final OkHttpClient httpClient = new OkHttpClient.Builder()
      .connectTimeout(10, TimeUnit.SECONDS)
      .readTimeout(20, TimeUnit.SECONDS)
      .build();

  // 你自己的钉钉配置
  static final String baseUrl = "https://api.dingtalk.com";
  static final String appKey = "dingpt1cifr39if9yjrr";
  static final String appSecret = "HAKgH5lFDAeF1P89MDh_BwR0vzy6DMguOtrK-8sSjjiUdrrVfkuRmJaaom38JRlA";
  static final String agentId = "4009656377";

  @Resource
  TokenService tokenService;

  @Resource
  TokenMapper tokenMapper;

  @Resource
  DeptService deptService;

  @Resource
  DeptMapper deptMapper;

  @Resource
  SystemUsersMapper usersMapper;

  @Resource
  BCryptPasswordEncoder passwordEncoder;

  @Resource
  SellfoxUserMapper sellfoxUserMapper;

  private String getAccessToken() {
    TokenDO token = tokenService.getTokenByName("钉钉");
    if (token == null) {
      token = createNewDingToken();
    }
    if (token.getExpiresTime() == null
        || token.getExpiresTime() < System.currentTimeMillis() + 20 * 60 * 1000) {
      token = getNewDingToken(token);
    }
    return token.getAccessToken();
  }

  private TokenDO createNewDingToken() {
    TokenDO token = new TokenDO();
    token.setName("钉钉");
    token.setAppKey(appKey);
    token.setAppSecret(appSecret);
    token.setAgentId(agentId);
    tokenMapper.insertOrUpdate(token);
    return token;
  }

  private TokenDO getNewDingToken(TokenDO token) {
    String getTokenUrl = baseUrl + "/v1.0/oauth2/accessToken";
    JSONObject params = new JSONObject();
    params.put("appKey", token.getAppKey());
    params.put("appSecret", token.getAppSecret());
    RequestBody requestBody = RequestBody.create(params.toString(), mediaType);
    Request request = new Request.Builder().url(getTokenUrl).post(requestBody).build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("请求钉钉accessToken失败，错误信息如下：{}", response);
      }
      String bodyString = Objects.requireNonNull(response.body()).string();
      if (StringUtils.isNotBlank(bodyString)) {
        JSONObject tokenJSON = JSON.parseObject(bodyString);
        if (tokenJSON.containsKey("accessToken") && tokenJSON.containsKey("expireIn")) {
          // 更新token
          String accessToken = tokenJSON.getString("accessToken");
          Long expireIn = tokenJSON.getLong("expireIn");
          token.setAccessToken(accessToken);
          token.setExpiresTime(expireIn * 1000L + System.currentTimeMillis());
          tokenMapper.insertOrUpdate(token);
        } else {
          log.error("请求钉钉accessToken失败，错误信息如下：{}", tokenJSON);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("请求钉钉accessToken失败，错误信息如下：{}", e);
    }
    return token;
  }

  @Override
  public String syncDingTalkDept() {
    String accessToken = getAccessToken();
    String baseDept = "1";
    // 查询所有的部门列表信息
    JSONArray allDeptArray = new JSONArray();
    JSONArray baseDeptArray = getDeptById(accessToken, baseDept);
    allDeptArray.addAll(baseDeptArray);
    baseDeptArray.forEach(
        dept -> {
          JSONObject deptJSON = (JSONObject) dept;
          String deptId = deptJSON.getString("dept_id");
          JSONArray deptArray = getDeptById(accessToken, deptId);
          if (deptArray != null) {
            allDeptArray.addAll(deptArray);
          }
        });
    deptMapper.update(new UpdateWrapper<DeptDO>().eq("locked", 0).set("status", 1));
    analyzeDept(allDeptArray);
    deptMapper.delete(
        new LambdaQueryWrapper<DeptDO>().eq(DeptDO::getLocked, "0").eq(DeptDO::getStatus, 1));
    return "";
  }

  private JSONArray getDeptById(String accessToken, String deptId) {

    HttpUrl url = Objects.requireNonNull(
        HttpUrl.parse("https://oapi.dingtalk.com/topapi/v2/department/listsub"))
        .newBuilder()
        .addQueryParameter("access_token", accessToken)
        .build();
    JSONObject params = new JSONObject();
    params.put("dept_id", deptId);

    RequestBody requestBody = RequestBody.create(params.toString(), mediaType);
    Request request = new Request.Builder().url(url).post(requestBody).build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("请求钉钉部门信息失败，错误信息如下：{}", response);
      }
      JSONObject body = JSON.parseObject(Objects.requireNonNull(response.body()).string());
      if (body.getInteger("errcode") == 0) {
        return body.getJSONArray("result");
      }
      return null;
    } catch (IOException e) {
      throw new RuntimeException("请求钉钉部门信息失败，错误信息如下：{}", e);
    }
  }

  private void analyzeDept(JSONArray deptArray) {
    deptArray.forEach(
        deptJSON -> {
          JSONObject dept = (JSONObject) deptJSON;
          Long deptId = dept.getLong("dept_id");
          DeptDO deptDO = deptService.getDept(deptId);
          if (deptDO == null) {
            deptDO = new DeptDO();
          }
          if (deptDO.getLocked() != null && deptDO.getLocked() == 1) {
            return;
          }
          deptDO.setId(deptId);
          deptDO.setName(dept.getString("name"));
          deptDO.setParentId(dept.getLong("parent_id"));
          deptDO.setStatus((byte) 0);
          deptService.insertOrUpdateDept(deptDO);
        });
  }

  @Transactional
  @Override
  public String syncDingTalkUsers() {
    String accessToken = getAccessToken();
    List<DeptDO> deptList = deptService.getAllDept();
    for (DeptDO dept : deptList) {
      usersMapper.update(
          new UpdateWrapper<SystemUsersDO>()
              .eq("locked", 0)
              .eq("dept_id", dept.getId())
              .set("status", 1));
      JSONArray deptArray = getUserByDeptId(accessToken, dept.getId());
      if (deptArray != null) {
        analyzeUser(deptArray, dept.getId());
      }
      usersMapper.delete(
          new LambdaQueryWrapper<SystemUsersDO>()
              .eq(SystemUsersDO::getLocked, 0)
              .eq(SystemUsersDO::getDeptId, dept.getId())
              .eq(SystemUsersDO::getStatus, 1));
    }

    return "";
  }

  private JSONArray getUserByDeptId(String accessToken, Long id) {
    HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://oapi.dingtalk.com/topapi/v2/user/list"))
        .newBuilder()
        .addQueryParameter("access_token", accessToken)
        .build();
    JSONObject params = new JSONObject();
    params.put("dept_id", id);
    params.put("cursor", 0);
    params.put("size", 100);

    RequestBody requestBody = RequestBody.create(params.toString(), mediaType);
    Request request = new Request.Builder().url(url).post(requestBody).build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("请求钉钉用户信息失败，错误信息如下：{}", response);
      }
      JSONObject body = JSON.parseObject(Objects.requireNonNull(response.body()).string());
      if (body.getInteger("errcode") == 0) {
        JSONObject result = body.getJSONObject("result");
        if (!result.isEmpty()) {
          return result.getJSONArray("list");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("请求钉钉用户信息失败，错误信息如下：{}", e);
    }
    return null;
  }

  private void analyzeUser(JSONArray userArray, Long deptId) {
    userArray.forEach(userJSON -> {
      JSONObject user = (JSONObject) userJSON;
      String userId = user.getString("userid");
      String name = user.getString("name");
      String mobile = user.getString("mobile");
      String avatar = user.getString("avatar");
      SystemUsersDO userDO = usersMapper.selectOne(
          new LambdaQueryWrapperX<SystemUsersDO>()
              .eq(SystemUsersDO::getDeptId, deptId)
              .eq(SystemUsersDO::getDingtalkId, userId)
              .eq(SystemUsersDO::getDeleted, false)
              .last("LIMIT 1"));
      if (userDO == null) {
        userDO = new SystemUsersDO();
        userDO.setDeptId(deptId);
        userDO.setDingtalkId(userId);
        userDO.setNickname(name);
        userDO.setStatus((byte) 0);
        userDO.setLocked(0);
        userDO.setMobile(mobile);
        userDO.setAvatar(avatar);
        userDO.setUsername(mobile);
        userDO.setSex((byte) 1);
        userDO.setPassword(passwordEncoder.encode("123456"));
      } else if (userDO.getLocked() != 1) {
        // 如果状态为锁定，则不更新
        userDO.setNickname(name);
        userDO.setStatus((byte) 0);
        userDO.setDingtalkId(userId);
      }
      usersMapper.insertOrUpdate(userDO);
    });
  }

  @Override
  public String checkUserSellfoxId() {
    // 查询所有的用户信息
    List<SystemUsersDO> userList = usersMapper.selectList(
        new LambdaQueryWrapperX<SystemUsersDO>().eq(SystemUsersDO::getLocked, 0).isNull(SystemUsersDO::getSellfoxId));
    for (SystemUsersDO user : userList) {
      // 查询用户赛狐ID
      String sellfoxId = user.getSellfoxId();
      if (StringUtils.isBlank(sellfoxId)) {
        // 查询用户赛狐ID
        String nickname = user.getNickname();
        if (StringUtils.isBlank(nickname)) {
          continue;
        }
        // 查询用户赛狐ID
        SellfoxUserDO sellfoxUser = sellfoxUserMapper.selectOne(
            new LambdaQueryWrapperX<SellfoxUserDO>()
                .eq(SellfoxUserDO::getNickname, nickname)
        );
        if (sellfoxUser != null) {
          user.setSellfoxId(sellfoxUser.getId());
          usersMapper.insertOrUpdate(user);
        }
      }
    }
    return null;
  }

}
