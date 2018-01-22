<%@ page language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../taglibs.jsp"%>
<%
    String path = request.getContextPath();
%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <title>编辑任务</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>
<div class="container theme-showcase form-horizontal" role="main">
    <div class="panel panel-default">
        <div class="panel-body">
            <input class="form-control" type="hidden" id="groupId" name="groupId" value="${groupId}"/>
            <%--<input class="form-control" type="hidden" id="tableId" name="tableId" value="${tableId}"/>--%>
            <%--<input class="form-control" type="hidden" id="mysqlTableId" name="mysqlTableId" value="${mysqlTableId}"/>--%>
            <input type="hidden" id="id" value="${task.id}"/>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="taskCode" class="col-md-2 control-label"><b class="text-danger">*</b>任务code</label>
                    <div class="col-md-6">
                        <input class="form-control" id="taskCode" placeholder="请输入任务code" value="${task.taskCode}">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="addUser" class="col-md-2 control-label"><b class="text-danger">*</b>任务添加用户</label>
                    <div class="col-md-6">
                        <input class="form-control" id="addUser" placeholder="请输入任务添加用户" value="${task.addUser}">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label"><b class="text-danger">*</b>目标数据库</label>
                    <div class="col-md-6">
                        <select id="dbId" name="dbId" class="selectpicker show-tick form-control" data-live-search="true">
                            <c:forEach begin="0" end="${dbInfo.size()-1}"  var="index">
                                <option value ="${dbInfo.get(index).id}" >${dbInfo.get(index).dbCode}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="tableCode" class="col-md-2 control-label"><b class="text-danger">*</b>目标表</label>
                    <div class="col-md-6">
                        <select id="tableCode" name="tableCode" class="selectpicker show-tick form-control" title="请选择目标表" data-live-search="true">
                            <c:forEach begin="0" end="${tableInfo.size()-1}"  var="index">
                                <option value ="${tableInfo.get(index).id}" >${tableInfo.get(index).tableCode}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label"><b class="text-danger">*</b>执行方式</label>
                    <div class="col-md-6">
                        <select id="executeType" name="executeType" class="selectpicker show-tick form-control" multiple data-live-search="true">
                            <option value="hour">hour</option>
                            <option value="day">day</option>
                            <option value="week">week</option>
                            <option value="month">month</option>
                            <option value="quarter">quarter</option>
                            <option value="ytd">ytd</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-12">
                <div class="form-group">
                    <label class="col-md-2 control-label"><b class="text-danger">*</b>业务分析语句</label>
                    <div class="col-md-6">
                        <textarea id="content" name="content" cols="20" rows="6"
                                  class="form-control">${task.content}</textarea>
                    </div>
                </div>
            </div>
            <div class="col-sm-12">
                <div class="form-group">
                    <label class="col-md-2 control-label">任务描述</label>
                    <div class="col-md-6">
                        <textarea id="taskDes" name="taskDes" cols="20" rows="2"
                                  class="form-control">${task.taskDes}</textarea>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-sm-12 text-center">
        <button type="submit" class="btn btn-primary" onclick="add();">保存</button>
        <button type="submit" class="btn btn-default" onclick="closeParentWindow();">返回</button>
    </div>
</div>
<script>
    jQuery(function($) {
        $('.selectpicker').selectpicker({
            'selectedText': 'cat'
        });
        var executeTypeArray = '${task.executeType}'.split(",");
        var dbId = '${task.fullHiveTable.hiveTable.dbId}';
        var tableId = '${task.tableId}';
        $('#dbId').selectpicker('val', dbId);
        $('#tableCode').selectpicker('val', tableId);
        $("#dbId").change(function () {
            var dbId = $("#dbId").val();
            $("#tableCode option").remove();
            $.ajax({
                type:"POST",
                url: "<%=path%>/task/getTables",
                data: "dbId=" + dbId,
                success: function(data) {
                    if (data != null && data.length > 0) {
                        var html = "";
                        for (var i = 0; i < data.length; i++) {
                            html += "<option value='" + data[i].id + "'>" + data[i].tableCode + "</option>";
                        }
                    }
                    $("#tableCode").html(html).selectpicker('refresh');
                }
            })
        });
        $('#executeType').selectpicker('val', executeTypeArray);
    });

    function add() {
        var taskFull = {};
        var fullHiveTable = {};
        var hiveTable = {};
        hiveTable.dbId = $("#dbId").val().toString();
        hiveTable.id = $("#tableCode").val().toString();
        fullHiveTable.hiveTable = hiveTable;
        taskFull.fullHiveTable = fullHiveTable;
        taskFull.taskCode = $("#taskCode").val();
        taskFull.groupId = $("#groupId").val();
        taskFull.id = $("#id").val();
        taskFull.addUser = $("#addUser").val();
        if ($("#executeType").val()!=null && $("#executeType").val()!=""){
            taskFull.executeType = $("#executeType").val().toString();
        }
        taskFull.content = $("#content").val();
        taskFull.taskDes = $("#taskDes").val();

        var method = "edit";
        if('${isCopy}'=='0'){
            method = "edit";
        }else{
            method = "add";
        }

        $.ajax({
            type: 'POST',
            url: '<%=path%>/task/'+method,
            data: JSON.stringify(taskFull),
            contentType: 'application/json', //设置请求头信息
            dataType: 'json',
            async: false,
            success: function (data) {
                if (data.status == "200") {
                    modalAlert("提示", data.msg, closeTip, "ok");
                } else {
                    modalAlert("提示", data.msg, closeWindow, "error");
                }
            },
            error: function (data) {
                modalAlert("提示", "修改任务失败,请重新修改"+data.msg, closeWindow, "error");
            }
        });
    }

    function closeTip(){
        window.parent.searchList();
        closeParentWindow();
    }

    function closeParentWindow(){
        window.parent.closeWindow();
    }
</script>
</body>
</html>
