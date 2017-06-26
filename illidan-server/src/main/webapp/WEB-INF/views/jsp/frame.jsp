<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%
    String path = request.getContextPath();
%>
<html  manifest="cache.appcache">
<link rel="stylesheet" type="text/css" media="screen"
      href="<%=path%>/static/css/bootstrap-datetimepicker.css">
<link rel="stylesheet" href="<%=path%>/static/css/bootstrap.min.css"/>
<link rel="stylesheet" href="<%=path%>/static/css/bootstrap-theme.min.css">
<link rel="stylesheet" href="<%=path%>/static/css/font-awesome.css"/>
<link rel="stylesheet" href="<%=path%>/static/css/theme.css"/>
<link rel="stylesheet" href="<%=path%>/static/css/chosen.css"/>

<%--<link rel="icon" href="//r.360img.cn/images/H5/common/favicon.ico" >--%>

<script src="<%=path%>/static/js/ace-extra.js"></script>
<!-- HTML5shiv and Respond.js for IE8 to support HTML5 elements and media queries -->
<%--<!--[if lte IE 8]>--%>
<%--<script src="<%=path%>/static/js/html5shiv.js"></script>--%>
<%--<script src="<%=path%>/static/js/respond.js"></script>--%>
<%--<![endif]-->--%>
<script src="<%=path%>/static/js/jquery.js"></script>
<%--<!--[if IE]>--%>
<%--<script type="text/javascript">--%>
<%--window.jQuery || document.write("<script src='<%=path%>/static/js/jquery1x.js'>" + "<" + "/script>");--%>
<%--</script>--%>
<%--<![endif]-->--%>
<%--<script type="text/javascript">--%>
<%--if ('ontouchstart' in document.documentElement) document.write("<script src='<%=path%>/static/js/jquery.mobile.custom.js'>" + "<" + "/script>");--%>
<%--</script>--%>
<script src="<%=path%>/static/js/bootstrap.js"></script>
<script src="<%=path%>/static/js/moment.js"></script>
<script src="<%=path%>/static/js/bootstrap-datetimepicker.js"></script>
<!-- page specific plugin scripts -->
<script src="<%=path%>/static/js/jquery.dataTables.js"></script>
<script src="<%=path%>/static/js/jquery.dataTables.bootstrap.js"></script>
<script src="<%=path%>/static/js/dataTables.buttons.js"></script>
<script src="<%=path%>/static/js/buttons.flash.js"></script>
<script src="<%=path%>/static/js/buttons.html5.js"></script>
<script src="<%=path%>/static/js/buttons.print.js"></script>
<script src="<%=path%>/static/js/buttons.colVis.js"></script>
<script src="<%=path%>/static/js/dataTables.select.js"></script>
<script src="<%=path%>/static/js/jquery.form.3.46.0.js"></script>
<script src="<%=path%>/static/js/popCommon.js"></script>
<script src="<%=path%>/static/js/chosen.jquery.js"></script>
<script src="<%=path%>/static/js/openWindow.js"></script>
<script src="<%=path%>/static/js/jquery.autocomplete.min.js"></script>
<script src="<%=path%>/static/js/selectize.min.js"></script>
<script src="<%=path%>/static/js/ajaxfileupload.js"></script>
<script src="<%=path%>/static/js/jquery.form.js"></script>
<script src="<%=path%>/static/js/jquery-ui.min.js"></script>
<script src="<%=path%>/static/js/jquery.iframe-transport.js"></script>
<script src="<%=path%>/static/js/jquery.fileupload.js"></script>

