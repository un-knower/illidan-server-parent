<!DOCTYPE html>

<html lang="zh-cn">
<head>
    <meta charset="utf-8">

    <title>用户</title>
</head>
<body>
<div>
    <form id="query" name="query" action="/findByName" method="post">
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
<#list userInfos as user>
    <tr>
        <td>${user.id}</td>
        <td>${user.name}</td>
        <td>${user.tel}</td>
        <td><#if user.createTime ?? >${user.createTime}</#if></td>
    </tr>
</#list>
</table>
<input type="button" name="" value="添加用户" onclick="javascript:window.location='./addUser';"/>

</body>
</html>