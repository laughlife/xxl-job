<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>

	<!-- 1-style start -->
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.css">
	<!-- daterangepicker -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.min.css">
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

					<div class="col-2">
						<div class="input-group">
							<span class="input-group-addon">执行器</span>
							<select class="form-control" id="jobGroup"  >
								<#list JobGroupList as group>
									<option value="${group.id}" <#if jobGroup==group.id>selected</#if> >${group.title}</option>
								</#list>
							</select>
						</div>
					</div>
					<div class="col-2">
						<div class="input-group">
							<span class="input-group-addon">任务</span>
							<select class="form-control" id="jobId" >
								<#if jobInfoList?size gt 0>
									<#list jobInfoList as jobItem>
										<option value="${jobItem.id}" >${jobItem.jobDesc}</option>
									</#list>
								<#else>
									<option value="0" >未选择</option>
								</#if>
							</select>
						</div>
					</div>
					<div class="col-2">
						<div class="input-group">
							<span class="input-group-addon">状态</span>
							<select class="form-control" id="logStatus" >
								<option value="-1" >全部</option>
								<option value="1" >成功</option>
								<option value="2" >失败</option>
								<option value="3" >进行中</option>
							</select>
						</div>
					</div>
					<div class="col-4">
						<div class="input-group">
                		<span class="input-group-addon">
	                  		调度时间
	                	</span>
							<input type="text" class="form-control" id="filterTime" readonly >
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
						<button class="btn btn-sm btn-warning selectOnlyOne logKill" type="button">终止任务</button>
						<button class="btn btn-sm btn-danger selectAny deleteSelected" type="button">删除选中</button>
						<button class="btn btn-sm btn-danger selectAny clearLog" type="button">日志清理</button>
						｜
						<button class="btn btn-sm btn-primary selectOnlyOne logDetail" type="button">执行日志</button>
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

		<!-- 日志清理.模态框 -->
		<div class="modal fade" id="clearLogModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >日志清理</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="form-group">
								<label class="col-sm-3 control-label">执行器：</label>
								<div class="col-sm-9">
									<input type="text" class="form-control jobGroupText" readonly >
									<input type="hidden" name="jobGroup" >
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-3 control-label">任务：</label>
								<div class="col-sm-9">
									<input type="text" class="form-control jobIdText" readonly >
									<input type="hidden" name="jobId" >
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-3 control-label">清理方式：</label>
								<div class="col-sm-9">
									<select class="form-control" name="type" >
										<option value="1" >清理一个月之前日志数据</option>
										<option value="2" >清理三个月之前日志数据</option>
										<option value="3" >清理六个月之前日志数据</option>
										<option value="4" >清理一年之前日志数据</option>
										<option value="5" >清理一千条以前日志数据</option>
										<option value="6" >清理一万条以前日志数据</option>
										<option value="7" >清理三万条以前日志数据</option>
										<option value="8" >清理十万条以前日志数据</option>
										<option value="9" >清理所有日志数据</option>
									</select>
								</div>
							</div>

							<hr>
							<div class="form-group">
								<div class="col-sm-offset-3 col-sm-6">
									<button type="button" class="btn btn-primary ok" >确定</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
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
<#--daterangepicker-->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.min.js"></script>
<#-- admin table -->
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<script>
	$(function() {

		// ---------------------- filter ----------------------

		/**
		 * jobGroup change
 		 */
		$('#jobGroup').on('change', function(){
			//reload
			var jobGroup = $('#jobGroup').val();
			window.location.href = base_url + "/joblog?jobGroup=" + jobGroup;
		});

		/**
		 * filter Time
		 */
		var rangesConf = {};
		rangesConf['今日'] = [moment().startOf('day'), moment().endOf('day')];
		rangesConf['昨日'] = [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')];
		rangesConf['本月'] = [moment().startOf('month'), moment().endOf('month')];
		rangesConf['上个月'] = [moment().subtract(1, 'months').startOf('month'), moment().subtract(1, 'months').endOf('month')];
		rangesConf['最近一周'] = [moment().subtract(1, 'weeks').startOf('day'), moment().endOf('day')];
		rangesConf['最近一月'] = [moment().subtract(1, 'months').startOf('day'), moment().endOf('day')];

		$('#filterTime').daterangepicker({
			autoApply:false,
			singleDatePicker:false,		// 范围选择 or 单时间选择
			showDropdowns:true,         // 年月 选择条件是否为下拉框
			timePicker: true,
			timePicker24Hour: true,
			timePickerSeconds: true,	// 时间选择是否显示秒
			opens : 'left', 			// 日期选择框的弹出位置
			ranges: rangesConf,
			locale : {
				format: 'YYYY-MM-DD HH:mm:ss',
				separator : ' - ',
				customRangeLabel : '自定义' ,
				applyLabel : '确定' ,
				cancelLabel : '取消' ,
				fromLabel : '起始时间' ,
				toLabel : '结束时间' ,
				daysOfWeek : ['日', '一', '二', '三', '四', '五', '六'] ,
				monthNames : ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'] ,
				firstDay : 1
			}
		});

		// init filter
		var jobGroup = '${jobGroup}';
		var jobId = '${jobId}';
		function resetFilter(){
			$('#filterTime').data("daterangepicker").setStartDate( rangesConf['最近一周'][0] );
			$('#filterTime').data("daterangepicker").setEndDate( rangesConf['最近一周'][1] );

			$("#jobGroup").val( jobGroup );
			$("#jobId").val( jobId );
			$('#logStatus').prop('selectedIndex', 0);
		}
		resetFilter();

		// ---------------------- page ----------------------

		/**
		 * init table
		 */
		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/joblog/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.jobGroup = $('#jobGroup').val();
				obj.jobId = $('#jobId').val();
				obj.logStatus = $('#logStatus').val();
				obj.filterTime = $('#filterTime').val();
				obj.offset = params.offset;
				obj.pagesize = params.limit;
				return obj;
			},
			resetHandler : function() {
				// reset filter
				resetFilter();
			},
			columns:[
				{
					checkbox: true,
					field: 'state',
					width: '5',
					widthUnit: '%',
					align: 'center',
					valign: 'middle'
				},
                {
                    title: '调度日志ID',
                    field: 'id',
                    width: '10',
                    widthUnit: '%',
                    align: 'left'
                },
                {
					title: '任务',
					field: 'jobId',
					width: '15',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
                        // build
                        let jobShow = '【'+ row.jobId +'】';
                        let jobDesc = $("#jobId").find("option[value='"+ row.jobId +"']").text();
                        if (jobDesc) {
                            jobShow += jobDesc;
                        }
                        if (jobShow.length > 10) {
                            jobShow = jobShow.substr(0, 10) + '...';
                        }
						// show
						return jobShow;
					}
				},{
					title: '调度时间',
					field: 'triggerTime',
					width: '15',
					widthUnit: '%',
					formatter: function(value, row, index) {
						return value?moment(value).format("YYYY-MM-DD HH:mm:ss"):"";
					}
				},{
					title: '调度结果',
					field: 'triggerCode',
					width: '10',
					widthUnit: '%',
					formatter: function(value, row, index) {
						var html = value;
						if (value == 200) {			// 200, success
							html = '<span style="color: green">成功</span>';
						} else if (value > 0) {		// >0 or 500, fail
							html = '<span style="color: red">失败</span>';
						} else if (value == 0) {		// 0, original pass
							html = '';
						}
						return html;
					}
				},{
					title: '调度备注',
					field: 'triggerMsg',
					width: '10',
					widthUnit: '%',
					formatter: function(value, row, index) {
						return value?'<a class="logTips" href="javascript:;" >查看<span style="display:none;">'+ value +'</span></a>':'无';
					}
				},{
					title: '执行时间',
					field: 'handleTime',
					width: '15',
					widthUnit: '%',
					formatter: function(value, row, index) {
						return value?moment(value).format("YYYY-MM-DD HH:mm:ss"):"";
					}
				},{
					title: '执行结果',
					field: 'handleCode',
					width: '10',
					widthUnit: '%',
					formatter: function(value, row, index) {
						var html = value;
						if (value == 200) {			// 200, success
							html = '<span style="color: green">成功</span>';
						} else if (value == 502) {	// 502, timeout
							html = '<span style="color: red">失败(超时)</span>';
						} else if (value > 0) {		// >0 or 500, fail
							html = '<span style="color: red">失败</span>';
						} else if (value == 0) {		// 0, original pass
							html = '';
						}
						return html;
					}
				},{
					title: '执行备注',
					field: 'handleMsg',
					width: '10',
					widthUnit: '%',
					formatter: function(value, row, index) {
						return value?'<a class="logTips" href="javascript:;" >查看<span style="display:none;">'+ value +'</span></a>':'无';
					}
				}
			]
		});

		/**
		 * logDetail
		 */
		$("#data_operation").on('click', '.logDetail',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择一条数据');
				return;
			}
			var row = rows[0];

			window.open(base_url + '/joblog/logDetailPage?id=' + row.id);
		});

		/**
		 * log Kill
		 */
		$('#data_operation').on('click', '.logKill', function(){
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择一条数据');
				return;
			}
			var row = rows[0];

			// do kill
			layer.confirm( '确定终止任务?', {
				icon: 3,
				title: '系统提示' ,
				btn: [ '确定', '取消' ]
			}, function(index){
				layer.close(index);

				$.ajax({
					type : 'POST',
					url : base_url + '/joblog/logKill',
					data : {
						"id": row.id
					},
					dataType : "json",
					success : function(data){
						if (data.code == 200) {
							layer.open({
								title: '系统提示',
								btn: [ '确定' ],
								content: '操作成功' ,
								icon: '1',
								end: function(layero, index){
									// refresh table
									$('#data_filter .searchBtn').click();
								}
							});
						} else {
							layer.open({
								title: '系统提示',
								btn: [ '确定' ],
								content: (data.msg || '操作失败' ),
								icon: '2'
							});
						}
					},
				});
			});

		});

		/**
		 * delete selected logs
		 */
		$('#data_operation').on('click', '.deleteSelected', function(){
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length < 1) {
				layer.msg('请选择至少一条数据');
				return;
			}

			// collect ids
			var ids = [];
			for (var i = 0; i < rows.length; i++) {
				ids.push(rows[i].id);
			}

			// do delete
			layer.confirm( '确定删除选中的 ' + rows.length + ' 条日志?', {
				icon: 3,
				title: '系统提示' ,
				btn: [ '确定', '取消' ]
			}, function(index){
				layer.close(index);

				$.ajax({
					type : 'POST',
					url : base_url + '/joblog/deleteSelected',
					data : {
						"ids": ids.join(',')
					},
					dataType : "json",
					success : function(data){
						if (data.code == 200) {
							layer.open({
								title: '系统提示',
								btn: [ '确定' ],
								content: '删除成功' ,
								icon: '1',
								end: function(layero, index){
									// refresh table
									$('#data_filter .searchBtn').click();
								}
							});
						} else {
							layer.open({
								title: '系统提示',
								btn: [ '确定' ],
								content: (data.msg || '删除失败' ),
								icon: '2'
							});
						}
					},
				});
			});

		});

		/**
		 * clear Log
		 */
		$('#data_operation').on('click', '.clearLog', function(){

			var jobGroup = $('#jobGroup').val();
			var jobId = $('#jobId').val();

			var jobGroupText = $("#jobGroup").find("option:selected").text();
			var jobIdText = $("#jobId").find("option:selected").text();

			$('#clearLogModal input[name=jobGroup]').val(jobGroup);
			$('#clearLogModal input[name=jobId]').val(jobId);

			$('#clearLogModal .jobGroupText').val(jobGroupText);
			$('#clearLogModal .jobIdText').val(jobIdText);

			$('#clearLogModal').modal('show');

		});
		$("#clearLogModal .ok").on('click', function(){
			$.post(base_url + "/joblog/clearLog",  $("#clearLogModal .form").serialize(), function(data, status) {
				if (data.code == "200") {
					$('#clearLogModal').modal('hide');
					layer.open({
						title: '系统提示' ,
						btn: [ '确定' ],
						content: '日志清理成功' ,
						icon: '1',
						end: function(layero, index){
							// refresh table
							$('#data_filter .searchBtn').click();
						}
					});
				} else {
					layer.open({
						title: '系统提示' ,
						btn: [ '确定' ],
						content: (data.msg || '日志清理失败' ),
						icon: '2'
					});
				}
			});
		});
		$("#clearLogModal").on('hide.bs.modal', function () {
			$("#clearLogModal .form")[0].reset();
		});

		// ---------------------- ComAlertTec ----------------------

		/**
		 * logTips alert
		 */
		$('body').on('click', '.logTips', function(){
			var msg = $(this).find('span').html();
			ComAlertTec.show(msg);
		});

		// Com Alert by Tec theme
		var ComAlertTec = {
			html:function(){
				var html =
						'<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
						'	<div class="modal-dialog modal-lg-">' +
						'		<div class="modal-content-tec">' +
						'			<div class="modal-body">' +
						'				<div class="alert" style="color:#fff;word-wrap: break-word;">' +
						'				</div>' +
						'			</div>' +
						'				<div class="modal-footer">' +
						'				<div class="text-center" >' +
						'					<button type="button" class="btn btn-info ok" data-bs-dismiss="modal" >确定</button>' +
						'				</div>' +
						'			</div>' +
						'		</div>' +
						'	</div>' +
						'</div>';
				return html;
			},
			show:function(msg, callback){
				// dom init
				if ($('#ComAlertTec').length == 0){
					$('body').append(ComAlertTec.html());
				}

				// init com alert
				$('#ComAlertTec .alert').html(msg);
				$('#ComAlertTec').modal('show');

				$('#ComAlertTec .ok').click(function(){
					$('#ComAlertTec').modal('hide');
					if(typeof callback == 'function') {
						callback();
					}
				});
			}
		};

	});
</script>
<!-- 3-script end -->

</body>
</html>
