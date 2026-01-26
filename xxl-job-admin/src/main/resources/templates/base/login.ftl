<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>

	<!-- 1-style start -->
	<@netCommon.commonStyle />
	<!-- iCheck -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/iCheck/square/blue.css">
	<!-- 1-style end -->

</head>
<body class="hold-transition login-page">

	<!-- 2-biz start -->
	<div class="login-box">
		<div class="login-logo">
			<a><b>XXL-JOB</b></a>
		</div>
		<form id="loginForm" method="post" >
			<div class="login-box-body">
				<p class="login-box-msg">任务调度中心</p>
				<div class="form-group mb-3 position-relative">
					<span class="position-absolute top-50 start-0 translate-middle-y ps-3 text-secondary pe-none">
						<i class="fa fa-envelope"></i>
					</span>
					<input type="text" name="userName" class="form-control ps-5" placeholder="请输入登录账号" maxlength="20" >
				</div>
				<div class="form-group mb-3 position-relative">
					<span class="position-absolute top-50 start-0 translate-middle-y ps-3 text-secondary pe-none">
						<i class="fa fa-lock"></i>
					</span>
					<input type="password" name="password" class="form-control ps-5" placeholder="请输入登录密码" maxlength="20" >
				</div>
				<div class="row mt-2">
					<div class="col-8">
		              	<div class="checkbox icheck">
		                	<label>
		                  		<input type="checkbox" name="ifRemember" > &nbsp; 记住密码
		                	</label>
						</div>
		            </div><!-- /.col -->
		            <div class="col-4">
						<button type="submit" class="btn btn-primary btn-block btn-flat">登录</button>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!-- 2-biz end -->

<!-- 3-script start -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script>
$(function () {

	// input iCheck
	$('input').iCheck({
		checkboxClass: 'icheckbox_square-blue',
		radioClass: 'iradio_square-blue',
		increaseArea: '20%' // optional
	});

	// login Form Valid
	var loginFormValid = $("#loginForm").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
			userName : {
				required : true ,
				minlength: 4,
				maxlength: 20
			},
			password : {
				required : true ,
				minlength: 4,
				maxlength: 20
			}
		},
		messages : {
			userName : {
				required  : '请输入登录账号',
				minlength : '登录账号不应低于4位'
			},
			password : {
				required  : '请输入登录密码'  ,
				minlength : '登录密码不应低于4位'
				/*,maxlength:"登录密码不应超过18位"*/
			}
		},
		highlight : function(element) {
			$(element).closest('.form-group').addClass('has-error');
		},
		success : function(label) {
			label.closest('.form-group').removeClass('has-error');
			label.remove();
		},
		errorPlacement : function(error, element) {
			element.closest('.form-group').append(error);
		},
		submitHandler : function(form) {
			$.post(base_url + "/auth/doLogin", $("#loginForm").serialize(), function(data, status) {
				if (data.code === 200) {
					layer.msg('登录成功');
					setTimeout(function(){
						window.location.href = base_url + "/";
					}, 500);
				} else {
					layer.open({
						title: '系统提示',
						btn: [ '确定' ],
						content: (data.msg || '登录失败' ),
						icon: '2'
					});
				}
			});
		}
	});

});
</script>
<!-- 3-script end -->


</body>
</html>
