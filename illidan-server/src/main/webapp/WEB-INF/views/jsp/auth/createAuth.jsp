<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@include file="../taglibs.jsp"%>
<%
    String path = request.getContextPath();
%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <title></title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>

<div class="container">
    <br>
    <button type="button" class="btn btn-success" onclick="createDBAuth()">初始化数据库权限</button>

    <button type="button" class="btn btn-success" onclick="createProjectAuth()">初始化工程权限</button>

</div>

<!-- inline scripts related to this page -->
<script type="text/javascript">

    function createDBAuth() {
        $.ajax({
            type: 'POST',
            url: '<%=path%>/toCreateDBAuth',
            dataType: 'json',
            async: false,
            success: function (data) {
                console.info(data.status);
                if (data.status == "200") {
                    modalAlert("提示", data.msg, closeTip, "ok");
                } else {
                    modalAlert("提示", data.msg, closeWindow, "error");
                }
            }
        });
    }

    function createProjectAuth() {
        $.ajax({
            type: 'POST',
            url: '<%=path%>/toCreateProjectAuth',
            dataType: 'json',
            async: false,
            success: function (data) {
                console.info(data.status);
                if (data.status == "200") {
                    modalAlert("提示", data.msg, closeTip, "ok");
                } else {
                    modalAlert("提示", data.msg, closeWindow, "error");
                }
            }
        });
    }

    function closeTip(){
        // window.parent.searchList();
        closeParentWindow();
    }

    function closeParentWindow(){
        window.parent.closeWindow();
    }

</script>
</body>
</html>
