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
	<table border="1" cellspacing="0" cellpadding="0" width="100%" style="align:center;">
		<tr>
			<th>ID</th>
			<th>用户名称</th>
			<th>电话</th>
			<th>创建时间</th>
		</tr>
		<tr>
			<td>${userInfo.id}</td>
			<td>${userInfo.name}</td>
			<td>${userInfo.tel}</td>
			<td>${userInfo.createTime}</td>
		</tr>
	</table>
	<%--<script src="http://code.jquery.com/jquery.js"></script>--%>
	<%--<script src="/js/bootstrap.min.js"></script>--%>
</body>
</html>