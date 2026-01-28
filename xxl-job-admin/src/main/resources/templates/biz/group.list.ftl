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
							<span class="input-group-addon">AppName</span>
							<input type="text" class="form-control" id="appname" placeholder="请输入AppName" >
						</div>
					</div>
					<div class="col-3">
						<div class="input-group">
							<span class="input-group-addon">名称</span>
							<input type="text" class="form-control" id="title" placeholder="名称" >
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

		<!-- 注册列表查看.模态框 -->
		<div class="modal fade" id="showRegistryListModal" tabindex="-1" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered">
				<div class="modal-content shadow">
					<div class="modal-header bg-success text-white">
						<h5 class="modal-title"><i class="fa fa-server me-2"></i>注册节点</h5>
						<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
					</div>
					<div class="modal-body">
						<div class="data" style="word-wrap: break-word;"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-success ok" data-bs-dismiss="modal">确定</button>
					</div>
				</div>
			</div>
		</div>

		<!-- 新增.模态框 -->
		<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
			<div class="modal-dialog modal-lg modal-dialog-centered">
				<div class="modal-content shadow">
					<div class="modal-header bg-info text-white">
						<h5 class="modal-title"><i class="fa fa-plus me-2"></i>新增执行器</h5>
						<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
					</div>
					<div class="modal-body p-4">
						<form class="form" role="form">
							<div class="mb-3">
								<label class="form-label fw-bold">AppName <span class="text-danger">*</span></label>
								<input type="text" class="form-control" name="appname" placeholder="请输入AppName" maxlength="64">
							</div>
							<div class="mb-3">
								<label class="form-label fw-bold">名称 <span class="text-danger">*</span></label>
								<input type="text" class="form-control" name="title" placeholder="请输入名称" maxlength="50">
							</div>
							<div class="mb-3">
								<label class="form-label fw-bold">注册方式 <span class="text-danger">*</span></label>
								<div class="d-flex gap-4 mt-2">
									<div class="form-check">
										<input class="form-check-input" type="radio" name="addressType" value="0" checked>
										<label class="form-check-label">自动注册</label>
									</div>
									<div class="form-check">
										<input class="form-check-input" type="radio" name="addressType" value="1">
										<label class="form-check-label">手动录入</label>
									</div>
								</div>
							</div>
							<div class="mb-3">
								<label class="form-label fw-bold">机器地址 <span class="text-danger">*</span></label>
								<textarea class="form-control" name="addressList" rows="4" maxlength="20000" placeholder="请输入执行器地址列表，多地址逗号分隔" readonly style="background-color:#eee;"></textarea>
							</div>
							<hr>
							<div class="d-flex justify-content-end gap-2">
								<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
								<button type="submit" class="btn btn-primary">保存</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- 更新.模态框 -->
		<div class="modal fade" id="updateModal" tabindex="-1" aria-hidden="true">
			<div class="modal-dialog modal-lg modal-dialog-centered">
				<div class="modal-content shadow">
					<div class="modal-header bg-warning">
						<h5 class="modal-title"><i class="fa fa-edit me-2"></i>编辑执行器</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					</div>
					<div class="modal-body p-4">
						<form class="form" role="form">
							<div class="mb-3">
								<label class="form-label fw-bold">AppName <span class="text-danger">*</span></label>
								<input type="text" class="form-control" name="appname" placeholder="请输入AppName" maxlength="64">
							</div>
							<div class="mb-3">
								<label class="form-label fw-bold">名称 <span class="text-danger">*</span></label>
								<input type="text" class="form-control" name="title" placeholder="请输入名称" maxlength="50">
							</div>
							<div class="mb-3">
								<label class="form-label fw-bold">注册方式 <span class="text-danger">*</span></label>
								<div class="d-flex gap-4 mt-2">
									<div class="form-check">
										<input class="form-check-input" type="radio" name="addressType" value="0">
										<label class="form-check-label">自动注册</label>
									</div>
									<div class="form-check">
										<input class="form-check-input" type="radio" name="addressType" value="1">
										<label class="form-check-label">手动录入</label>
									</div>
								</div>
							</div>
							<div class="mb-3">
								<label class="form-label fw-bold">机器地址 <span class="text-danger">*</span></label>
								<textarea class="form-control" name="addressList" rows="4" maxlength="20000" placeholder="请输入执行器地址列表，多地址逗号分隔" readonly style="background-color:#eee;"></textarea>
							</div>
							<hr>
							<div class="d-flex justify-content-end gap-2">
								<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
								<button type="submit" class="btn btn-primary">保存</button>
								<input type="hidden" name="id">
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
			url: base_url + "/jobgroup/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.appname = $('#appname').val();
				obj.title = $('#title').val();
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
					title: 'AppName',
					field: 'appname',
					width: '30',
					widthUnit: '%',
					align: 'left'
				},{
					title: '名称',
					field: 'title',
					width: '30',
					widthUnit: '%'
				},{
					title: '注册方式',
					field: 'addressType',
					width: '10',
					widthUnit: '%',
					formatter: function(value, row, index) {
						if (row.addressType == 0) {
							return '自动注册';
						} else {
							return '手动录入';
						}
					}
				},{
					title: 'OnLine' + '机器地址',
					field: 'registryList',
					width: '15',
					widthUnit: '%',
					formatter: function(value, row, index) {
						tableData['key' + row.id] = row;
						return row.registryList
								?'<a class="show_registryList" href="javascript:;" _id="'+ row.id +'" >' + '查看' +' ( ' + row.registryList.length+ ' ）</a>'
								:'无';
					}
				}
			]
		});

		// job registryinfo
		var tableData = {};
		$("#data_list").on('click', '.show_registryList',function() {
			var id = $(this).attr("_id");
			var row = tableData['key'+id];

			var html = '<table class="table table-bordered"><tbody>';
			if (row.registryList) {
				for (var index in row.registryList) {
					html += '<tr><th class="col-md-3" >' + (parseInt(index)+1) + '</th>';
					html += '<th class="col-md-9" ><span class="badge bg-green" >' + row.registryList[index] + '</span></th><tr>';
				}
			}
			html += '</tbody></table>';

			$('#showRegistryListModal .data').html(html);
			$('#showRegistryListModal').modal({backdrop: false, keyboard: false}).modal('show');
		});

		/**
		 * init delete
		 */
		$.adminTable.initDelete({
			url: base_url + "/jobgroup/delete"
		});

		/**
		 * init add
		 */
		// add validator method
		jQuery.validator.addMethod("myValid01", function(value, element) {
			var length = value.length;
			var valid = /^[a-z][a-zA-Z0-9-]*$/;
			return this.optional(element) || valid.test(value);
		}, '限制以小写字母开头，由小写字母、数字和中划线组成' );
		$.adminTable.initAdd( {
			url: base_url + "/jobgroup/insert",
			rules : {
				appname : {
					required : true,
					rangelength:[4,64],
					myValid01 : true
				},
				title : {
					required : true,
					rangelength:[4, 50]
				}
			},
			messages : {
				appname : {
					required : "请输入"+"AppName",
					rangelength: "AppName长度限制为4~64" ,
					myValid01: "限制以小写字母开头，由小写字母、数字和中划线组成"
				},
				title : {
					required : "请输入" + "名称" ,
					rangelength: "名称长度限制为4~50"
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

		// addressType change
		$("#addModal input[name=addressType], #updateModal input[name=addressType]").click(function(){
			var addressType = $(this).val();
			var $addressList = $(this).parents("form").find("textarea[name=addressList]");
			if (addressType == 0) {
				$addressList.css("background-color", "#eee");	// 自动注册
				$addressList.attr("readonly","readonly");
				$addressList.val("");
			} else {
				$addressList.css("background-color", "white");
				$addressList.removeAttr("readonly");
			}
		});

		/**
		 * init update
		 */
		$.adminTable.initUpdate( {
			url: base_url + "/jobgroup/update",
			writeFormData: function(row) {

				$("#updateModal .form input[name='id']").val( row.id );
				$("#updateModal .form input[name='appname']").val( row.appname );
				$("#updateModal .form input[name='title']").val( row.title );

				// 注册方式
				$("#updateModal .form input[name='addressType']").removeAttr('checked');
				$("#updateModal .form input[name='addressType'][value='"+ row.addressType +"']").click();
				// 机器地址
				$("#updateModal .form textarea[name='addressList']").val( row.addressList );

			},
			rules : {
				appname : {
					required : true,
					rangelength:[4,64],
					myValid01 : true
				},
				title : {
					required : true,
					rangelength:[4, 50]
				}
			},
			messages : {
				appname : {
					required : "请输入"+"AppName",
					rangelength: "AppName长度限制为4~64" ,
					myValid01: "限制以小写字母开头，由小写字母、数字和中划线组成"
				},
				title : {
					required : "请输入" + "名称" ,
					rangelength: "名称长度限制为4~50"
				}
			},
			readFormData: function() {
				// request
				return $("#updateModal .form").serializeArray();
			}
		});

	});

</script>
<!-- 3-script end -->

</body>
</html>