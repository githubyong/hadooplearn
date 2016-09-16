<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<!DOCTYPE html>

<html>
<head>
    <title>Bootstrap 101 Template</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link href="<%=request.getContextPath()%>/js/my.css" rel="stylesheet" media="screen">
    <link href="<%=request.getContextPath()%>/js/bootstrap-3.2.0-dist/css/bootstrap.css" rel="stylesheet"
          media="screen">
</head>
<body>
<!-- Static navbar -->
<div class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index.jsp">Hadoop课程</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">云存储 <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="listfile.do">我的文件</a></li>
                        <li><a href="goto_uplaod_page.do">上传文件</a></li>
                    </ul>
                </li>
                <li><a href="search.do">搜索</a></li>
                <li><a href="query_cdr.do">话单查询</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">微博 <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="inbox.do">我的主页</a></li>
                        <li><a href="post.do">发送微博</a></li>
                        <li><a href="weibo.do">关注好友</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">Action</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li class="divider"></li>
                        <li class="dropdown-header">Nav header</li>
                        <li><a href="#">Separated link</a></li>
                        <li><a href="#">One more separated link</a></li>
                    </ul>
                </li>
            </ul>

            <ul class="nav navbar-nav navbar-right">

                <div class="navbar-collapse collapse">
                    <%
                        String username = (String) session.getAttribute("USERNAME");
                        if (username == null || username.length() == 0) {
                    %>
                    <form class="navbar-form navbar-right" role="form" action="login.do" method="post">

                        <div class="form-group">
                            <input name="name" type="text" placeholder="用户名" class="form-control">
                        </div>
                        <div class="form-group">
                            <input name="password" type="password" placeholder="密码" class="form-control">
                        </div>
                        <button type="submit" class="btn btn-success">登录</button>
                        <button formaction="register.do" type="submit" class="btn btn-success">注册</button>
                    </form>


                    <%} else { %>

                    <form class="navbar-form navbar-right" role="form" action="logout.do" method="get">
                        <div class="form-group">
                            <label>你好，<%=username %>
                            </label>
                        </div>
                        <button type="submit" class="btn btn-success">注销</button>
                    </form>
                    <%} %>
                </div>
            </ul>
        </div>
        <!--/.nav-collapse -->
    </div>
    <!--/.container-fluid -->
</div>


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->

<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap-3.2.0-dist/js/bootstrap.js"></script>
</body>
</html>