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
    <title>新增项目</title>
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
                    <label for="projectCode" class="col-md-2 control-label"><b class="text-danger">*</b>项目code</label>
                    <div class="col-md-6">
                        <input class="form-control" id="projectCode" placeholder="请输入项目code">
                    </div>
                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">
                    <label for="ownerId" class="col-md-2 control-label">所有者</label>
                    <div class="col-md-6">
                        <select class="form-control" id="ownerId" name="ownerId" data-placeholder="">
                            <c:forEach begin="0" end="${owner.size()-1}"  var="index">
                                <option value ="${owner.get(index).id}" >${owner.get(index).ownerName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>

            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">项目描述</label>
                    <div class="col-md-6">
                        <textarea id="projectDes" name="projectDes" cols="20" rows="4"
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
        var project = {};
        project.projectCode = $("#projectCode").val();
        project.projectDes = $("#projectDes").val();
        project.ownerId = $("#ownerId").val();
        $.ajax({
            type: 'POST',
            url: '<%=path%>/project/add',
            data: JSON.stringify(project),
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
