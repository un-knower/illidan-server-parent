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
    <title>新增输出表</title>
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
                        <input class="form-control" id="tableCode" placeholder="hive目标表名" value="${hiveTable.tableCode}">
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label for="tableDes" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;">目标表描述</label>
                    <div class="col-md-7">
                        <input class="form-control" id="tableDes" placeholder="hive目标表描述" value="${hiveTable.tableDes}">
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label for="dbId" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>数据库</label>
                    <div class="col-md-7">
                        <select class=" show-tick form-control" style="height: 34px;" id="dbId" name="dbId" title="" data-live-search="true">
                            <option value="-1">hive数据库</option>
                            <c:forEach begin="0" end="${hiveDbInfoList.size()-1}"  var="index">
                                <option value ="${hiveDbInfoList.get(index).id}" >${hiveDbInfoList.get(index).dbCode}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>存储格式</label>
                    <div class="col-md-7">
                        <select id="dataType" name="dataType" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                            <option value="parquet" selected>parquet</option>
                            <option value="textfile">textfile</option>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
        <c:choose>
            <c:when test="${isCopy==-1}">
                <tr>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colName" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段名称</label>
                            <div class="col-md-7">
                                <input class="form-control" name="colName" placeholder="字段名称" value="date_type">
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colType" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段类型</label>
                            <div class="col-md-7">
                                    <%--<input class="form-control" name="colType" placeholder="字段类型" value="string">--%>
                                <select id="colType" name="colType" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                                    <option value='-1'>字段类型</option>
                                    <option value="int">int</option>
                                    <option value="bigint">bigint</option>
                                    <option value="float">float</option>
                                    <option value="double">double</option>
                                    <option value="string" selected>string</option>
                                    <option value="timestamp">timestamp</option>
                                </select>
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colDes" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;">字段描述</label>
                            <div class="col-md-7">
                                <input class="form-control" name="colDes" placeholder="字段描述" value="统计粒度类型">
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>分区字段</label>
                            <div class="col-md-4 partitionCol">
                                <select name="isPartitionCol" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                                    <option value="-1">请选择</option>
                                    <option value="1" selected>是</option>
                                    <option value="0">否</option>
                                </select>
                            </div>
                            <label class="col-md-2 control-label addBtn" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                                <button class="btn btn-info addBtn" onclick="addrow(this);">添加</button>
                            </label>
                            <label class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                                <button id="delBtn" class='btn btn-danger delBtn' onclick='deleteTrRow(this);'>删除</button>
                            </label>
                        </div>
                    </td>
                </tr>

                <tr>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colName" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段名称</label>
                            <div class="col-md-7">
                                <input class="form-control" name="colName" placeholder="字段名称" value="month_p">
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colType" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段类型</label>
                            <div class="col-md-7">
                                    <%--<input class="form-control" name="colType" placeholder="字段类型" value="string">--%>
                                <select id="colType" name="colType" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                                    <option value='-1'>字段类型</option>
                                    <option value="int">int</option>
                                    <option value="bigint">bigint</option>
                                    <option value="float">float</option>
                                    <option value="double">double</option>
                                    <option value="string" selected>string</option>
                                    <option value="timestamp">timestamp</option>
                                </select>
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colDes" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;">字段描述</label>
                            <div class="col-md-7">
                                <input class="form-control" name="colDes" placeholder="字段描述" value="月分区">
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>分区字段</label>
                            <div class="col-md-4 partitionCol">
                                <select name="isPartitionCol" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                                    <option value="-1">请选择</option>
                                    <option value="1" selected>是</option>
                                    <option value="0">否</option>
                                </select>
                            </div>
                            <label class="col-md-2 control-label addBtn" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                                <button class="btn btn-info addBtn" onclick="addrow(this);">添加</button>
                            </label>
                            <label class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                                <button class='btn btn-danger delBtn' onclick='deleteTrRow(this);'>删除</button>
                            </label>
                        </div>
                    </td>
                </tr>

                <tr>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colName" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段名称</label>
                            <div class="col-md-7">
                                <input class="form-control" id="colName" name="colName" placeholder="字段名称" value="day_p">
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colType" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段类型</label>
                            <div class="col-md-7">
                                    <%--<input class="form-control" id="colType" name="colType" placeholder="字段类型" value="string">--%>
                                <select id="colType" name="colType" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                                    <option value='-1'>字段类型</option>
                                    <option value="int">int</option>
                                    <option value="bigint">bigint</option>
                                    <option value="float">float</option>
                                    <option value="double">double</option>
                                    <option value="string" selected>string</option>
                                    <option value="timestamp">timestamp</option>
                                </select>
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label for="colDes" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;">字段描述</label>
                            <div class="col-md-7">
                                <input class="form-control" id="colDes" name="colDes" placeholder="字段描述" value="天分区">
                            </div>
                        </div>
                    </td>
                    <td style="border:none;">
                        <div class="form-group">
                            <label class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>分区字段</label>
                            <div class="col-md-4 partitionCol">
                                <select id="isPartitionCol" name="isPartitionCol" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                                    <option value="-1">请选择</option>
                                    <option value="1" selected>是</option>
                                    <option value="0">否</option>
                                </select>
                            </div>
                            <label class="col-md-2 control-label addBtn" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                                <button class="btn btn-info addBtn" onclick="addrow(this);">添加</button>
                            </label>
                            <label class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                                <button class='btn btn-danger delBtn' onclick='deleteTrRow(this);'>删除</button>
                            </label>
                        </div>
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach begin="0" end="${hiveTable.fieldList.size()-1}"  var="index">
                    <tr>
                        <input class="form-control" type="hidden" id="colIndex" name="colIndex" value="${hiveTable.fieldList.get(index).colIndex}"/>
                        <td style="border:none;">
                            <div class="form-group">
                                <label for="colName" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段名称</label>
                                <div class="col-md-7">
                                    <input class="form-control" id="colNameCopy" name="colName" placeholder="字段名称" value="${hiveTable.fieldList.get(index).colName}">
                                </div>
                            </div>
                        </td>
                        <td style="border:none;">
                            <div class="form-group">
                                <label for="colType" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>字段类型</label>
                                <div class="col-md-7">
                                    <select id="colTypeCopy" name="colType" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                                        <option value='-1'>字段类型</option>
                                        <option value="int" <c:if test ="${hiveTable.fieldList.get(index).colType eq 'int'}">selected</c:if>>int</option>
                                        <option value="bigint" <c:if test ="${hiveTable.fieldList.get(index).colType eq 'bigint'}">selected</c:if>>bigint</option>
                                        <option value="float" <c:if test ="${hiveTable.fieldList.get(index).colType eq 'float'}">selected</c:if>>float</option>
                                        <option value="double" <c:if test ="${hiveTable.fieldList.get(index).colType eq 'double'}">selected</c:if>>double</option>
                                        <option value="string" <c:if test ="${hiveTable.fieldList.get(index).colType eq 'string'}">selected</c:if>>string</option>
                                        <option value="timestamp" <c:if test ="${hiveTable.fieldList.get(index).colType eq 'timestamp'}">selected</c:if>>timestamp</option>
                                    </select>
                                </div>
                            </div>
                        </td>
                        <td style="border:none;">
                            <div class="form-group">
                                <label for="colDes" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;">字段描述</label>
                                <div class="col-md-7">
                                    <input class="form-control" id="colDesCopy" name="colDes" placeholder="字段描述" value="${hiveTable.fieldList.get(index).colDes}">
                                </div>
                            </div>
                        </td>
                        <td style="border:none;">
                            <div class="form-group">
                                <label class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>分区字段</label>
                                <div class="col-md-4 partitionCol">
                                    <select id="isPartitionColCopy" name="isPartitionCol" style="height: 34px;" class=" show-tick form-control" title="" data-live-search="true">
                                        <%--<option value="${hiveTable.fieldList.get(index).isPartitionCol}">${hiveTable.fieldList.get(index).isPartitionCol==1?'是':'否'}</option>--%>
                                        <option value="-1" >请选择</option>
                                        <option value="1" <c:if test ="${hiveTable.fieldList.get(index).isPartitionCol eq '1'}">selected</c:if>>是</option>
                                        <option value="0" <c:if test ="${hiveTable.fieldList.get(index).isPartitionCol eq '0'}">selected</c:if>>否</option>
                                    </select>
                                </div>
                                <label class="col-md-2 control-label addBtn" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                                    <button class="btn btn-info addBtn" onclick="addrow(this);">添加</button>
                                </label>
                                <label class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;padding-top: 0px">
                                    <button class='btn btn-danger delBtn' onclick='deleteTrRow(this);'>删除</button>
                                </label>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>

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
                    <label for="mysqlTableCode" class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>目标表名</label>
                    <div class="col-md-7">
                        <input class="form-control" id="mysqlTableCode" placeholder="mysql目标表名" value="${mysqlTable.tableCode}">
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label for="mysqlTableDes" class="col-md-4 control-label" style="padding-left: 0px;padding-right: 0px;">目标表描述</label>
                    <div class="col-md-7">
                        <input class="form-control" id="mysqlTableDes" placeholder="目标表描述" value="${mysqlTable.tableDes}">
                    </div>
                </div>
            </td>
            <td style="border:none;">
                <div class="form-group">
                    <label for="mysqlDbId" class="col-md-3 control-label" style="padding-left: 0px;padding-right: 0px;"><b class="text-danger">*</b>数据库</label>
                    <div class="col-md-7">
                        <select class=" show-tick form-control" style="height: 34px;width: 120px;" id="mysqlDbId" name="dbId" data-live-search="true">
                            <option value="-1">mysql数据库</option>
                            <c:forEach begin="0" end="${mysqlDbInfoList.size()-1}"  var="index">
                                <option value ="${mysqlDbInfoList.get(index).id}" >${mysqlDbInfoList.get(index).dbCode}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
    </table>
    <div class="col-sm-12 text-center">
        <button type="submit" class="btn btn-primary" onclick="add();">保存</button>
        <button type="submit" class="btn btn-default" onclick="closeParentWindow();">返回</button>
    </div>
