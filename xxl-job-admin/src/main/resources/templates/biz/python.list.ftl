<!DOCTYPE html>
<html>
<head>
	<#import "../common/common.macro.ftl" as netCommon>

	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.css">
</head>
<body class="hold-transition" style="background-color: #ecf0f5;">
<div class="wrapper">
	<section class="content">

		<div class="box" style="margin-bottom:9px;">
			<div class="box-body">
				<div class="row" id="data_filter" >
					<div class="col-3">
						<div class="input-group">
							<span class="input-group-addon">名称</span>
							<input type="text" class="form-control" id="name" autocomplete="on" >
						</div>
					</div>
					<div class="col-3">
						<div class="input-group">
							<span class="input-group-addon">版本</span>
							<input type="text" class="form-control" id="version" autocomplete="on" >
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

		<div class="row">
			<div class="col-12">
				<div class="box">
					<div class="box-header pull-left" id="data_operation" >
						<button class="btn btn-sm btn-info add" type="button"><i class="fa fa-plus" ></i>新增</button>
						<button class="btn btn-sm btn-warning selectOnlyOne update" type="button"><i class="fa fa-edit"></i>编辑</button>
						<button class="btn btn-sm btn-danger selectAny delete" type="button"><i class="fa fa-remove "></i>删除</button>
						<button class="btn btn-sm btn-success scan" type="button"><i class="fa fa-search"></i>扫描</button>
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

		<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >新增</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="row mb-3">
								<label class="col-sm-3 col-form-label">名称<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="name" placeholder="请输入名称" maxlength="64" ></div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-3 col-form-label">版本<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="version" placeholder="请输入版本" maxlength="32" ></div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-3 col-form-label">执行路径<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="execPath" placeholder="请输入执行路径" maxlength="512" ></div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-3 col-form-label">备注</label>
								<div class="col-sm-9"><input type="text" class="form-control" name="remark" placeholder="请输入备注" maxlength="255" ></div>
							</div>

							<hr>
							<div class="row mb-3">
								<div class="offset-sm-3 col-sm-6">
									<button type="submit" class="btn btn-primary"  >保存</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >编辑</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="row mb-3">
								<label class="col-sm-3 col-form-label">名称<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="name" placeholder="请输入名称" maxlength="64" ></div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-3 col-form-label">版本<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="version" placeholder="请输入版本" maxlength="32" ></div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-3 col-form-label">执行路径<font color="red">*</font></label>
								<div class="col-sm-9"><input type="text" class="form-control" name="execPath" placeholder="请输入执行路径" maxlength="512" ></div>
							</div>
							<div class="row mb-3">
								<label class="col-sm-3 col-form-label">备注</label>
								<div class="col-sm-9"><input type="text" class="form-control" name="remark" placeholder="请输入备注" maxlength="255" ></div>
							</div>

							<hr>
							<div class="row mb-3">
								<div class="offset-sm-3 col-sm-6">
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

	</section>
</div>

<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<script>
	$(function() {

		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/python/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.name = $('#name').val();
				obj.version = $('#version').val();
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
					width: '5',
					widthUnit: '%',
					align: 'left'
				},{
					title: '名称',
					field: 'name',
					width: '15',
					widthUnit: '%',
					align: 'left'
				},{
					title: '版本',
					field: 'version',
					width: '10',
					widthUnit: '%',
					align: 'left'
				},{
					title: '执行路径',
					field: 'execPath',
					width: '35',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						if (!value) {
							return '';
						}
						return '<span title="' + value + '">' + value + '</span>';
					}
				},{
					title: '备注',
					field: 'remark',
					width: '15',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						return value || '';
					}
				},{
					title: '操作',
					field: 'opt',
					width: '15',
					widthUnit: '%',
					align: 'center',
					formatter: function(value, row, index) {
						return '';
					}
				}
			]
		});

		$.adminTable.initDelete({
			url: base_url + "/python/delete"
		});

		$.adminTable.initAdd({
			url: base_url + "/python/insert",
			rules: {
				name: { required: true, maxlength: 64 },
				version: { required: true, maxlength: 32 },
				execPath: { required: true, maxlength: 512 },
				remark: { maxlength: 255 }
			},
			messages: {
				name: { required: '请输入名称' },
				version: { required: '请输入版本' },
				execPath: { required: '请输入执行路径' }
			},
			readFormData: function() {
				return $("#addModal .form").serializeArray();
			}
		});

		$.adminTable.initUpdate({
			url: base_url + "/python/update",
			writeFormData: function(row) {
				$("#updateModal .form input[name='id']").val(row.id);
				$("#updateModal .form input[name='name']").val(row.name);
				$("#updateModal .form input[name='version']").val(row.version);
				$("#updateModal .form input[name='execPath']").val(row.execPath);
				$("#updateModal .form input[name='remark']").val(row.remark);
			},
			readFormData: function() {
				return $("#updateModal .form").serializeArray();
			}
		});

		$("#data_operation .scan").click(function() {
			layer.confirm('确定扫描？', {
				icon: 3,
				title: '提示',
				btn: [ '确定', '取消' ]
			}, function(index){
				layer.close(index);
				$.post(base_url + "/python/scan", {}, function(data) {
					if (data.code === 200) {
						layer.msg('成功');
						$('#data_filter .searchBtn').click();
					} else {
						layer.open({
							title: '提示',
							btn: [ '确定' ],
							content: (data.msg || '失败'),
							icon: '2'
						});
					}
				});
			});
		});

	});
</script>

</body>
</html>
