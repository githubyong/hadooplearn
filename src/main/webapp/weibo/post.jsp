<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title>Bootstrap 101 Template</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link href="<%=request.getContextPath()%>/js/jquery-ui.css" rel="stylesheet" type="text/css">
    <link href="<%=request.getContextPath()%>/js/jquery.dataTables.min.css" rel="stylesheet" type="text/css">

</head>
<body>

<div class="container">

    <%@ include file="../topbar.jsp" %>

    <!-- Main component for a primary marketing message or call to action -->
    <div class="jumbotron">
        <div class="bs-callout bs-callout-warning">
            <h4>发微博</h4>

            <form action="post.do" class="form-horizontal">
                <div class="form-group">
                    <textarea rows="3" class="form-control" name="postContent"></textarea>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-default">提交</button>
                </div>
            </form>

        </div>
    </div>

</div>
<!-- /container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
</body>

<link rel="stylesheet"
      , href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css"/>
<script type="text/javascript"
        src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.dataTables.min.js" type="text/javascript"></script>

<script type="text/javascript">
    $(document).ready(function () {
        $('#tab_cdr').DataTable();
        $('#datepicker1').datepicker({dateFormat: "yymmdd"});
        $('#datepicker2').datepicker({dateFormat: "yymmdd"});
    });
</script>

</html>