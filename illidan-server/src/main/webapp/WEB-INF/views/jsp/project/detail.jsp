<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <title>产品详情</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>
<div class="container">
    <div class="panel-body" id="mainDiv">
        <table id="baseInfo" class="table table-striped">
            <%--<caption>--%>
            <%--<h1>产品详情</h1>--%>
            <%--</caption>--%>
            <tbody>
            <tr>
                <td><strong>产品ID:</strong>${product.id}</td> <td><strong>产品名称:</strong>${product.name}</td>
            </tr>
            <tr>
                <td><strong>产品售价:</strong>${product.sellPrice}</td> <td><strong>中文产品规格:</strong>${product.spec}</td>
            </tr>

            <tr>
                <td><strong>中文生产厂家:</strong>${product.manufactor}</td> <td><strong>所属医疗机构:</strong>${product.medicalInstitution}</td>
            </tr>
            <tr>
                <td><strong>产品供货价:</strong>${product.supplyPrice}</td> <td><strong>产品市场价:</strong>${product.marketPrice}</td>
            </tr>
            <tr>
                <td><strong>联系方式:</strong>${product.contact}</td> <td><strong>专业(科室):</strong>${product.major}</td>
            </tr>

            <tr>
                <td><strong>创建人:</strong>${product.creator}</td> <td><strong>创建时间:</strong>${createTime}</td>
            </tr>
            <tr>
                <td colspan="2"><strong>简介:</strong>${product.remark}</td>
            </tr>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>
