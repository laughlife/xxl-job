# XXL-JOB 任务分组改造方案

## 一、需求背景

### 1.1 当前问题
- 一个执行器（JobGroup）下有多个任务，缺乏分组管理
- 无法按业务逻辑对任务进行分组（如：订单流程、支付流程等）
- 查询任务时无法按组筛选
- 需要支持任务组内的顺序执行（A→B→C→D→E→F）

### 1.2 改造目标
- 在执行器下增加"任务组"概念
- 支持任务组的CRUD操作
- 支持按任务组筛选任务
- 支持任务组内任务的顺序编排和执行

## 二、架构设计

### 2.1 层级关系
```
执行器（JobGroup）
  └── 任务组（TaskGroup）
        └── 任务（JobInfo）
              └── 子任务（ChildJob）
```

### 2.2 数据模型
```
xxl_job_group (执行器表)
  ├── id
  ├── app_name
  └── title

xxl_job_task_group (任务组表) [新增]
  ├── id
  ├── job_group_id (关联执行器)
  ├── group_name
  ├── group_desc
  ├── group_order
  ├── add_time
  └── update_time

xxl_job_info (任务表)
  ├── id
  ├── job_group (关联执行器)
  ├── task_group_id (关联任务组) [新增]
  ├── task_order (组内排序) [新增]
  ├── job_desc
  └── ...其他字段
```

## 三、数据库改造

### 3.1 创建任务组表

