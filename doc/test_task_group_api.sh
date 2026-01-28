#!/bin/bash

# XXL-JOB 任务分组功能测试脚本
# 使用前请修改BASE_URL为实际的服务地址

BASE_URL="http://localhost:8080/xxl-job-admin"

echo "========================================="
echo "XXL-JOB 任务分组功能测试"
echo "========================================="
echo ""

# 1. 测试查询任务组列表
echo "1. 测试查询任务组列表（执行器ID=1）"
curl -s "${BASE_URL}/taskgroup/list?jobGroupId=1" | python -m json.tool
echo ""
echo ""

# 2. 测试新增任务组
echo "2. 测试新增任务组"
curl -s -X POST "${BASE_URL}/taskgroup/add" \
  -d "jobGroupId=1" \
  -d "groupName=测试任务组" \
  -d "groupDesc=这是一个测试任务组" \
  -d "groupOrder=1" | python -m json.tool
echo ""
echo ""

# 3. 再次查询任务组列表
echo "3. 再次查询任务组列表（验证新增）"
curl -s "${BASE_URL}/taskgroup/list?jobGroupId=1" | python -m json.tool
echo ""
echo ""

# 4. 测试查询任务列表（不带任务组筛选）
echo "4. 测试查询任务列表（不带任务组筛选）"
curl -s "${BASE_URL}/jobinfo/pageList?offset=0&pagesize=10&jobGroup=1&triggerStatus=-1&jobDesc=&executorHandler=&author=" | python -m json.tool
echo ""
echo ""

# 5. 测试查询任务列表（带任务组筛选）
echo "5. 测试查询任务列表（带任务组筛选，taskGroupId=1）"
curl -s "${BASE_URL}/jobinfo/pageList?offset=0&pagesize=10&jobGroup=1&triggerStatus=-1&jobDesc=&executorHandler=&author=&taskGroupId=1" | python -m json.tool
echo ""
echo ""

echo "========================================="
echo "测试完成"
echo "========================================="
