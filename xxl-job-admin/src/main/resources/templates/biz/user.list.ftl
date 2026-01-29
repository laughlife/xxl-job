<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>

	<!-- 1-style start -->
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.css">
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/iCheck/square/blue.css">
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
							<span class="input-group-text">角色</span>
							<select class="form-control" id="role" >
								<option value="-1" >全部</option>
								<option value="1" >管理员</option>
								<option value="0" >普通用户</option>
							</select>
						</div>
					</div>
					<div class="col-3">
						<div class="input-group">
							<span class="input-group-text">账号</span>
							<input type="text" class="form-control" id="username" autocomplete="on" >
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
						<button class="btn btn-sm btn-danger selectAny delete" type="button"><i class="fa fa-remove "></i>删除</button>
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
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >新增用户</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">账号<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="username" placeholder="请输入账号" maxlength="20" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">密码<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="password" placeholder="请输入密码" maxlength="20" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">角色<font color="red">*</font></label>
								<div class="col-sm-10">
									<input type="radio" name="role" value="0" checked />普通用户
									&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="radio" name="role" value="1" />管理员
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">权限<font color="black">*</font></label>
								<div class="col-sm-10">
									<#if groupList?exists && groupList?size gt 0>
										<#list groupList as item>
											<input type="checkbox" name="permission" value="${item.id}" />&nbsp;&nbsp;${item.title}：${item.appname}
											<br>
										</#list>
									</#if>
								</div>
							</div>

							<hr>
							<div class="form-group">
								<div class="col-sm-offset-3 col-sm-6">
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
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >更新用户</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">账号<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="username" placeholder="请输入账号" maxlength="20" readonly ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">密码<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="password" placeholder="请输入新密码，为空则不更新密码" maxlength="20" ></div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">角色<font color="red">*</font></label>
								<div class="col-sm-10">
									<input type="radio" name="role" value="0" />普通用户
									&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="radio" name="role" value="1" />管理员
								</div>
							</div>
							<div class="form-group">
								<label for="lastname" class="col-sm-2 control-label">权限<font color="black">*</font></label>
								<div class="col-sm-10">
									<#if groupList?exists && groupList?size gt 0>
										<#list groupList as item>
											<input type="checkbox" name="permission" value="${item.id}" />${item.title}(${item.appname})<br>
										</#list>
									</#if>
								</div>
							</div>

							<hr>
							<div class="form-group">
								<div class="col-sm-offset-3 col-sm-6">
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
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<#-- admin table -->
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<script>
	$(function() {

		/**
		 * init table
		 */
		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/user/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.username = $('#username').val();
				obj.role = $('#role').val();
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
					title: '账号',
					field: 'username',
					width: '20',
					widthUnit: '%',
					align: 'left'
				},{
					title: '密码',
					field: 'password',
					width: '20',
					widthUnit: '%',
					formatter: function(value, row, index) {
						return '******';
					}
				},{
					title: '角色',
					field: 'role',
					width: '10',
					widthUnit: '%',
					formatter: function(value, row, index) {
						let result = value;
						$('#data_filter #role option').each(function(){
							if ( value+"" === $(this).val() ) {
								result = $(this).text();
							}
						});
						return result;
					}
				}
			]
		});

		/**
		 * init delete
		 */
		$.adminTable.initDelete({
			url: base_url + "/user/delete"
		});

		/**
		 * init add
		 */
		// add validator method
		jQuery.validator.addMethod("myValid01", function(value, element) {
			var length = value.length;
			var valid = /^[a-z][a-z0-9]*$/;
			return this.optional(element) || valid.test(value);
		}, '限制以小写字母开头，由小写字母、数字组成' );
		$.adminTable.initAdd( {
			url: base_url + "/user/insert",
			rules : {
				username : {
					required : true,
					rangelength:[4, 20],
					myValid01: true
				},
				password : {
					required : true,
					rangelength:[4, 20]
				}
			},
			messages : {
				username : {
					required : '请输入' + '账号',
					rangelength: '长度限制' + "[4-20]"
				},
				password : {
					required : '请输入' + '密码',
					rangelength: '长度限制' + "[4-20]"
				}
			},
			writeFormData: function() {
				$("#addModal .form input[name='role'][value='0']").change();
			},
			readFormData: function() {
				// request
				return $("#addModal .form").serializeArray();
			}
		});

		// add role
		$("#addModal .form input[name=role]").change(function () {
			var role = $(this).val();
			if (role == 1) {
				$("#addModal .form input[name=permission]").parents('.form-group').hide();
			} else {
				$("#addModal .form input[name=permission]").parents('.form-group').show();
			}
			$("#addModal .form input[name='permission']").prop("checked",false);
		});

		/**
		 * init update
		 */
		$.adminTable.initUpdate( {
			url: base_url + "/user/update",
			writeFormData: function(row) {

				// base data
				$("#updateModal .form input[name='id']").val( row.id );
				$("#updateModal .form input[name='username']").val( row.username );
				$("#updateModal .form input[name='password']").val( '' );
				$("#updateModal .form input[name='role'][value='"+ row.role +"']").click();
				var permissionArr = [];
				if (row.permission) {
					permissionArr = row.permission.split(",");
				}
				$("#updateModal .form input[name='permission']").each(function () {
					if($.inArray($(this).val(), permissionArr) > -1) {
						$(this).prop("checked",true);
					} else {
						$(this).prop("checked",false);
					}
				});

			},
			readFormData: function() {
				// request
				return $("#updateModal .form").serializeArray();
			}
		});

		// update role
		$("#updateModal .form input[name=role]").change(function () {
			var role = $(this).val();
			if (role == 1) {
				$("#updateModal .form input[name=permission]").parents('.form-group').hide();
			} else {
				$("#updateModal .form input[name=permission]").parents('.form-group').show();
			}
			$("#updateModal .form input[name='permission']").prop("checked",false);
		});

	});

</script>
<!-- 3-script end -->

</body>
</html>
