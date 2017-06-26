<%@ page language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <title>产品管理</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>
<div class="container theme-showcase form-horizontal" role="main">
    <div class="panel panel-default">
        <div class="panel-body">
            <input type="hidden" id="id" value="${product.id}"/>
            <div class="col-sm-6">
                <div class="form-group">
                    <label for="name" class="col-md-2 control-label"><b class="text-danger">*</b>产品名称</label>
                    <div class="col-md-6">
                        <input class="form-control" id="name" placeholder="请输入姓名" value="${product.name}">
                    </div>
                </div>
            </div>

            <%--<div class="col-sm-6">--%>
            <%--<div class="form-group">--%>
            <%--<label class="col-md-2 control-label"><b class="text-danger">*</b>产品类型</label>--%>
            <%--<div class="col-md-6">--%>
            <%--<select class="chosen-select form-control" name="type" data-placeholder=""--%>
            <%--id="type" url="/sysDic/get?name=product_type" optionValue="id"--%>
            <%--optionText="name" value="${product.type}">--%>
            <%--</select>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label"><b class="text-danger">*</b>产品售价</label>
                    <div class="col-md-6">
                        <input class="form-control" id="sellPrice" placeholder="产品售价" value="${product.sellPrice}">
                    </div>
                </div>
            </div>

            <%--<c:if test="${product.type=='0'}">--%>
            <%--<div class="col-sm-6" id="en_name_div">--%>
            <%--<div class="form-group">--%>
            <%--<label for="enName" class="col-md-2 control-label">英文名称</label>--%>
            <%--<div class="col-md-6">--%>
            <%--<input class="form-control" id="enName" placeholder="请输入英文名称" value="${product.enName}">--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <%--<div class="col-sm-6" id="ch_general_name_div">--%>
            <%--<div class="form-group">--%>
            <%--<label for="chGeneralName" class="col-md-2 control-label">中文通用名称</label>--%>
            <%--<div class="col-md-6">--%>
            <%--<input class="form-control" id="chGeneralName" placeholder="请输入中文通用名称" value="${product.chGeneralName}">--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <%--<div class="col-sm-6" id="en_general_name_div">--%>
            <%--<div class="form-group">--%>
            <%--<label for="enGeneralName" class="col-md-2 control-label">英文通用名称</label>--%>
            <%--<div class="col-md-6">--%>
            <%--<input class="form-control" id="enGeneralName" placeholder="请输入英文通用名称" value="${product.enGeneralName}">--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <div class="col-sm-6" id="spec_div">
                <div class="form-group">
                    <label class="col-md-2 control-label">产品规格</label>
                    <div class="col-md-6">
                        <input class="form-control" id="spec" placeholder="产品规格" value="${product.spec}">
                    </div>
                </div>
            </div>

            <%--<div class="col-sm-6" id="en_spec_div">--%>
            <%--<div class="form-group">--%>
            <%--<label class="col-md-2 control-label">英文产品规格</label>--%>
            <%--<div class="col-md-6">--%>
            <%--<input class="form-control" id="enSpec" placeholder="英文产品规格" value="${product.enSpec}">--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <div class="col-sm-6" id="manufactor_div">
                <div class="form-group">
                    <label class="col-md-2 control-label">生产厂家</label>
                    <div class="col-md-6">
                        <input class="form-control" id="manufactor" placeholder="生产厂家" value="${product.manufactor}">
                    </div>
                </div>
            </div>

            <%--<div class="col-sm-6" id="en_manufactor_div">--%>
            <%--<div class="form-group">--%>
            <%--<label class="col-md-2 control-label">英文生产厂家</label>--%>
            <%--<div class="col-md-6">--%>
            <%--<input class="form-control" id="enManufactor" placeholder="英文生产厂家" value="${product.enManufactor}">--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <div class="col-sm-6" id="supplyPrice_div">
                <div class="form-group">
                    <label class="col-md-2 control-label">产品供货价</label>
                    <div class="col-md-6">
                        <input class="form-control" id="supplyPrice" placeholder="产品供货价" value="${product.supplyPrice}">
                    </div>
                </div>
            </div>

            <div class="col-sm-6" id="marketPrice_div">
                <div class="form-group">
                    <label class="col-md-2 control-label">产品市场价</label>
                    <div class="col-md-6">
                        <input class="form-control" id="marketPrice" placeholder="产品市场价" value="${product.marketPrice}">
                    </div>
                </div>
            </div>
            <%--</c:if>--%>

            <%--<c:if test="${product.type=='1' || product.type=='2'}">--%>
            <div class="col-sm-6" id="medicalInstitution_div">
                <div class="form-group">
                    <label class="col-md-2 control-label">所属医疗机构</label>
                    <div class="col-md-6">
                        <input class="form-control" id="medicalInstitution" placeholder="所属医疗机构" value="${product.medicalInstitution}">
                    </div>
                </div>
            </div>
            <%--</c:if>--%>

            <%--<c:if test="${product.type=='2'}">--%>
            <div class="col-sm-6" id="contact_div">
                <div class="form-group">
                    <label class="col-md-2 control-label">联系方式</label>
                    <div class="col-md-6">
                        <input class="form-control" id="contact" placeholder="联系方式" value="${product.contact}">
                    </div>
                </div>
            </div>

            <div class="col-sm-6" id="major_div">
                <div class="form-group">
                    <label class="col-md-2 control-label">专业(科室)</label>
                    <div class="col-md-6">
                        <input class="form-control" id="major" placeholder="专业(科室)" value="${product.major}">
                    </div>
                </div>
            </div>
            <%--</c:if>--%>

            <%--<div class="col-sm-6">--%>
            <%--<div class="form-group">--%>
            <%--<label class="col-md-2 control-label"><b class="text-danger">*</b>所属供应商</label>--%>
            <%--<div class="col-md-6">--%>
            <%--<select class="chosen-select form-control" name="supplier" data-placeholder=""--%>
            <%--id="supplier" url="/supplier/getSupplierList" optionValue="id"--%>
            <%--optionText="organizationName" value="${product.supplier}">--%>
            <%--</select>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <div class="col-sm-6">
                <div class="form-group">
                    <label class="col-md-2 control-label">简介</label>
                    <div class="col-md-6">
                        <textarea id="remark" cols="20" rows="5"
                                  class="form-control">${product.remark}</textarea>
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
//        autocomplete("supplier", "/supplier/getSupplierList", 'organizationName', "id", "organizationName", 1);
    });


    function add() {
        if (!$("#name").val()){
            $("#name").parents(".form-group").addClass("has-error");
            return false;
        }
//        if (!$("#type").val()){
//            $("#type").parents(".form-group").addClass("has-error");
//            return false;
//        }
//
        if (!$("#sellPrice").val()){
            $("#sellPrice").parents(".form-group").addClass("has-error");
            return false;
        }

        var product = {};
        product.id = $("#id").val();
        product.name = $("#name").val();
        product.type = $("#type").val();
        product.spec = $("#spec").val();
        product.manufactor = $("#manufactor").val();
        product.supplyPrice = $("#supplyPrice").val();
        product.marketPrice = $("#marketPrice").val();
        product.medicalInstitution = $("#medicalInstitution").val();
        product.contact = $("#contact").val();
        product.major = $("#major").val();
        product.supplier = $("#supplier").val();
        product.sellPrice = $("#sellPrice").val();
        product.remark = $("#remark").val();
//        product.chGeneralName = $("#chGeneralName").val();
//        product.enGeneralName = $("#enGeneralName").val();
//        product.enName = $("#enName").val();
//        product.enSpec = $("#enSpec").val();
//        product.enManufactor = $("#enManufactor").val();
        $.ajax({
            type: 'POST',
            url: '/product/edit',
            data: JSON.stringify(product),
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
                modalAlert("提示", "修改产品失败,请重新添加", closeWindow, "error");
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
