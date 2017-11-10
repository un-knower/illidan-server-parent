<%@ page import="java.util.List" %>
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
    <title>编辑输出表</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>
<div class="container theme-showcase form-horizontal col-md-12" >
    <table id="hive-table" class="table table-condensed" style="margin-bottom: 0px;">
        <tr><td style="border:none;"><h4>hive输出表</h4></td></tr>
        <tr>
            <td style="border:none;">
                <div class="form-group">
                    <label for="tableCode" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>目标表名</label>
                    <div class="col-md-7">
                        <input class="form-control" id="tableCode" placeholder="hive目标表名" value="${hiveTable.tableCode}" disabled="disabled">
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label for="tableDes" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;">目标表描述</label>
                    <div class="col-md-7">
                        <input class="form-control" id="tableDes" placeholder="hive目标表描述" value="${hiveTable.tableDes}" disabled="disabled">
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label for="dbId" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>数据库</label>
                    <div class="col-md-7">
                        <select class=" show-tick form-control" style="height: 34px;" id="dbId" name="dbId" title="" data-live-search="true" disabled="disabled">
                            <option value="${hiveTable.dbInfo.id}">${hiveTable.dbInfo.dbCode}</option>
                        </select>
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>存储格式</label>
                    <div class="col-md-5">
                        <select id="dataType" name="dataType" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true" disabled="disabled">
                            <option value="${hiveTable.dataType}">${hiveTable.dataType}</option>
                        </select>
                    </div>
                    <label class="col-md-2 control-label addBtn" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                        <button class="btn btn-info addBtn" onclick="addrow();">添加</button>
                    </label>
                </div>
            </td>
        </tr>

        <c:forEach begin="0" end="${hiveTable.fieldList.size()-1}"  var="index">
            <tr>
                <td style="border:none;">
                    <div class="form-group">
                        <label for="colName" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段名称</label>
                        <div class="col-md-7">
                            <input class="form-control" id="colName" name="colName" placeholder="字段名称" value="${hiveTable.fieldList.get(index).colName}" disabled="disabled">
                        </div>
                    </div>
                </td>
                <td style="border:none;">
                    <div class="form-group">
                        <label for="colType" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段类型</label>
                        <div class="col-md-7">
                            <select id="colType" name="colType" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true" disabled="disabled">
                                <option value="${hiveTable.fieldList.get(index).colType}">${hiveTable.fieldList.get(index).colType}</option>
                            </select>
                        </div>
                    </div>
                </td>
                <td style="border:none;">
                    <div class="form-group">
                        <label for="colDes" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;">字段描述</label>
                        <div class="col-md-7">
                            <input class="form-control" id="colDes" name="colDes" placeholder="字段描述" value="${hiveTable.fieldList.get(index).colDes}" disabled="disabled">
                        </div>
                    </div>
                </td>
                <td style="border:none;">
                    <div class="form-group">
                        <label class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>分区字段</label>
                        <div class="col-md-5 partitionCol">
                            <select id="isPartitionCol" name="isPartitionCol" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true" disabled="disabled">
                                <option value="${hiveTable.fieldList.get(index).isPartitionCol}">${hiveTable.fieldList.get(index).isPartitionCol==1?'是':'否'}</option>
                            </select>
                        </div>
                    </div>
                </td>
            </tr>
        </c:forEach>


        <tr id="exportTr">
            <td style="border:none;">
                <div class="form-group">
                    <label for="colName" class="col-md-5 control-label" style="padding-left: 0px;padding-right: 0px;padding-top: 0px;"><b class="text-danger">*</b>导出到mysql</label>
                    <div class="col-md-7">
                        <label>
                            <input type="radio" id="noExport2Mysql" name="export2Mysql" checked value="no" onclick="isExport2Mysql(this)"> 否
                        </label>
                        <label>
                            <input type="radio" id="export2Mysql" name="export2Mysql" value="yes" onclick="isExport2Mysql(this)"> 是
                        </label>
                    </div>

                </div>
            </td>
        </tr>
    </table>
    <table id="mysql-table" class="table table-condensed" style="margin-bottom: 0px;display: none;">
        <tr><td style="border:none;"><h4>mysql输出表</h4></td></tr>
        <tr>
            <td style="border:none;">
                <div class="form-group">
                    <label for="mysqlTableCode" class="col-md-5 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>目标表名</label>
                    <div class="col-md-7">
                        <input class="form-control" id="mysqlTableCode" placeholder="mysql目标表名" value="${mysqlTable.tableCode}" disabled="disabled">
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label for="mysqlTableDes" class="col-md-5 control-label" style="padding-left: 0px;padding-right: 0px;">目标表描述</label>
                    <div class="col-md-7">
                        <input class="form-control" id="mysqlTableDes" placeholder="目标表描述" value="${mysqlTable.tableDes}" disabled="disabled">
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label for="mysqlDbId" class="col-md-5 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>数据库</label>
                    <div class="col-md-7">
                        <select class=" show-tick form-control" style="height: 34px;width: 120px;" id="mysqlDbId" name="dbId" data-live-search="true" disabled="disabled">
                            <option value ="${mysqlTable.dbId}" >${mysqlTableDbCode}</option>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
    </table>
    <div class="col-sm-12 text-center">
        <button type="submit" class="btn btn-primary" onclick="edit();">保存</button>
        <button type="submit" class="btn btn-default" onclick="closeParentWindow();">返回</button>
    </div>
