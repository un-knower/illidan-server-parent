<%@ page language="java" pageEncoding="utf-8" %>
<%@include file="../taglibs.jsp"%>
<%
    String path = request.getContextPath();
%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <title>新增任务组</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>
<div class="container theme-showcase form-horizontal" role="main">
    <div class="panel panel-default">
        <div class="panel-body">
            <input class="form-control" type="hidden" id="projectId" name="projectId" value="${projectId}"/>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="groupCode" class="col-md-2 control-label"><b class="text-danger">*</b>任务组code</label>
                    <div class="col-md-6">
                        <input class="form-control" id="groupCode" placeholder="请输入任务组code">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="email" class="col-md-2 control-label">任务失败邮件发送人</label>
                    <div class="col-md-6">
                        <input class="form-control" id="email" placeholder="请输入任务失败邮件发送人">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">调度策略</label>
                    <label>
                        <input type="radio" name="schedule" checked value="no" onclick="isSchedule(this)"> 无
                    </label>
                    <label>
                        <input type="radio" name="schedule" value="yes" onclick="isSchedule(this)"> 有
                    </label>
                </div>
            </div>
            <div class="col-sm-6" id="scheduleDiv" style="display: none">
                <div class="form-group">
                    <div class="col-md-6">
                        <input class="form-control" id="schedule" placeholder="调度策略">
                    </div>
                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">任务组描述</label>
                    <div class="col-md-6">
                        <textarea id="groupDes" name="groupDes" cols="20" rows="4"
                                  class="form-control"></textarea>
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

    $('#workTime').datetimepicker({
        icons: {
            time: 'fa fa-clock-o',
            date: 'fa fa-calendar',
            up: 'fa fa-chevron-up',
            down: 'fa fa-chevron-down',
            previous: 'fa fa-chevron-left',
            next: 'fa fa-chevron-right',
            today: 'fa fa-arrows ',
            clear: 'fa fa-trash',
            close: 'fa fa-times'
        },
        format: 'YYYY-MM-DD HH:mm:ss',
        extraFormats: ['YYYY-MM-DD HH:mm:ss', 'YYYY-MM-DD HH:mm:ss']

    }).next().on(ace.click_event, function () {
        $(this).prev().focus();
    });

    function add() {
        var group = {};
        group.groupCode = $("#groupCode").val();
        group.groupDes = $("#groupDes").val();
        group.schedule = $("#schedule").val();
        group.email = $("#email").val();
        group.projectId = $("#projectId").val();
        $.ajax({
            type: 'POST',
            url: '<%=path%>/group/add',
            data: JSON.stringify(group),
            contentType: 'application/json', //设置请求头信息
            dataType: 'json',
            async: false,
            success: function (data) {
                if (data.result == true) {
                    modalAlert("提示", data.msg, closeTip, "ok");
                } else {
                    // modalAlert("提示", data.msg, closeWindow, "error");
                    modalAlert("提示", data.msg, closeTip, "error");
                }
            },
            error: function (XMLHttpRequest, data, textStatus) {
//                alert(eval(data));
//                alert("status:" + XMLHttpRequest.status);
//                alert("readyState:" + XMLHttpRequest.readyState);
//                alert("textStatus:" + textStatus);
                modalAlert("提示", "新增任务组失败,请重新添加", closeTip, "error");
                // modalAlert("提示", "新增任务组失败,请重新添加", closeWindow, "error");
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

    function isSchedule(obj) {
        var oDiv = document.getElementById("scheduleDiv");
        if(obj.value=="yes"){
            oDiv.style.display = "block";
        }else{
            oDiv.style.display = "none";
            $("#schedule").val("0");
        }
    }

</script>
</body>
</html>
