<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Bootstrap 101 Template</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->



</head>
<body>

	<div class="container">

		<%@ include file="../topbar.jsp"%>

		<!-- Main component for a primary marketing message or call to action -->
		<div class="jumbotron">
			<form class="form-inline" role="form" action="search.do"
				method="get">
				<input class="form-control" type="search" id="query" name="text" />
				<button type="submit" class="btn btn-default">Search</button>
			</form>

			<h2>
				<s:property value="text" />
			</h2>
		</div>

	</div>
	<!-- /container -->


	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
</body>

<link rel="stylesheet"
	, href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" />
<script type="text/javascript"
	src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>

<script type="text/javascript">
	$(document).ready(function(){
		$("#query").autocomplete({
			source : function(request, response){
				$.ajax({
					url : "${pageContext.request.contextPath}/suggest.do",
					dataType : "json",
					data : {
						query : $("#query").val()
					},
					success : function(data){
						response($.map(data.result, function(item){
							return {value:item}
						}));
					}
				});
			},
			minLength:1,
		});
	});

</script>

</html>