</div>

<script type="text/javascript">
    jQuery(function($) {
        var dDiv = document.getElementById("mysql-table");
        if ('${flag}' == "true") {
            dDiv.style.display = "block";
            $("#export2Mysql").attr("checked",true);
        }else{
            $("#noExport2Mysql").attr("checked",true);
        }
    });

    function addrow(){
        var hiveTable = $('#hive-table');
        var trNum = $("#hive-table").find("tr").length;
        console.info(trNum);
        if(trNum ==4){
            $(".delBtn").show();
            $(".partitionCol").attr("class","col-md-3 partitionCol");
        }
        var addtr = $("<tr>"+
            "<td style='border:none;'><div class='form-group'><label for='colName' class='col-md-4 control-label' style='padding-left: 0px;padding-right: 0px;'><b class='text-danger'>*</b>字段名称</label><div class='col-md-7'><input class='form-control' id='colName' name='colName'' placeholder='字段名称'></div></div></td>"+
            "<td style='border:none;'><div class='form-group'><label for='colType' class='col-md-4 control-label' style='padding-left: 0px;padding-right: 0px;'><b class='text-danger'>*</b>字段类型</label><div class='col-md-7'><select id='colType' name='colType' style='height: 34px;' class=' show-tick form-control' title='' data-live-search='true'><option value='-1'>字段类型</option><option value='int'>int</option><option value='bigint'>bigint</option><option value='float'>float</option><option value='double'>double</option><option value='string'>string</option><option value='timestamp'>timestamp</option></select></div></div></td>"+
            "<td style='border:none;'><div class='form-group'><label for='colDes' class='col-md-4 control-label' style='padding-left: 0px;padding-right: 0px;'>字段描述</label><div class='col-md-7'><input class='form-control' id='colDes' name='colDes' placeholder='字段描述'></div></div></td>"+
            "<td style='border:none;'><div class='form-group'><label class='col-md-4 control-label' style='padding-left: 0px;padding-right: 0px;'><b class='text-danger'>*</b>分区字段</label><div class='col-md-5 partitionCol'><select id='isPartitionCol' name='isPartitionCol' style='height: 34px;' class=' show-tick form-control' title='' data-live-search='true'><option value='-1'>请选择</option><option value='1'>是</option><option value='0'>否</option></select></div><label class='col-md-3 control-label' style='padding-left: 0px;padding-right: 0px;padding-top: 0px'><button class='btn btn-danger delBtn' onclick='deleteTrRow(this);'>删除</button></label></div> </td>"+
            "</tr>");
//        addtr.appendTo(hiveTable);
        $('#hive-table tr:eq(-1)').before(addtr);
//        $(tr).parent().parent().parent().parent().after(addtr);
    }

    function deleteTrRow(tr){
        var trNum = $("#hive-table").find("tr").length;
        console.info(trNum);
        if (trNum ==5){
            $(tr).parent().parent().parent().parent().remove();
            $(".delBtn").hide();
            $(".partitionCol").attr("class","col-md-4 partitionCol");
        }else {
            $(tr).parent().parent().parent().parent().remove();
        }
    }

    function edit() {
        alert("功能待完善");
    }

    function closeTip(){
        window.parent.searchList();
        closeParentWindow();
    }

    function closeParentWindow(){
        window.parent.closeWindow();
    }

    function isExport2Mysql(obj) {
        var dDiv = document.getElementById("mysql-table");
        if(obj.value=="yes"){
            dDiv.style.display = "block";
        }else{
            dDiv.style.display = "none";
        }
    }

</script>
</body>
</html>
