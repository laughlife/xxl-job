# XXL-JOB 任务分组功能 - 快速开始

## 5分钟快速上手

### 第一步：执行数据库迁移（1分钟）

```bash
# 连接到MySQL数据库
mysql -uroot -p

# 执行迁移脚本
source doc/db/migration_task_group.sql;

# 验证表是否创建成功
SHOW TABLES LIKE 'xxl_job_task_group';
```

### 第二步：编译并启动服务（2分钟）

```bash
# 编译项目
mvn clean package -DskipTests

# 启动服务（根据实际情况选择启动方式）
java -jar xxl-job-admin/target/xxl-job-admin.jar

# 或者使用已有的启动脚本
./start.sh
```

### 第三步：测试API（2分钟）

#### 方式1：使用测试脚本

**Linux/Mac:**
```bash
chmod +x doc/test_task_group_api.sh
./doc/test_task_group_api.sh
```

**Windows:**
```cmd
doc\test_task_group_api.bat
```

#### 方式2：手动测试

**1. 查询任务组列表**
```bash
curl "http://localhost:8080/xxl-job-admin/taskgroup/list?jobGroupId=1"
```

**2. 创建任务组**
```bash
curl -X POST "http://localhost:8080/xxl-job-admin/taskgroup/add" \
  -d "jobGroupId=1" \
  -d "groupName=我的第一个任务组" \
  -d "groupDesc=测试任务组" \
  -d "groupOrder=1"
```

**3. 查询任务列表（带任务组筛选）**
```bash
curl "http://localhost:8080/xxl-job-admin/jobinfo/pageList?offset=0&pagesize=10&jobGroup=1&triggerStatus=-1&jobDesc=&executorHandler=&author=&taskGroupId=1"
```

## 完整使用流程

### 场景：创建一个订单处理流程

#### 1. 创建任务组
```bash
curl -X POST "http://localhost:8080/xxl-job-admin/taskgroup/add" \
  -d "jobGroupId=1" \
  -d "groupName=订单处理流程" \
  -d "groupDesc=订单从创建到完成的完整流程" \
  -d "groupOrder=1"
```

#### 2. 创建任务（通过Web界面）

**任务A - 订单创建**
- 任务描述：订单创建
- 执行器：选择对应的执行器
- 任务组：订单处理流程
- 组内排序：1
- 执行器Handler：createOrderHandler

**任务B - 订单支付**
- 任务描述：订单支付
- 执行器：选择对应的执行器
- 任务组：订单处理流程
- 组内排序：2
- 执行器Handler：payOrderHandler

**任务C - 订单发货**
- 任务描述：订单发货
- 执行器：选择对应的执行器
- 任务组：订单处理流程
- 组内排序：3
- 执行器Handler：shipOrderHandler

#### 3. 配置顺序执行
```bash
# 假设任务组ID为1
curl -X POST "http://localhost:8080/xxl-job-admin/taskgroup/execute?id=1"
```

#### 4. 触发执行
在Web界面找到"订单创建"任务，点击"执行"按钮，系统会自动按顺序执行：
```
订单创建 -> 订单支付 -> 订单发货
```

## API参考

### 任务组管理

#### 查询任务组列表
```
GET /taskgroup/list?jobGroupId={执行器ID}
```

#### 查询单个任务组
```
GET /taskgroup/get?id={任务组ID}
```

#### 新增任务组
```
POST /taskgroup/add
参数：
  - jobGroupId: 执行器ID（必填）
  - groupName: 任务组名称（必填）
  - groupDesc: 任务组描述（可选）
  - groupOrder: 排序（可选，默认0）
```

#### 更新任务组
```
POST /taskgroup/update
参数：
  - id: 任务组ID（必填）
  - groupName: 任务组名称（必填）
  - groupDesc: 任务组描述（可选）
  - groupOrder: 排序（可选）
```

#### 删除任务组
```
POST /taskgroup/delete?id={任务组ID}
注意：只能删除没有关联任务的任务组
```

#### 配置顺序执行
```
POST /taskgroup/execute?id={任务组ID}
说明：将任务组内的任务配置为顺序执行
```

### 任务管理增强

#### 查询任务列表（支持任务组筛选）
```
GET /jobinfo/pageList
参数：
  - offset: 偏移量
  - pagesize: 每页大小
  - jobGroup: 执行器ID
  - triggerStatus: 触发状态
  - jobDesc: 任务描述（模糊查询）
  - executorHandler: 执行器Handler（模糊查询）
  - author: 作者（模糊查询）
  - taskGroupId: 任务组ID（可选，新增参数）
```

## 常见问题

### Q1: 数据库迁移脚本执行失败？
**A**: 
1. 检查数据库连接是否正常
2. 确认有CREATE TABLE和ALTER TABLE权限
3. 脚本可以重复执行，不会报错

### Q2: API返回404？
**A**: 
1. 确认服务是否启动成功
2. 检查端口是否正确（默认8080）
3. 查看启动日志是否有错误

### Q3: 任务组列表为空？
**A**: 
1. 检查数据库迁移是否成功
2. 确认执行器ID是否正确
3. 查看数据库中是否有数据：`SELECT * FROM xxl_job_task_group;`

### Q4: 顺序执行不生效？
**A**: 
1. 确认任务的task_order设置是否正确
2. 检查是否调用了execute接口配置顺序执行
3. 确认是否手动触发了第一个任务

### Q5: 现有任务是否受影响？
**A**: 
不会受影响。新增字段都是可空的，不传taskGroupId时查询行为与原来一致。

## 下一步

### 开发前端页面
参考 `doc/XXL-JOB任务分组改造方案.md` 中的前端改造部分，开发：
1. 任务列表页面的任务组筛选
2. 任务编辑页面的任务组选择
3. 任务组管理页面

### 查看详细文档
- `doc/TASK_GROUP_REFACTOR_README.md` - 完整的改造说明
- `doc/DEPLOYMENT_CHECKLIST.md` - 部署检查清单
- `doc/REFACTOR_SUMMARY.md` - 改造总结

## 技术支持

如有问题，请查看：
1. 项目文档：`doc/` 目录下的所有文档
2. 原始改造方案：`doc/XXL-JOB任务分组改造方案.md`
3. 联系开发人员：李伟

---

**祝您使用愉快！** 🎉
