<%@ page import="java.util.List" %>
<%@ page language="java" pageEncoding="utf-8" %>
<%@include file="./taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <title>错误页面</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>
<div class="content">
    <h2>${msg}</h2>
    <p class="text">
        由于您输入的projetId不合法,没有相应的页面
    </p>
</div>
</body>
</html>