```sql
-- 创建任务组表
CREATE TABLE `xxl_job_task_group` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `job_group_id` int NOT NULL COMMENT '执行器ID',
  `group_name` varchar(100) NOT NULL COMMENT '任务组名称',
  `group_desc` varchar(255) DEFAULT NULL COMMENT '任务组描述',
  `group_order` int NOT NULL DEFAULT '0' COMMENT '任务组排序',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_job_group_id` (`job_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务组表';
```

### 3.2 修改任务表

```sql
-- 为 xxl_job_info 表添加任务组字段
ALTER TABLE `xxl_job_info` 
ADD COLUMN `task_group_id` int DEFAULT NULL COMMENT '任务组ID' AFTER `job_group`,
ADD COLUMN `task_order` int NOT NULL DEFAULT '0' COMMENT '任务组内排序' AFTER `task_group_id`,
ADD INDEX `idx_task_group_id` (`task_group_id`);
```

### 3.3 初始化数据（可选）

```sql
-- 为现有执行器创建默认任务组
INSERT INTO xxl_job_task_group (job_group_id, group_name, group_desc, group_order, add_time, update_time)
SELECT id, CONCAT(title, '-默认组'), '系统自动创建的默认任务组', 0, NOW(), NOW()
FROM xxl_job_group;

-- 将现有任务关联到默认任务组
UPDATE xxl_job_info ji
INNER JOIN xxl_job_task_group tg ON ji.job_group = tg.job_group_id
SET ji.task_group_id = tg.id
WHERE ji.task_group_id IS NULL;
```

## 四、后端代码改造

### 4.1 实体类改造

#### 4.1.1 新增任务组实体
文件：`xxl-job-admin/src/main/java/com/xxl/job/admin/model/XxlJobTaskGroup.java`

```java
package com.xxl.job.admin.model;

import lombok.Data;
import java.util.Date;

@Data
public class XxlJobTaskGroup {
    private int id;
    private int jobGroupId;
    private String groupName;
    private String groupDesc;
    private int groupOrder;
    private Date addTime;
    private Date updateTime;
}
```

#### 4.1.2 修改任务实体
文件：`xxl-job-admin/src/main/java/com/xxl/job/admin/model/XxlJobInfo.java`

```java
// 添加字段
private Integer taskGroupId;    // 任务组ID
private int taskOrder;          // 任务组内排序
```

### 4.2 Mapper层改造

#### 4.2.1 新增任务组Mapper
文件：`xxl-job-admin/src/main/java/com/xxl/job/admin/mapper/XxlJobTaskGroupMapper.java`

```java
@Mapper
public interface XxlJobTaskGroupMapper {
    List<XxlJobTaskGroup> findByJobGroupId(@Param("jobGroupId") int jobGroupId);
    XxlJobTaskGroup findById(@Param("id") int id);
    int insert(XxlJobTaskGroup taskGroup);
    int update(XxlJobTaskGroup taskGroup);
    int delete(@Param("id") int id);
    int countJobsByTaskGroupId(@Param("taskGroupId") int taskGroupId);
}
```

#### 4.2.2 修改任务Mapper
文件：`xxl-job-admin/src/main/java/com/xxl/job/admin/mapper/XxlJobInfoMapper.java`

```java
// 在 pageList 和 pageListCount 方法中添加 taskGroupId 参数
public List<XxlJobInfo> pageList(..., @Param("taskGroupId") Integer taskGroupId);
public int pageListCount(..., @Param("taskGroupId") Integer taskGroupId);
```

#### 4.2.3 修改任务Mapper XML
文件：`xxl-job-admin/src/main/resources/mapper/XxlJobInfoMapper.xml`

```xml
<!-- 在 pageList 的 WHERE 条件中添加 -->
<if test="taskGroupId != null">
    AND t.task_group_id = #{taskGroupId}
</if>

<!-- 在 INSERT 和 UPDATE 中添加 task_group_id 和 task_order 字段 -->
```

### 4.3 Service层改造

#### 4.3.1 新增任务组Service
文件：`xxl-job-admin/src/main/java/com/xxl/job/admin/service/XxlJobTaskGroupService.java`

```java
@Service
public class XxlJobTaskGroupService {
    
    @Resource
    private XxlJobTaskGroupMapper taskGroupMapper;
    
    @Resource
    private XxlJobInfoMapper jobInfoMapper;
    
    /**
     * 查询执行器下的所有任务组
     */
    public List<XxlJobTaskGroup> findByJobGroupId(int jobGroupId) {
        return taskGroupMapper.findByJobGroupId(jobGroupId);
    }
    
    /**
     * 新增任务组
     */
    public Response<String> add(XxlJobTaskGroup taskGroup) {
        // 参数校验
        if (taskGroup.getJobGroupId() <= 0) {
            return Response.ofFail("执行器ID不能为空");
        }
        if (StringTool.isBlank(taskGroup.getGroupName())) {
            return Response.ofFail("任务组名称不能为空");
        }
        
        // 保存
        int ret = taskGroupMapper.insert(taskGroup);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail();
    }
    
    /**
     * 更新任务组
     */
    public Response<String> update(XxlJobTaskGroup taskGroup) {
        // 参数校验
        if (taskGroup.getId() <= 0) {
            return Response.ofFail("任务组ID不能为空");
        }
        
        // 更新
        int ret = taskGroupMapper.update(taskGroup);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail();
    }
    
    /**
     * 删除任务组
     */
    public Response<String> delete(int id) {
        // 检查是否有关联任务
        int count = taskGroupMapper.countJobsByTaskGroupId(id);
        if (count > 0) {
            return Response.ofFail("该任务组下还有" + count + "个任务，无法删除");
        }
        
        // 删除
        int ret = taskGroupMapper.delete(id);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail();
    }
    
    /**
     * 批量执行任务组内的任务（按顺序）
     */
    public Response<String> executeTaskGroup(int taskGroupId) {
        // 查询任务组下的所有任务，按 task_order 排序
        List<XxlJobInfo> jobs = jobInfoMapper.findByTaskGroupIdOrderByTaskOrder(taskGroupId);
        
        if (jobs == null || jobs.isEmpty()) {
            return Response.ofFail("任务组下没有任务");
        }
        
        // 使用子任务链式执行
        // 将任务按顺序设置为子任务关系：A.childJobId=B, B.childJobId=C, ...
        for (int i = 0; i < jobs.size() - 1; i++) {
            XxlJobInfo currentJob = jobs.get(i);
            XxlJobInfo nextJob = jobs.get(i + 1);
            currentJob.setChildJobId(String.valueOf(nextJob.getId()));
            jobInfoMapper.update(currentJob);
        }
        
        // 触发第一个任务
        XxlJobInfo firstJob = jobs.get(0);
        // 调用触发接口...
        
        return Response.ofSuccess();
    }
}
```

### 4.4 Controller层改造

#### 4.4.1 新增任务组Controller
文件：`xxl-job-admin/src/main/java/com/xxl/job/admin/controller/biz/JobTaskGroupController.java`

```java
@Controller
@RequestMapping("/taskgroup")
public class JobTaskGroupController {
    
    @Resource
    private XxlJobTaskGroupService taskGroupService;
    
    /**
     * 查询任务组列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public Response<List<XxlJobTaskGroup>> list(@RequestParam("jobGroupId") int jobGroupId) {
        List<XxlJobTaskGroup> list = taskGroupService.findByJobGroupId(jobGroupId);
        return Response.ofSuccess(list);
    }
    
    /**
     * 新增任务组
     */
    @RequestMapping("/add")
    @ResponseBody
    public Response<String> add(XxlJobTaskGroup taskGroup) {
        return taskGroupService.add(taskGroup);
    }
    
    /**
     * 更新任务组
     */
    @RequestMapping("/update")
    @ResponseBody
    public Response<String> update(XxlJobTaskGroup taskGroup) {
        return taskGroupService.update(taskGroup);
    }
    
    /**
     * 删除任务组
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Response<String> delete(@RequestParam("id") int id) {
        return taskGroupService.delete(id);
    }
    
    /**
     * 执行任务组
     */
    @RequestMapping("/execute")
    @ResponseBody
    public Response<String> execute(@RequestParam("id") int id) {
        return taskGroupService.executeTaskGroup(id);
    }
}
```

#### 4.4.2 修改任务Controller
文件：`xxl-job-admin/src/main/java/com/xxl/job/admin/controller/biz/JobInfoController.java`

在 `pageList` 方法中添加 `taskGroupId` 参数：

```java
@RequestMapping("/pageList")
@ResponseBody
public Map<String, Object> pageList(..., Integer taskGroupId) {
    // 传递 taskGroupId 到 Mapper
    List<XxlJobInfo> list = xxlJobInfoMapper.pageList(
        offset, pagesize, jobGroup, triggerStatus, 
        jobDesc, executorHandler, author, taskGroupId
    );
    // ...
}
```

## 五、前端页面改造

### 5.1 任务列表页面改造
文件：`xxl-job-admin/src/main/resources/templates/biz/job.list.ftl`

#### 5.1.1 添加任务组筛选下拉框

```html
<!-- 在查询区域添加任务组筛选 -->
<div class="col-2">
    <div class="input-group">
        <span class="input-group-addon">任务组</span>
        <select class="form-control" id="taskGroupId">
            <option value="">全部</option>
            <!-- 动态加载任务组列表 -->
        </select>
    </div>
</div>
```

#### 5.1.2 修改查询参数

```javascript
// 在 queryParams 中添加 taskGroupId
queryParams: function (params) {
    var obj = {};
    obj.jobGroup = $('#jobGroup').val();
    obj.taskGroupId = $('#taskGroupId').val(); // 新增
    obj.triggerStatus = $('#triggerStatus').val();
    // ...
    return obj;
}
```

#### 5.1.3 添加任务组联动

```javascript
// 执行器改变时，加载对应的任务组
$('#jobGroup').on('change', function(){
    var jobGroup = $(this).val();
    
    // 加载任务组列表
    $.ajax({
        url: base_url + "/taskgroup/list",
        data: { jobGroupId: jobGroup },
        success: function(data) {
            if (data.code === 200) {
                var html = '<option value="">全部</option>';
                $.each(data.data, function(i, item) {
                    html += '<option value="' + item.id + '">' + item.groupName + '</option>';
                });
                $('#taskGroupId').html(html);
            }
        }
    });
    
    // 刷新任务列表
    $('#data_filter .searchBtn').click();
});
```

### 5.2 任务编辑页面改造

#### 5.2.1 添加任务组选择和排序

```html
<!-- 在任务编辑表单中添加 -->
<div class="row mb-3">
    <label class="col-sm-2 col-form-label">任务组</label>
    <div class="col-sm-4">
        <select class="form-control" name="taskGroupId">
            <option value="">无</option>
            <!-- 动态加载 -->
        </select>
    </div>
    
    <label class="col-sm-2 col-form-label">组内排序</label>
    <div class="col-sm-4">
        <input type="number" class="form-control" name="taskOrder" 
               placeholder="数字越小越靠前" value="0">
    </div>
</div>
```

### 5.3 新增任务组管理页面
文件：`xxl-job-admin/src/main/resources/templates/biz/taskgroup.list.ftl`

```html
<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <title>任务组管理</title>
</head>
<body>
    <!-- 任务组列表 -->
    <div class="box">
        <div class="box-header">
            <button class="btn btn-primary" id="addTaskGroup">新增任务组</button>
        </div>
        <div class="box-body">
            <table id="taskGroupTable" class="table table-bordered">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>任务组名称</th>
                        <th>描述</th>
                        <th>排序</th>
                        <th>任务数量</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
    </div>
    
    <!-- 新增/编辑模态框 -->
    <div class="modal" id="taskGroupModal">
        <!-- 表单内容 -->
    </div>
</body>
</html>
```

## 六、使用示例

### 6.1 创建任务组

```sql
-- 创建"订单处理流程"任务组
INSERT INTO xxl_job_task_group (job_group_id, group_name, group_desc, group_order)
VALUES (4, '订单处理流程', '订单从创建到完成的完整流程', 1);
```

### 6.2 创建任务并关联任务组

```sql
-- 假设任务组ID为1
-- 创建任务A（订单创建）
INSERT INTO xxl_job_info (job_group, task_group_id, task_order, job_desc, executor_handler, ...)
VALUES (4, 1, 1, '订单创建', 'createOrderHandler', ...);

-- 创建任务B（订单支付）
INSERT INTO xxl_job_info (job_group, task_group_id, task_order, job_desc, executor_handler, ...)
VALUES (4, 1, 2, '订单支付', 'payOrderHandler', ...);

-- 创建任务C（订单发货）
INSERT INTO xxl_job_info (job_group, task_group_id, task_order, job_desc, executor_handler, ...)
VALUES (4, 1, 3, '订单发货', 'shipOrderHandler', ...);
```

### 6.3 查询任务组下的任务

```sql
SELECT * FROM xxl_job_info 
WHERE task_group_id = 1 
ORDER BY task_order ASC;
```

### 6.4 顺序执行任务组

方式1：使用子任务链（推荐）
```
任务A.child_jobid = 任务B的ID
任务B.child_jobid = 任务C的ID
任务C.child_jobid = 任务D的ID
...
```

方式2：使用工作流引擎（需要额外开发）
- 定义工作流模板
- 按照任务组的 task_order 顺序执行
- 支持条件分支、并行执行等高级特性

## 七、改造步骤

### 7.1 数据库改造（第一步）
1. 执行建表SQL创建 `xxl_job_task_group` 表
2. 执行ALTER TABLE添加 `task_group_id` 和 `task_order` 字段
3. 执行初始化数据SQL（可选）

### 7.2 后端代码改造（第二步）
1. 创建 `XxlJobTaskGroup` 实体类
2. 修改 `XxlJobInfo` 实体类
3. 创建 `XxlJobTaskGroupMapper` 接口和XML
4. 修改 `XxlJobInfoMapper` 接口和XML
5. 创建 `XxlJobTaskGroupService`
6. 创建 `JobTaskGroupController`
7. 修改 `JobInfoController`

### 7.3 前端页面改造（第三步）
1. 修改 `job.list.ftl` 添加任务组筛选
2. 修改任务编辑表单添加任务组选择
3. 创建 `taskgroup.list.ftl` 任务组管理页面
4. 添加相关JavaScript交互逻辑

### 7.4 测试验证（第四步）
1. 测试任务组的CRUD操作
2. 测试任务的任务组关联
3. 测试按任务组筛选任务
4. 测试任务组的顺序执行

## 八、注意事项

### 8.1 兼容性
- 新增字段设置为可空，保证现有任务不受影响
- 提供数据迁移脚本，将现有任务关联到默认任务组

### 8.2 性能优化
- 为 `task_group_id` 添加索引
- 任务组列表使用缓存
- 分页查询避免全表扫描

### 8.3 扩展性
- 预留任务组配置字段（JSON格式）
- 支持任务组模板功能
- 支持任务组的导入导出

## 九、后续优化方向

1. **工作流引擎**：支持更复杂的任务编排（条件分支、并行、循环等）
2. **任务组模板**：预定义常用的任务组模板
3. **可视化编排**：拖拽式任务流程设计
4. **任务组监控**：任务组执行进度、成功率统计
5. **任务组版本管理**：支持任务组的版本控制和回滚

---

**文档版本**：v1.0  
**创建时间**：2026-01-27  
**作者**：李伟