<script type="text/javascript">
    jQuery(function($) {

        $(".chosen-select").chosen({
            no_results_text: "My language message.",
            placeholder_text : "My language message.",
            search_contains: true,
            disable_search_threshold: 10
        });
//        initSelect();
        initKeyUp();
        initCheckBox();
    });


    Date.prototype.Format = function (fmt) { //author: meizz
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "H+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    function formatDate(longTime) {
        var unixTimestamp = new Date(longTime).Format("yyyy-MM-dd HH:mm:ss");
        return unixTimestamp;
    }

    //两位小数的金额
    function isMoney(str) {
        var reg=/^[1-9]{1}\d*(\.\d{1,2})?$/;
        return reg.test(str);
    }

    var gExportModal = false;
    function getParam(param) {
        if(!param){
            param = {};
        }
        var paramCon = $(".form-inline [query='query']");
        var mapKey = "";
        paramCon.each(function (i) {
            if ($(this).val() != "" && $(this).val() != -1) {
                mapKey = mapKey + $(this).attr("id") + ":" + $(this).val() + ",";
                param[$(this).attr("id")] = $(this).val();
            }
        });
//        mapKey  = mapKey.substring(0,mapKey.length-1);
//        if(!gExportModal){
//            $('#exportButton').attr("params", mapKey);
//            gExportModal = $("#exportButton").excelModal();
//        }else{
//            gExportModal.options.params = mapKey;
//        }
////        alert("params==="+$('#exportButton').attr("params"));
        return param;
    }

    //    function initSelect() {
    //        var selects = $(".chosen-select");
    //        selects.each(function (i) {
    //            var id = $(this).attr("id");
    //            var value = $(this).attr("value");
    //            var placeholder = $(this).attr("data-placeholder");
    //            if($(this).attr("url") != null){
    //                $.ajax({
    //                    type: 'POST',
    //                    url: $(this).attr("url"),
    //                    dataType: 'json',
    //                    async: false,
    //                    success: function (data) {
    //                        if(data != null && data.length > 0){
    //                            if(placeholder != null){
    //                                $("#"+id).append("<option value=''>"+placeholder+"</option>");
    //                            }
    //                            var optionValue = $("#"+id).attr("optionValue") == null ? "id" : $("#"+id).attr("optionValue");
    //                            var optionText = $("#"+id).attr("optionText") == null ? "name" : $("#"+id).attr("optionText");
    //                            for(var j=0;j<data.length;j++){
    //                                $("#"+id).append("<option value='"+data[j][optionValue]+"'>"+data[j][optionText]+"</option>")
    //                            }
    //                            if(typeof(value) != "undefined"){
    //                                $("#"+id).val(value);
    //                            }
    //                            $("#"+id).trigger("chosen:updated");
    //                        }
    //
    //                    }
    //                });
    //            }
    //        })
    //    }

    function changeSelect(id,url,optionValueStr,optionTextStr) {
        var placeholder = $("#"+id).attr("data-placeholder");
        if(url != null){
            $.ajax({
                type: 'POST',
                url: url,
                dataType: 'json',
                async: false,
                success: function (data) {
                    $("#"+id).find("option").remove();
                    $("#"+id).append("<option value=''>"+placeholder+"</option>");
                    if(data != null && data.length > 0){
                        var optionValue = optionValueStr == null ? "id" : optionValueStr;
                        var optionText = optionTextStr == null ? "name" : optionTextStr;
                        for(var j=0;j<data.length;j++){
                            $("#"+id).append("<option value='"+data[j][optionValue]+"'>"+data[j][optionText]+"</option>")
                        }
                        if(typeof(value) != "undefined"){
                            $("#"+id).val(value);
                        }
                    }
                    $("#"+id).trigger("chosen:updated");

                }
            });
        }



        var selects = $(".chosen-select");
        selects.each(function (i) {
            var id = $(this).attr("id");
            var value = $(this).attr("value");
            var placeholder = $(this).attr("data-placeholder");
            if($(this).attr("url") != null){

            }
        })
    }

    function initCheckBox() {
        var checkBoxs = $(".checkBox");
        checkBoxs.each(function (i) {
            var id = $(this).attr("id");
            var name = $(this).attr("check_box_name");
            var value = $(this).attr("value");
            if($(this).attr("url") != null){
                $.ajax({
                    type: 'POST',
                    url: $(this).attr("url"),
                    dataType: 'json',
                    async: false,
                    success: function (data) {
                        if(data != null && data.length > 0){
                            var optionValue = $("#"+id).attr("optionValue") == null ? "id" : $("#"+id).attr("optionValue");
                            var optionText = $("#"+id).attr("optionText") == null ? "name" : $("#"+id).attr("optionText");
                            for(var j=0;j<data.length;j++){
                                if(isChecked(value,data[j][optionValue])){
                                    $("#"+id).append('<label class="checkbox-inline"> <input checked="checked" type="checkbox" value="'+data[j][optionValue]+'" name="'+name+'">'+ data[j][optionText] + '</label>');
                                }else{
                                    $("#"+id).append('<label class="checkbox-inline"> <input type="checkbox" value="'+data[j][optionValue]+'" name="'+name+'">'+ data[j][optionText] + '</label>');
                                }
                            }
                        }
                    }
                });
            }
        })
    }

    function isChecked(allValue,value) {
        var result = false;
        if(typeof(allValue) != "undefined" && value != null){
            var values = allValue.split(",");
            for(var i= 0;i<values.length;i++){
                if(value == values[i]){
                    result = true;
                    return result;
                }
            }
        }
        return result;
    }



    function clearCondition() {
        var paramCon = $(".form-inline [query='query']");
        for(var i = 0;i<g_complete.length;i++){
            g_complete[i].clearOptions();
        }
        paramCon.each(function (i) {
            if ($(this).val() != "") {
                $(this).val("");
            }
        })
        var selects = $(".chosen-select");
        selects.each(function (i){
            $(this).trigger("chosen:updated");
        })
    }

    function initKeyUp() {
        $("input,textarea").each(function (i) {
            $(this).keyup(function () {
                if($(this).val()!= ""){
                    $(this).parents(".form-group").removeClass("has-error");
                }
            })
        })
        $("select,input").each(function (i) {
            $(this).change(function () {
                if($(this).val()!= ""){
                    $(this).parents(".form-group").removeClass("has-error");
                }
            })
        })
    }
    /**
     *
     * @param id 自动补全的下拉控件Id
     * @param url 请求的地址
     * @param param 请求的参数名
     * @param optionValue 返回json的属性 如：id
     * @param optionText 返回json的属性 如：name
     * @param maxItems null:多选 1:单选
     * 如果要设初始值,请在下拉控件上加上data-[optionValue]和data-[optionText]的值如: <input data-id="2" data-name="达达">
     */
    var g_complete = [];
    function autocomplete(id,url,param,optionValue,optionText,maxItems){
        var defaultItem = [],
            defaultValue = [],
            defaultText = [];
        $('#'+id).attr("data-"+optionValue)?defaultValue = $('#'+id).attr("data-"+optionValue).split(","):"";
        $('#'+id).attr("data-"+optionText)?defaultText = $('#'+id).attr("data-"+optionText).split(","):"";
        if(defaultValue.length && defaultValue.length == defaultText.length){
            for(var i = 0;i <defaultValue.length;i++){
                var insertItem = {};
                insertItem[optionValue] = defaultValue[i];
                insertItem[optionText] = defaultText[i];
                defaultItem.push(insertItem);
            }
        }
        $('#'+id).val("");
        $('#'+id).selectize()[0].selectize.destroy();
        var slectItem = $('#'+id).selectize({
            valueField: optionValue,
            labelField: optionText,
            searchField: optionText,
            options: defaultItem,
            delimiter: ',',
            maxItems: maxItems,
            create: false,
            options: defaultItem,
            load: function(query, callback) {
                if (!query.length) {return callback();}
                var connectChar = "?";
                if(url.indexOf("?")>=0){
                    connectChar = "&";
                }
                $.ajax({
                    url: url+connectChar+param+"="+encodeURIComponent(query),
                    type: 'GET',
                    dataType: 'json',
                    error: function() {
                        callback();
                    },
                    success: function(res) {
                        callback(res);
                    }
                });
            }
        });
        if(defaultItem != null && defaultItem!= ''){
//            slectItem[0].selectize.addOption(defaultItem);
            slectItem[0].selectize.setValue(defaultValue);
        }

        g_complete.push(slectItem[0].selectize);
        return slectItem[0].selectize;
    }


    // Ajax 文件下载
    jQuery.download = function(url, data, method){    // 获得url和data
        if( url && data ){
            // data 是 string 或者 array/object
            data = typeof data == 'string' ? data : jQuery.param(data);        // 把参数组装成 form的  input
            var inputs = '';
            data = decodeURIComponent(data.replace(/\+/g, '%20'));
            jQuery.each(data.split('&'), function(){
                var pair = this.split('=');
                inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />';
            });        // request发送请求
            jQuery('<form action="'+ url +'" method="'+ (method||'post') +'">'+inputs+'</form>')
                .appendTo('body').submit().remove();
        };
    };


    function importExcelByMainAction(mainAction) {
        var modelFileName = 'downExcelModel';
        modalWindow("/excelImport/page?mainAction="+mainAction+"&modelFileName="+modelFileName, "导入", 200, 500);
    }

    function importExcelByMainAction_withModelFileName(mainAction, modelFileName) {
        modalWindow("/excelImport/page?mainAction="+mainAction+"&modelFileName="+modelFileName, "导入", 200, 500);
    }


</script>
</html>
