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
        <form class="form-inline" role="form" action="query_cdr.do"
              method="get">
            <input class="form-control" type="search" id="query" name="simNum"/>
            <button type="submit" class="btn btn-default">话单查询</button>
        </form>
        <table id="tab_cdr" class="display" width="100%" cellspacing="0">
            <thead>
            <tr>
                <th>org</th>
                <th>dest</th>
                <th>type</th>
                <th>time</th>
            </tr>
            </thead>
            <tfoot>
            <tr>
                <th>org</th>
                <th>dest</th>
                <th>type</th>
                <th>time</th>
            </tr>
            </tfoot>
            <tbody>
            <s:iterator value="list" var="cdr">
                <tr>
                    <td><s:property value="#cdr.org"/></td>
                    <td><s:property value="#cdr.dest"/></td>
                    <td><s:property value="#cdr.type"/></td>
                    <td><s:property value="#cdr.dt"/></td>
                </tr>
            </s:iterator>
            </tbody>
        </table>

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
    $(document).ready(function(){
        $('#tab_cdr').DataTable();
    });
</script>

</html>