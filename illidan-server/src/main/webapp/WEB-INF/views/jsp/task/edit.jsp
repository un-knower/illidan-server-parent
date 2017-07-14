<%@ page language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../taglibs.jsp"%>
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
            <input class="form-control" type="hidden" id="tableId" name="tableId" value="${tableId}"/>
            <input class="form-control" type="hidden" id="mysqlTableId" name="mysqlTableId" value="${mysqlTableId}"/>
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
                        <input class="form-control" id="tableCode" placeholder="请输入目标表" value="${task.table.tableCode}">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="tableDes" class="col-md-2 control-label">目标表描述</label>
                    <div class="col-md-6">
                        <input class="form-control" id="tableDes" placeholder="请输入目标表描述" value="${task.table.tableDes}">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label"><b class="text-danger">*</b>分区字段</label>
                    <div class="col-md-6">
                        <select id="partitionCol" name="partitionCol" class="selectpicker show-tick form-control" multiple data-live-search="true">
                            <option value="date_type">date_type</option>
                            <option value="product_line">product_line</option>
                            <option value="month_p">month_p</option>
                            <option value="day_p">day_p</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label"><b class="text-danger">*</b>存储格式</label>
                    <div class="col-md-6">
                        <select id="dataType" name="dataType" class="selectpicker show-tick form-control">
                            <option value="parquet">parquet</option>
                            <option value="textfile">textfile</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label"><b class="text-danger">*</b>执行方式</label>
                    <div class="col-md-6">
                        <select id="executeType" name="executeType" class="selectpicker show-tick form-control" multiple data-live-search="true">
                            <option value="day">day</option>
                            <option value="week">week</option>
                            <option value="month">month</option>
                            <option value="quarter">quarter</option>
                            <option value="ytd">ytd</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6" id="mysqlDataBaseDiv" style="display: none">
                <div class="form-group">
                    <label class="col-md-1 control-label">mysql目标数据库</label>
                    <div class="col-md-6">
                        <select disabled id="mysqlDataBase" name="mysqlDataBase" class="selectpicker show-tick form-control" title="请选择mysql目标数据库" data-live-search="true">
                            <c:forEach begin="0" end="${mysqlDbInfoList.size()-1}"  var="index">
                                <option value ="${mysqlDbInfoList.get(index).id}" >${mysqlDbInfoList.get(index).dbCode}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6" id="mysqlTableDiv" style="display: none">
                <div class="form-group">
                    <label class="col-md-1 control-label">mysql目标表</label>
                    <div class="col-md-6">
                        <input disabled class="form-control" id="mysqlTable" value="${mysqlTable}">
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
        var dbId = '${task.table.dbId}';
        var dataType = '${task.table.dataType}';
        var partitionColList = '${fieldList}'.toString().split(",");
        $('#partitionCol').selectpicker('val', partitionColList);
        $('#dataType').selectpicker('val', dataType);
        $('#dbId').selectpicker('val', dbId);
        $('#mysqlDataBase').selectpicker('val', ${mysqlDbId});
        $('#executeType').selectpicker('val', executeTypeArray);
        if ('${flag}' == "true") {
            var dDiv = document.getElementById("mysqlDataBaseDiv");
            var tDiv = document.getElementById("mysqlTableDiv");
            dDiv.style.display = "block";
            tDiv.style.display = "block";
        }
    });

    function add() {
        var taskFull = {};
        var table1 = {};
        var table2 = {};
        var fieldList = [];

        if ($("#partitionCol").val()!=null && $("#partitionCol").val()!="") {
            var fieldArray = $("#partitionCol").val().toString().split(",");
            for (var i = 0; i <= fieldArray.length - 1; ++i) {
                var fieldInfo = {};
                fieldInfo.colName = fieldArray[i];
                fieldList.push(fieldInfo);
            }
        }
        var tableList = [];
        //hive table
        table1.id = $("#tableId").val();
        table1.tableCode = $("#tableCode").val().toString();
        table1.dbId = $("#dbId").val().toString();
        table1.dataType = $("#dataType").val().toString();
        table1.tableDes = $("#tableDes").val().toString();
        table1.fieldList = fieldList;
        tableList.push(table1);
        //mysql table
        if($("#mysqlTable").val().toString()!=null && $("#mysqlTable").val().toString()!="" && $("#mysqlDataBase").val().toString()!=null && $("#mysqlDataBase").val().toString()!=""){
            table2.id = $("#mysqlTableId").val();
            table2.tableCode = $("#mysqlTable").val().toString();
            table2.dbId = $("#mysqlDataBase").val().toString();
            tableList.push(table2);
        }

        taskFull.tableList = tableList;
        taskFull.taskCode = $("#taskCode").val();
        taskFull.groupId = $("#groupId").val();
        taskFull.id = $("#id").val();
        taskFull.addUser = $("#addUser").val();
        if ($("#executeType").val()!=null && $("#executeType").val()!=""){
            taskFull.executeType = $("#executeType").val().toString();
        }
        taskFull.content = $("#content").val();
        taskFull.taskDes = $("#taskDes").val();
        $.ajax({
            type: 'POST',
            url: '/task/edit',
            data: JSON.stringify(taskFull),
            contentType: 'application/json', //设置请求头信息
            dataType: 'json',
            async: false,
            success: function (data) {
                if (data.result == true) {
                    modalAlert("提示", data.msg, closeTip, "ok");
                } else {
                    modalAlert("提示", data.msg, closeWindow, "error");
                }
            },
            error: function (XMLHttpRequest, data, textStatus) {
//                alert(eval(data));
//                alert("status:" + XMLHttpRequest.status);
//                alert("readyState:" + XMLHttpRequest.readyState);
//                alert("textStatus:" + textStatus);
                modalAlert("提示", "修改任务失败,请重新修改", closeWindow, "error");
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
