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
							<span class="input-group-addon">${I18n.python_name}</span>
							<input type="text" class="form-control" id="name" autocomplete="on" >
						</div>
					</div>
					<div class="col-3">
						<div class="input-group">
							<span class="input-group-addon">${I18n.python_version}</span>
							<input type="text" class="form-control" id="version" autocomplete="on" >
						</div>
					</div>

					<div class="col-1">
						<button class="btn btn-block btn-primary searchBtn" >${I18n.system_search}</button>
					</div>
					<div class="col-1">
						<button class="btn btn-block btn-secondary resetBtn" >${I18n.system_reset}</button>
					</div>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-12">
				<div class="box">
					<div class="box-header pull-left" id="data_operation" >
						<button class="btn btn-sm btn-info add" type="button"><i class="fa fa-plus" ></i>${I18n.system_opt_add}</button>
						<button class="btn btn-sm btn-warning selectOnlyOne update" type="button"><i class="fa fa-edit"></i>${I18n.system_opt_edit}</button>
						<button class="btn btn-sm btn-danger selectAny delete" type="button"><i class="fa fa-remove "></i>${I18n.system_opt_del}</button>
						<button class="btn btn-sm btn-success scan" type="button"><i class="fa fa-search"></i>${I18n.python_scan}</button>
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
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >${I18n.python_add}</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label class="col-sm-2 control-label">${I18n.python_name}<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="name" placeholder="${I18n.system_please_input}${I18n.python_name}" maxlength="64" ></div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">${I18n.python_version}<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="version" placeholder="${I18n.system_please_input}${I18n.python_version}" maxlength="32" ></div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">${I18n.python_exec_path}<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="execPath" placeholder="${I18n.system_please_input}${I18n.python_exec_path}" maxlength="512" ></div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">${I18n.python_remark}</label>
								<div class="col-sm-8"><input type="text" class="form-control" name="remark" placeholder="${I18n.python_remark}" maxlength="255" ></div>
							</div>

							<hr>
							<div class="form-group">
								<div class="col-sm-offset-3 col-sm-6">
									<button type="submit" class="btn btn-primary"  >${I18n.system_save}</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">${I18n.system_cancel}</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >${I18n.python_update}</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label class="col-sm-2 control-label">${I18n.python_name}<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="name" placeholder="${I18n.system_please_input}${I18n.python_name}" maxlength="64" ></div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">${I18n.python_version}<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="version" placeholder="${I18n.system_please_input}${I18n.python_version}" maxlength="32" ></div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">${I18n.python_exec_path}<font color="red">*</font></label>
								<div class="col-sm-8"><input type="text" class="form-control" name="execPath" placeholder="${I18n.system_please_input}${I18n.python_exec_path}" maxlength="512" ></div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">${I18n.python_remark}</label>
								<div class="col-sm-8"><input type="text" class="form-control" name="remark" placeholder="${I18n.python_remark}" maxlength="255" ></div>
							</div>

							<hr>
							<div class="form-group">
								<div class="col-sm-offset-3 col-sm-6">
									<button type="submit" class="btn btn-primary"  >${I18n.system_save}</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">${I18n.system_cancel}</button>
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
<script src="${request.contextPath}/static/plugins/bootstrap-table/locale/<#if I18n.admin_i18n?? && I18n.admin_i18n == 'en'>bootstrap-table-en-US.js<#else>bootstrap-table-zh-CN.js</#if>"></script>
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
					title: I18n.python_name,
					field: 'name',
					width: '15',
					widthUnit: '%',
					align: 'left'
				},{
					title: I18n.python_version,
					field: 'version',
					width: '10',
					widthUnit: '%',
					align: 'left'
				},{
					title: I18n.python_exec_path,
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
					title: I18n.python_remark,
					field: 'remark',
					width: '15',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						return value || '';
					}
				},{
					title: I18n.system_opt,
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
				name: { required: I18n.system_please_input + I18n.python_name },
				version: { required: I18n.system_please_input + I18n.python_version },
				execPath: { required: I18n.system_please_input + I18n.python_exec_path }
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
			layer.confirm(I18n.system_ok + I18n.python_scan + '?', {
				icon: 3,
				title: I18n.system_tips ,
				btn: [ I18n.system_ok, I18n.system_cancel ]
			}, function(index){
				layer.close(index);
				$.post(base_url + "/python/scan", {}, function(data) {
					if (data.code === 200) {
						layer.msg(I18n.system_success);
						$('#data_filter .searchBtn').click();
					} else {
						layer.open({
							title: I18n.system_tips,
							btn: [ I18n.system_ok ],
							content: (data.msg || I18n.system_fail),
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

