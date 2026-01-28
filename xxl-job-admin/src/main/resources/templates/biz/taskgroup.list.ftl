<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>

	<!-- 1-style start -->
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.css">
	<!-- 1-style end -->

</head>
<body class="hold-transition" style="background-color: #ecf0f5;">
<div class="wrapper">
	<section class="content">

		<!-- 2-content start -->

		<#-- 查询区域 -->
		<div class="box" style="margin-bottom:9px;">
			<div class="box-body">
				<div class="row" id="data_filter" >

					<div class="col-3">
						<div class="input-group">
							<span class="input-group-addon">执行器</span>
							<select class="form-control" id="jobGroupId" >
								<#list JobGroupList as group>
									<option value="${group.id}" >${group.title}</option>
								</#list>
							</select>
						</div>
					</div>
					<div class="col-3">
						<div class="input-group">
							<span class="input-group-addon">任务组名称</span>
							<input type="text" class="form-control" id="groupName" placeholder="请输入任务组名称" >
						</div>
					</div>

					<div class="col-1">
						<button class="btn btn-block btn-primary searchBtn" >搜索</button>
					</div>
					<div class="col-1">
						<button class="btn btn-block btn-secondary resetBtn" >重置</button>
					</div>
				</div>
			</div>
		</div>

		<#-- 数据表格区域 -->
		<div class="row">
			<div class="col-12">
				<div class="box">
					<div class="box-header pull-left" id="data_operation" >
						<button class="btn btn-sm btn-info add" type="button"><i class="fa fa-plus" ></i>新增</button>
						<button class="btn btn-sm btn-warning selectOnlyOne update" type="button"><i class="fa fa-edit"></i>编辑</button>
						<button class="btn btn-sm btn-danger selectOnlyOne delete" type="button"><i class="fa fa-remove "></i>删除</button>
						｜
						<button class="btn btn-sm btn-primary selectOnlyOne execute_taskgroup" type="button"><i class="fa fa-play"></i>配置顺序执行</button>
						<button class="btn btn-sm btn-secondary selectOnlyOne view_jobs" type="button"><i class="fa fa-list"></i>查看任务</button>
					</div>
					<div class="box-body" >
						<table id="data_list" class="table table-bordered table-striped" width="100%" >
							<thead></thead>
							<tbody></tbody>
							<tfoot></tfoot>
						</table>
					</div>
				</div>
			</div>
		</div>

		<!-- 新增.模态框 -->
		<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >新增任务组</h4>
					</div>
					<div class="modal-body">
						<form class="form" role="form" >
							<div class="row mb-3">
								<label class="col-sm-2 col-form-label text-end">执行器<font color="red">*</font></label>
								<div class="col-sm-10">
									<select class="form-control" name="jobGroupId" >
										<#list JobGroupList as group>
											<option value="${group.id}" >${group.title}</option>
										</#list>
									</select>
								</div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-2 col-form-label text-end">任务组名称<font color="red">*</font></label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="groupName" placeholder="请输入任务组名称" maxlength="100" >
								</div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-2 col-form-label text-end">任务组描述</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="groupDesc" placeholder="请输入任务组描述" maxlength="255" >
								</div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-2 col-form-label text-end">排序</label>
								<div class="col-sm-10">
									<input type="number" class="form-control" name="groupOrder" placeholder="数字越小越靠前" value="0" min="0" >
								</div>
							</div>
							<hr>
							<div class="row">
								<div class="col-sm-10 offset-sm-2">
									<button type="submit" class="btn btn-primary"  >保存</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- 更新.模态框 -->
		<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >编辑任务组</h4>
					</div>
					<div class="modal-body">
						<form class="form" role="form" >
							<div class="row mb-3">
								<label class="col-sm-2 col-form-label text-end">执行器<font color="red">*</font></label>
								<div class="col-sm-10">
									<select class="form-control" name="jobGroupId" disabled >
										<#list JobGroupList as group>
											<option value="${group.id}" >${group.title}</option>
										</#list>
									</select>
									<input type="hidden" name="jobGroupId" />
								</div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-2 col-form-label text-end">任务组名称<font color="red">*</font></label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="groupName" placeholder="请输入任务组名称" maxlength="100" >
								</div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-2 col-form-label text-end">任务组描述</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="groupDesc" placeholder="请输入任务组描述" maxlength="255" >
								</div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-2 col-form-label text-end">排序</label>
								<div class="col-sm-10">
									<input type="number" class="form-control" name="groupOrder" placeholder="数字越小越靠前" value="0" min="0" >
								</div>
							</div>
							<hr>
							<div class="row">
								<div class="col-sm-10 offset-sm-2">
									<button type="submit" class="btn btn-primary"  >保存</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
									<input type="hidden" name="id" >
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- 2-content end -->

	</section>
</div>

