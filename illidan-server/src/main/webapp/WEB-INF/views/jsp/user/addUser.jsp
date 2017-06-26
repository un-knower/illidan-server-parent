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
<form id="add" name="add" action ="<%=request.getContextPath()%>/addUserCommit" method="post">
	用户名:&nbsp;<input type="text" id="name" name="name"/><br/>
	电&nbsp;&nbsp;话:&nbsp;<input type="text" id="tel" name="tel"/><br/>
	<input type="submit" value="提交"/>
	<input type="reset" value="重置"/>
</form>
<%--<script src="http://code.jquery.com/jquery.js"></script>--%>
	<%--<script src="/js/bootstrap.min.js"></script>--%>
</body>
</html>