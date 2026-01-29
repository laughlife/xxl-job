<#-- import: style -->
<#macro commonStyle>

<#-- title、favicon、meta -->
<title>分布式任务调度平台｜XXL-JOB</title>
<link rel="icon" href="${request.contextPath}/static/favicon.ico" />
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<#-- css -->
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap5/css/bootstrap.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/Ionicons/css/ionicons.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/AdminLTE.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/skins/_all-skins.min.css">
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
<link rel="stylesheet" href="${request.contextPath}/static/plugins/nprogress/nprogress.css">
<link rel="stylesheet" href="${request.contextPath}/static/biz/common/bootstrap5-compat.css">

</#macro>

<#-- import: script -->
<#macro commonScript>

<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap5/js/bootstrap.bundle.min.js"></script>
<script src="${request.contextPath}/static/adminlte/dist/js/adminlte.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/jquery-slimscroll/jquery.slimscroll.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/fastclick/fastclick.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/plugins/layer/layer.js"></script>
<script src="${request.contextPath}/static/plugins/nprogress/nprogress.js"></script>
<script src="${request.contextPath}/static/plugins/fullscreen/jquery.fullscreen.js"></script>
<script>
	// init page param
	var base_url = '${request.contextPath}';
	var I18n = {
		// system
		system_tips: '系统提示',
		system_ok: '确定',
		system_close: '关闭',
		system_save: '保存',
		system_cancel: '取消',
		system_search: '搜索',
		system_reset: '重置',
		system_status: '状态',
		system_opt: '操作',
		system_opt_add: '新增',
		system_please_input: '请输入',
		system_please_choose: '请选择',
		system_success: '成功',
		system_fail: '失败',
		system_error: '错误',
		system_all: '全部',
		system_show: '查看',
		system_empty: '无',
		system_opt_suc: '操作成功',
		system_opt_fail: '操作失败',
		system_opt_edit: '编辑',
		system_opt_del: '删除',
		system_opt_copy: '复制',
		system_unvalid: '非法',
		system_not_found: '不存在',
		system_nav: '导航',
		system_digits: '整数',
		system_lengh_limit: '长度限制',
		system_permission_limit: '权限拦截',
		system_welcome: '欢迎',
		system_num_range: '数值范围限制',
		system_one: '一条',
		system_data: '数据',
		system_selected_nothing: '未选择',
		// logout
		logout_confirm: '确认注销登录?',
		logout_success: '注销成功',
		logout_fail: '注销失败',
		// change pwd
		change_pwd: '修改密码',
		change_pwd_suc_to_logout: '修改密码成功，即将注销登陆',
		change_pwd_field_oldpwd: '旧密码',
		change_pwd_field_newpwd: '新密码',
		// change skin
		change_skin: '切换主题'
	};
</script>

</#macro>