</div>

<script>
    jQuery(function($) {
        var dDiv = document.getElementById("mysql-table");
        if ('${flag}' == "true") {
            dDiv.style.display = "";
            $("#export2Mysql").attr("checked",true);
        }else{
            $("#noExport2Mysql").attr("checked",true);
        }

        $("#dbId").val(${hiveTable.dbInfo.id});
        if('${isCopy}'!=-1){
            $("#dataType").val('${hiveTable.dataType}');
        }
        $("#mysqlDbId").val(${mysqlTable.dbId});
    });

    function addrow(tr){
        var hiveTable = $('#hive-table');
        var trNum = $("#hive-table").find("tr").length;
        if(trNum ==4){
            $(".delBtn").show();
            $(".partitionCol").attr("class","col-md-3 partitionCol");
        }
        var addtr = $("<tr>"+
            "<td style='border:none;'><div class='form-group'><label for='colName' class='col-md-4 control-label' style='padding-left: 0px;padding-right: 0px;'><b class='text-danger'>*</b>字段名称</label><div class='col-md-7'><input class='form-control' id='colName' name='colName'' placeholder='字段名称'></div></div></td>"+
            "<td style='border:none;'><div class='form-group'><label for='colType' class='col-md-4 control-label' style='padding-left: 0px;padding-right: 0px;'><b class='text-danger'>*</b>字段类型</label><div class='col-md-7'><select id='colType' name='colType' style='height: 34px;' class=' show-tick form-control' title='' data-live-search='true'><option value='-1'>字段类型</option><option value='int'>int</option><option value='bigint'>bigint</option><option value='float'>float</option><option value='double'>double</option><option value='string'>string</option><option value='timestamp'>timestamp</option></select></div></div></td>"+
            "<td style='border:none;'><div class='form-group'><label for='colDes' class='col-md-4 control-label' style='padding-left: 0px;padding-right: 0px;'>字段描述</label><div class='col-md-7'><input class='form-control' id='colDes' name='colDes' placeholder='字段描述'></div></div></td>"+
            "<td style='border:none;'><div class='form-group'><label class='col-md-3 control-label' style='padding-left: 0px;padding-right: 0px;'><b class='text-danger'>*</b>分区字段</label><div class='col-md-4 partitionCol'><select id='isPartitionCol' name='isPartitionCol' style='height: 34px;' class=' show-tick form-control' title='' data-live-search='true'><option value='-1'>请选择</option><option value='1'>是</option><option value='0'>否</option></select></div><label class='col-md-2 control-label addBtn' style='padding-left: 0px;padding-right: 0px;padding-top: 0px'><button class='btn btn-info addBtn' onclick='addrow(this);'>添加</button></label><label class='col-md-3 control-label' style='padding-left: 0px;padding-right: 0px;padding-top: 0px'><button class='btn btn-danger delBtn' onclick='deleteTrRow(this);'>删除</button></label></div> </td>"+
            "</tr>");
//        addtr.appendTo(hiveTable);
//        $('#hive-table tr:eq(-1)').before(addtr);
        $(tr).parent().parent().parent().parent().after(addtr);
    }

    function deleteTrRow(tr){
        var trNum = $("#hive-table").find("tr").length;
        if (trNum ==5){
            $(tr).parent().parent().parent().parent().remove();
            $(".delBtn").hide();
            $(".partitionCol").attr("class","col-md-4 partitionCol");
        }else {
            $(tr).parent().parent().parent().parent().remove();
        }
    }

    function add() {
        var fullHiveTable = {};
        var hiveTableWithField = {};
        var hiveFieldList = [];
        $("#hive-table tr:gt(1)").each(function(){
            var fieldInfo = {};
            $("input,select",this).each(function(){ //遍历行内的input select的值
                if($(this).prop("name")=="colName"){
                    fieldInfo.colName = $(this).val().toString();
                }
                if($(this).prop("name")=="colType"){
                    fieldInfo.colType = $(this).val().toString();
                }
                if($(this).prop("name")=="colDes"){
                    fieldInfo.colDes = $(this).val().toString();
                }
                if($(this).prop("name")=="isPartitionCol"){
                    fieldInfo.isPartitionCol = $(this).val().toString();
                }
            });
            if(JSON.stringify(fieldInfo) != "{}"){
                hiveFieldList.push(fieldInfo);
            }
        });
        //hive table
        hiveTableWithField.tableCode = $("#tableCode").val().toString();
        hiveTableWithField.dbId = $("#dbId").val().toString();
        hiveTableWithField.dataType = $("#dataType").val().toString();
        hiveTableWithField.tableDes = $("#tableDes").val().toString();
        hiveTableWithField.fieldList = hiveFieldList;
        fullHiveTable.hiveTable = hiveTableWithField;
        //mysql table
        if($('input:radio[id="export2Mysql"]:checked').val()!=null){
            var mysqlTable = {};
            mysqlTable.tableCode = $("#mysqlTableCode").val().toString();
            mysqlTable.dbId = $("#mysqlDbId").val().toString();
            mysqlTable.tableDes = $("#mysqlTableDes").val().toString();
            fullHiveTable.mysqlTable = mysqlTable;
        }
        $.ajax({
            type: 'POST',
            url: '<%=path%>/table/add',
            data: JSON.stringify(fullHiveTable),
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
            error: function() {
                modalAlert("提示", "新增输出表失败,请重新添加", closeWindow, "error");
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

    function isExport2Mysql(obj) {
        var dDiv = document.getElementById("mysql-table");
        if(obj.value=="yes"){
            dDiv.style.display = "";
        }else{
            dDiv.style.display = "none";
        }
    }

</script>
</body>
</html>
