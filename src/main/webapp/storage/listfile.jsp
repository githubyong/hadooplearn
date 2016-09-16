<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>



<html>
<head>
<title>深入浅出Hadoop</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="../js/bootstrap-3.2.0-dist/css/bootstrap.css"
	rel="stylesheet" media="screen">
</head>
<body>

	<div class="container">

		<%@ include file="../topbar.jsp"%>

		<!-- Main component for a primary marketing message or call to action -->
		<div class="jumbotron">
				<table class="table table-hover">
				
				<th>文件名</th>
				<th>操作</th>
				<th>大小</th>
				<th>上传时间</th>
				<s:iterator value="fileList" id="file">
					<tr>
						<td><s:property value="#file.name" /></td>
						<td>
							<a href="download.do?fileName=<s:property value="#file.name" />"><span class="glyphicon glyphicon-download-alt"></span></a>
							<span class="icon-bar"></span>
							<a href="delfile.do?delFilename=<s:property value="#file.name" />"><span class="glyphicon glyphicon-remove"></span></a>
						</td>
						<td>
						<s:property value="#file.size" />
						</td>
						<td><s:property value="#file.dt" /></td>
					</tr>
					</s:iterator>
				</table>
		</div>

	</div>
	<!-- /container -->


	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	<script src="../js/bootstrap-3.2.0-dist/js/bootstrap.js"></script>
</body>
</html>