<%@ page language="java" pageEncoding="utf-8" %>
<%@include file="../taglibs.jsp"%>
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
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="groupCode" class="col-md-2 control-label">任务组code</label>
                    <div class="col-md-6">
                        <input class="form-control" id="groupCode" placeholder="请输入任务组code">
                    </div>
                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">
                    <label for="projectId" class="col-md-2 control-label">所属项目</label>
                    <div class="col-md-6">
                        <select class="form-control" id="projectId" name="projectId" data-placeholder="">
                            <c:forEach begin="0" end="${project.size()-1}"  var="index">
                                <option value ="${project.get(index).id}" >${project.get(index).projectCode}</option>
                            </c:forEach>
                        </select>
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
                    <label for="schedule" class="col-md-2 control-label">调度策略</label>
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
<%--<script src="http://s.360img.cn/wgroup/js/common/jquery-ui.min.js"></script>--%>
<%--<script src="http://s.360img.cn/wgroup/js/common/jquery.fileupload.js"></script>--%>
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

    //    jQuery(function($) {
    //        autocomplete("supplier", "/supplier/getSupplierList", 'organizationName', "id", "organizationName", 1);
    //    });

    function add() {
        var group = {};
        group.groupCode = $("#groupCode").val();
        group.groupDes = $("#groupDes").val();
        group.schedule = $("#schedule").val();
        group.email = $("#email").val();
        group.projectId = $("#projectId").val();
        $.ajax({
            type: 'POST',
            url: '/group/add',
            data: JSON.stringify(group),
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
                modalAlert("提示", "新增任务组失败,请重新添加", closeWindow, "error");
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
