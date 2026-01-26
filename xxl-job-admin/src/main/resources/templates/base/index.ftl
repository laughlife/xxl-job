<!DOCTYPE html>
<html>
<head>
    <#-- import macro -->
    <#import "../common/common.macro.ftl" as netCommon>

    <!-- 1-style start -->
    <@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/biz/common/admin.tab.css?v=3.4.0-SNAPSHOT">
    <!-- 1-style end -->

</head>
<body class="hold-transition skin-blue sidebar-mini fixed" >
<div class="wrapper" >

    <!-- 2-header start -->
    <header class="main-header">
        <!-- header logo -->
        <a href="${request.contextPath}/" class="logo">
            <span class="logo-mini"><b>XXL</b></span>
            <span class="logo-lg"><b>任务调度中心</b></span>
        </a>
        <nav class="navbar navbar-static-top" role="navigation">
            <!--header left -->
            <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button" style="padding:0 0 0 0px;">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <!--header right -->
            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">

                    <#-- login user -->
                    <li class="dropdown">
                        <a href="javascript:" class="dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false" style="font-weight: bold;">
                            欢迎：${xxl_sso_user.userName!}
                        </a>
                        <ul class="dropdown-menu" role="menu">
                            <li id="updatePwd" >
                                <a class="dropdown-item" href="javascript:"><i class="fa fa-key"></i> 修改密码</a>
                            </li>
                            <li id="changeSkin" >
                                <a class="dropdown-item" href="javascript:"><i class="fa fa-key"></i> 切换主题</a>
                            </li>
                            <li id="logoutBtn" >
                                <a class="dropdown-item" href="javascript:"><i class="fa fa-sign-out"></i> 注销</a>
                            </li>
                        </ul>
                    </li>

                </ul>
            </div>

        </nav>
    </header>
    <!-- 2-header end -->

    <!-- 3-left start -->
    <aside class="main-sidebar">
        <section class="sidebar" style="height: auto;" >
            <!-- sidebar menu -->
            <ul class="sidebar-menu" data-widget="tree" >
                <li class="header">导航</li>
                <#if resourceList?? && resourceList?size gt 0>
                    <@renderMenu resourceList />
                </#if>
            </ul>
            <#-- render menu -->
            <#macro renderMenu resourceList >
                <#list resourceList as resource>
                    <#if resource.type ==0>
                        <#-- catalog -->
                        <li class="treeview" style="height: auto;"  >
                            <a href="javascript:void(0);">
                                <i class="fa ${resource.icon}"></i>
                                <span>${resource.name}</span>
                                <span class="pull-right-container">
                                    <i class="fa fa-angle-left pull-right"></i>
                                </span>
                            </a>
                            <ul class="treeview-menu" >
                                <#if resource.children?? && resource.children?size gt 0>
                                    <@renderMenu resource.children />
                                </#if>
                            </ul>
                        </li>
                    <#elseif resource.type ==1>
                        <#-- mainMenu -->
                        <#if !(mainMenu?exists) >
                            <#assign mainMenu = resource />
                        <#elseif resource.order lt mainMenu.order >
                            <#assign mainMenu = resource />
                        </#if>
                        <#-- menu -->
                        <li class="nav-click">
                            <#-- url -->
                            <#assign resourceUrl = request.contextPath + resource.url />
                            <#if resource.url?starts_with("http") >
                                <#assign resourceUrl = resource.url />
                            </#if>
                            <#-- menu -->
                            <a class="J_menuItem" href="${resourceUrl}">
                                <i class="fa ${resource.icon}"></i>
                                <span>${resource.name}</span>
                            </a>
                        </li>
                    </#if>
                </#list>
            </#macro>
        </section>
    </aside>
    <!-- 3-left end -->

    <!-- 4-right start -->
    <div class="content-wrapper">

        <!-- Tabs -->
        <div class="content-tabs">
            <!-- left -->
            <button class="roll-nav roll-left J_tabLeft"><i class="fa fa-backward"></i></button>

            <!-- Tab -->
            <nav class="page-tabs J_menuTabs">
                <div class="page-tabs-content">
                    <#-- mainPage -->
                    <#if mainMenu?exists >
                        <a href="javascript:;" class="active J_menuTab noactive" data-id="${request.contextPath}${mainMenu.url}">${mainMenu.name}</a>
                    </#if>
                </div>
            </nav>

            <!-- right -->
            <button class="roll-nav roll-right J_tabRight"><i class="fa fa-forward"></i></button>
            <!-- opt -->
            <div class="btn-group roll-nav roll-right">
                <button class="dropdown-toggle" data-bs-toggle="dropdown">页签操作</button>
                <ul role="menu" class="dropdown-menu dropdown-menu-end">
                    <li class="tabCloseCurrent"><a class="dropdown-item">关闭当前</a></li>
                    <li class="J_tabCloseOther"><a class="dropdown-item">关闭其他</a></li>
                    <li class="J_tabCloseAll"><a class="dropdown-item">全部关闭</a></li>
                </ul>
            </div>
            <!-- refresh -->
            <a href="#" class="roll-nav roll-right tabReload"><i class="fa fa-refresh"></i> 刷新</a>
            <!-- fullscreen -->
            <a href="#" class="roll-nav roll-right fullscreen" id="fullScreen"><i class="fa fa-arrows-alt"></i></a>
        </div>
        <!-- Iframe Content -->
        <div class="J_mainContent" id="content-main" >
            <!-- Iframe -->
            <iframe class="J_iframe" width="100%" height="100%" src="${request.contextPath}${mainMenu.url}" frameborder="0" data-id="${request.contextPath}${mainMenu.url}" seamless></iframe>
        </div>

    </div>
    <!-- 4-right end -->

    <!-- 5-footer start -->
    <footer class="main-footer">
        Powered by <b>XXL-JOB</b> 3.4.0-SNAPSHOT
        <div class="pull-right hidden-xs">
            <strong>Copyright &copy; 2015-${.now?string('yyyy')} &nbsp;
                <a href="https://www.xuxueli.com/" target="_blank" >xuxueli</a>
                &nbsp;
                <a href="https://github.com/xuxueli/xxl-job" target="_blank" >github</a>
            </strong><!-- All rights reserved. -->
        </div>
    </footer>
    <!-- 5-footer end -->

</div>

<!-- 6-script start -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/biz/common/admin.tab.js?v=3.4.0-SNAPSHOT"></script>
<script src="${request.contextPath}/static/biz/common/admin.setting.js?v=3.4.0-SNAPSHOT"></script>
<script>
    $(function () {
        // init admin tab
        $.adminTab.initTab();
    });
</script>
<!-- 6-script end -->

</body>
</html>