<!-- 3-script start -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<#-- admin table -->
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<script>
	$(function() {

		/**
		 * init table
		 */
		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/taskgroup/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.jobGroupId = $('#jobGroupId').val();
				obj.groupName = $('#groupName').val();
				obj.offset = params.offset;
				obj.pagesize = params.limit;
				return obj;
			},
			columns:[
				{
					checkbox: true,
					field: 'state',
					width: '5',
					widthUnit: '%',
					align: 'center',
					valign: 'middle'
				},{
					title: 'ID',
					field: 'id',
					width: '8',
					widthUnit: '%',
					align: 'left'
				},{
					title: '任务组名称',
					field: 'groupName',
					width: '20',
					widthUnit: '%',
					align: 'left'
				},{
					title: '任务组描述',
					field: 'groupDesc',
					width: '25',
					widthUnit: '%',
					formatter: function(value, row, index) {
						return value || '-';
					}
				},{
					title: '排序',
					field: 'groupOrder',
					width: '8',
					widthUnit: '%',
					align: 'center'
				},{
					title: '任务数量',
					field: 'jobCount',
					width: '10',
					widthUnit: '%',
					align: 'center',
					formatter: function(value, row, index) {
						return value || 0;
					}
				},{
					title: '创建时间',
					field: 'addTime',
					width: '15',
					widthUnit: '%',
					formatter: function(value, row, index) {
						if (value) {
							return new Date(value).toLocaleString();
						}
						return '-';
					}
				}
			]
		});

		/**
		 * jobGroupId change - 刷新表格
		 */
		$('#jobGroupId').on('change', function(){
			$('#data_filter .searchBtn').click();
		});

		/**
		 * init delete
		 */
		$("#data_operation").on('click', '.delete',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select ids
			const selectIds = (rows && rows.length > 0) ? rows.map(row => row.id) : [];
			if (selectIds.length !== 1) {
				layer.msg('请选择一条数据');
				return;
			}

			// do delete
			layer.confirm( '确定删除该任务组?', {
				icon: 3,
				title: '系统提示' ,
				btn: [ '确定', '取消' ]
			}, function(index){
				layer.close(index);

				$.ajax({
					type : 'POST',
					url : base_url + "/taskgroup/delete",
					data : {
						"id" : selectIds[0]
					},
					dataType : "json",
					success : function(data){
						if (data.code === 200) {
							layer.msg( '删除成功' );
							$('#data_filter .searchBtn').click();
						} else {
							layer.msg( data.msg || '删除失败' );
						}
					},
					error: function(xhr, status, error) {
						layer.msg('删除失败');
					}
				});
			});
		});

		/**
		 * init add
		 */
		$.adminTable.initAdd( {
			url: base_url + "/taskgroup/add",
			rules : {
				groupName : {
					required : true,
					maxlength: 100
				}
			},
			messages : {
				groupName : {
					required : "请输入任务组名称",
					maxlength: "任务组名称长度不能超过100"
				}
			},
			writeFormData: function() {
				// 设置默认执行器
				var jobGroupId = $('#jobGroupId').val();
				$('#addModal .form select[name=jobGroupId] option[value='+ jobGroupId +']').prop('selected', true);
			},
			readFormData: function() {
				return $("#addModal .form").serializeArray();
			}
		});

		/**
		 * init update
		 */
		$.adminTable.initUpdate( {
			url: base_url + "/taskgroup/update",
			writeFormData: function(row) {
				$("#updateModal .form input[name='id']").val( row.id );
				$('#updateModal .form select[name=jobGroupId] option[value='+ row.jobGroupId +']').prop('selected', true);
				$("#updateModal .form input[name='jobGroupId']").val( row.jobGroupId );
				$("#updateModal .form input[name='groupName']").val( row.groupName );
				$("#updateModal .form input[name='groupDesc']").val( row.groupDesc );
				$("#updateModal .form input[name='groupOrder']").val( row.groupOrder );
			},
			rules : {
				groupName : {
					required : true,
					maxlength: 100
				}
			},
			messages : {
				groupName : {
					required : "请输入任务组名称",
					maxlength: "任务组名称长度不能超过100"
				}
			},
			readFormData: function() {
				return $("#updateModal .form").serializeArray();
			}
		});

		/**
		 * 配置顺序执行
		 */
		$("#data_operation").on('click', '.execute_taskgroup',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择一条数据');
				return;
			}
			var row = rows[0];

			// confirm
			layer.confirm( '确定配置该任务组的顺序执行？<br>这将根据组内任务的排序设置子任务链。', {
				icon: 3,
				title: '系统提示' ,
				btn: [ '确定', '取消' ]
			}, function(index){
				layer.close(index);

				$.ajax({
					type : 'POST',
					url : base_url + "/taskgroup/execute",
					data : {
						"id" : row.id
					},
					dataType : "json",
					success : function(data){
						if (data.code === 200) {
							layer.msg( '配置成功，请手动触发第一个任务开始执行' );
						} else {
							layer.msg( data.msg || '配置失败' );
						}
					},
					error: function(xhr, status, error) {
						layer.msg('配置失败');
					}
				});
			});
		});

		/**
		 * 查看任务
		 */
		$("#data_operation").on('click', '.view_jobs',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择一条数据');
				return;
			}
			var row = rows[0];

			// 跳转到任务列表页面，带上任务组ID参数
			var url = base_url + '/jobinfo?jobGroup=' + row.jobGroupId + '&taskGroupId=' + row.id;
			window.open(url);
		});

	});

</script>
<!-- 3-script end -->

</body>
</html>
