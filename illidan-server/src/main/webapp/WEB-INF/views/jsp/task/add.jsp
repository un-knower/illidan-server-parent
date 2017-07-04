<%@ page import="java.util.List" %>
<%@ page language="java" pageEncoding="utf-8" %>
<%@include file="../taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <title>新增任务</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>
<div class="container theme-showcase form-horizontal" role="main">
    <div class="panel panel-default">
        <div class="panel-body">
            <input class="form-control" type="hidden" id="groupId" name="groupId" value="${groupId}"/>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="taskCode" class="col-md-2 control-label">任务code</label>
                    <div class="col-md-6">
                        <input class="form-control" id="taskCode" placeholder="请输入任务code">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="addUser" class="col-md-2 control-label">任务添加用户</label>
                    <div class="col-md-6">
                        <input class="form-control" id="addUser" placeholder="请输入任务添加用户">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">执行方式</label>
                    <div class="col-md-6">
                        <select id="executeType" name="executeType" class="selectpicker show-tick form-control" multiple data-live-search="true">
                            <option value="day" selected>day</option>
                            <option value="week">week</option>
                            <option value="month">month</option>
                            <option value="quarter">quarter</option>
                            <option value="ytd">ytd</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">目标数据库</label>
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
                    <label for="tableCode" class="col-md-2 control-label">目标表</label>
                    <div class="col-md-6">
                        <input class="form-control" id="tableCode" placeholder="请输入目标表">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="tableDes" class="col-md-2 control-label">目标表描述</label>
                    <div class="col-md-6">
                        <input class="form-control" id="tableDes" placeholder="请输入目标表描述">
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">存储格式</label>
                    <div class="col-md-6">
                        <select id="dataType" name="dataType" class="selectpicker show-tick form-control">
                            <option value="parquet" selected>parquet</option>
                            <option value="textfile">textfile</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">分区字段</label>
                    <div class="col-md-6">
                        <select id="partitionCol" name="partitionCol" class="selectpicker show-tick form-control" multiple data-live-search="true">
                            <option value="date_type" selected>date_type</option>
                            <option value="product_line">product_line</option>
                            <option value="month_p">month_p</option>
                            <option value="day_p">day_p</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">业务分析语句</label>
                    <div class="col-md-6">
                        <textarea id="content" name="content" cols="20" rows="4"
                                  class="form-control"></textarea>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">任务描述</label>
                    <div class="col-md-6">
                        <textarea id="taskDes" name="taskDes" cols="20" rows="4"
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

    $('.selectpicker').selectpicker({
        'selectedText': 'cat'
    });

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
        var taskFull = {};
        var table = {};
        var fieldList = [];

        var fieldArray = $("#partitionCol").val().toString().split(",");
        for (var i=0;i<=fieldArray.length-1;++i){
            var fieldInfo = {};
            fieldInfo.colName = fieldArray[i];
            fieldList.push(fieldInfo);
        }
        taskFull.taskCode = $("#taskCode").val();
        taskFull.groupId = $("#groupId").val();
        taskFull.addUser = $("#addUser").val();
        taskFull.executeType = $("#executeType").val().toString();
        taskFull.content = $("#content").val();
        taskFull.taskDes = $("#taskDes").val();

        table.tableCode = $("#tableCode").val();
        table.dbId = $("#dbId").val().toString();
        table.dataType = $("#dataType").val().toString();
        table.tableDes = $("#tableDes").val().toString();
        table.fieldList = fieldList;
        taskFull.table = table;

        $.ajax({
            type: 'POST',
            url: '/task/add',
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
                modalAlert("提示", "新增产品失败,请重新添加", closeWindow, "error");
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
