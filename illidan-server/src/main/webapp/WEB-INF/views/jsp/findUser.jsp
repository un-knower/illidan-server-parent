<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>--%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<html lang="zh-cn">
<head>
<meta charset="utf-8">

<title>用户</title>
</head>
<!-- CSS件 -->
<%--<link rel="stylesheet" href="/css/bootstrap.min.css">--%>
<%--<link rel="stylesheet" href="/css/customer/login.css">--%>
<body>

    <div>
        <form id="query" name="query" action ="<%=request.getContextPath()%>/findByName" method="post">
            用户名:&nbsp;<input type="text" id="name" name="name"/><br/>
            电&nbsp;&nbsp;话:&nbsp;<input type="text" id="tel" name="tel"/><br/>
            <input type="submit" value="提交"/>
            <input type="reset" value="重置"/>
        </form>
    </div>
	<table border="1" cellspacing="0" cellpadding="0" width="100%" style="align:center;">
		<tr>
			<th>ID</th>
			<th>用户名称</th>
			<th>电话</th>
			<th>创建时间</th>
		</tr>
		<c:forEach var="user" items="${userInfos}">
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.tel}</td>
                <td>${user.createTime}</td>
            </tr>
		</c:forEach>
	</table>
    <input type="button" name="" value="添加用户" onclick="javascript:window.location='<%=request.getContextPath()%>/addUser';" />
	<%--<script src="http://code.jquery.com/jquery.js"></script>--%>
	<%--<script src="/js/bootstrap.min.js"></script>--%>
</body>
</html>