<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@include file="../taglibs.jsp"%>
<%
    String path = request.getContextPath();
%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta charset="utf-8"/>
    <title>输出表列表</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>
<div class="container">
    <ul class="pager" style="margin-top: 10px;margin-bottom: 0px;">
        <li class="previous" ><a href="javascript:history.go(-1)" style="margin-top: 5px;"><span aria-hidden="true">&larr;</span> 返回</a></li>
        <li class="previous" style="float: left;margin-left: 10px;"><h4>输出表列表</h4></li>

    </ul>
    <div class="page-header objhid">
        <div class="form-inline ">
            <div class="form-group">
                <input class="form-control" id="tableCode" name="tableCode" query="query" placeholder="hive表名称">
            </div>
            <div class="form-group">
                <input class="form-control" id="tableDes" name="tableDes" query="query" placeholder="hive表描述">
            </div>
            <div class="text-center search-btns">
                <button class="btn btn-info" onclick="searchList();">查询</button>
                <button class="btn btn-default" onclick="clearCondition();">重置</button>
            </div>
        </div>
    </div>
    <button type="button" class="btn btn-success" onclick="add()">新增</button>
    <%--<button type="button" class="btn btn-danger" onclick="remove();">删除</button>--%>
    <button type="button" class="btn btn-primary" onclick="parseSql();">解析建表语句</button>
    <table id="dynamic-table" name="dynamic-table" class="table table-striped table-hover table-bordered">
        <thead>
        <tr>
            <th class="center"><label class="pos-rel">
                <input type="checkbox" class="ace"/>
                <span class="lbl"></span> </label>
            </th>
            <th >操作</th>
            <th >hive表名称</th>
            <%--<th >mysql表名称</th>--%>
            <th >hive表描述</th>
            <th >创建时间</th>
            <th >更新时间</th>

        </tr>
        </thead>
    </table>
</div>

<!-- inline scripts related to this page -->
<script type="text/javascript">
    $('#createTimeBegin,#createTimeEnd').datetimepicker({
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
        extraFormats: ['YYYY-MM-DD HH:mm:ss', 'YYYY-MM-DD HH:mm:ss'],

    }).next().on(ace.click_event, function () {
        $(this).prev().focus();
    });
    var myTable = null;
    jQuery(function ($) {
        myTable =
            $('#dynamic-table').DataTable({
                "sDom": "t<'row table-bottom-tool'<'col-sm-6'l><'col-sm-6'p>r>",
                "oLanguage": {
                    "sLengthMenu": "每页显示 _MENU_ 条",
                    "oPaginate": {
                        "sFirst": "首页",
                        "sLast": "末页",
                        "sNext": "下一页",
                        "sPrevious": "上一页"
                    }
                },
                bAutoWidth: false,
                processing: false,
                bFilter: false,
                bInfo: false,
                ajax: {
                    url: '<%=path%>/table/tableList',
                    type: 'POST',
                    dataType: 'json',
                    data: getParam()
                },
                aoColumns: [
                    {
                        data: function (row) {
                            return '<label class="pos-rel"><input class="ace" type="checkbox" value="' + row.id + '"><span class="lbl"></span></label>'
                        }
                    },
                    {
                        data: function (row) {
                            return "<a href='javascript:void(0);' onclick='edit(" + row.id + ");'>编辑</a> <a href='javascript:void(0);' onclick='copyTable(" + row.id + ");'>复制</a>";
                        }
                    },
                    {data: "tableCode"},
                    {data: "tableDes"},
                    {
                        data: function (data) {
                            if (data.createTime != null) {
                                return formatDate(data.createTime);
                            } else {
                                return "";
                            }
                        }
                    },
                    {
                        data: function (data) {
                            if (data.updateTime != null) {
                                return formatDate(data.updateTime);
                            } else {
                                return "";
                            }
                        }
                    }

                ],
                "serverSide": true,
                "aLengthMenu": [10, 25, 50, 100],
                'bSort': false,
                fnPreDrawCallback: function (settings) {
                    if (settings.json) {
                        var count = settings.json.iTotalRecords;
                        settings._iRecordsTotal = count;
                        settings._iRecordsDisplay = count;
                    }
                }
            });

        var defaultColvisAction = myTable.button(0).action();
        myTable.button(0).action(function (e, dt, button, config) {
            defaultColvisAction(e, dt, button, config);
            if ($('.dt-button-collection > .dropdown-menu').length == 0) {
                $('.dt-button-collection')
                    .wrapInner('<ul class="dropdown-menu dropdown-light dropdown-caret dropdown-caret" />')
                    .find('a').attr('href', '#').wrap("<li />")
            }
            $('.dt-button-collection').appendTo('.tableTools-container .dt-buttons')
        });

        myTable.on('select', function (e, dt, type, index) {
            if (type === 'row') {
                $(myTable.row(index).node()).find('input:checkbox').prop('checked', true);
            }
        });
        myTable.on('deselect', function (e, dt, type, index) {
            if (type === 'row') {
                $(myTable.row(index).node()).find('input:checkbox').prop('checked', false);
            }
        });

        //table checkboxes
        $('th input[type=checkbox], td input[type=checkbox]').prop('checked', false);

        //select/deselect all rows according to table header checkbox
        $('#dynamic-table > thead > tr > th input[type=checkbox], #dynamic-table_wrapper input[type=checkbox]').eq(0).on('click', function () {
            var th_checked = this.checked;//checkbox inside "TH" table header

            $('#dynamic-table').find('tbody > tr').each(function () {
                var row = this;
                if (th_checked) myTable.row(row).select();
                else  myTable.row(row).deselect();
            });
        });

        //select/deselect a row when the checkbox is checked/unchecked
        $('#dynamic-table').on('click', 'td input[type=checkbox]', function () {
            var row = $(this).closest('tr').get(0);
            if (!this.checked) myTable.row(row).deselect();
            else myTable.row(row).select();
        });

        $(document).on('click', '#dynamic-table .dropdown-toggle', function (e) {
            e.stopImmediatePropagation();
            e.stopPropagation();
            e.preventDefault();
        });

    });

    function searchList() {
        if (myTable) {
            searchObj = getParam();
            myTable.page("first");
            myTable.ajax.params(searchObj);
            myTable.ajax.reload(null, false);
        }
    }

    function add() {
        <%--modalWindow("<%=path%>/table/toAdd", "新增输出表", 580, 1200);--%>
        if(getCookie('tableId')!='' && getCookie('tableId')!=null){
            modalWindow("<%=path%>/table/toAdd?id=" + getCookie('tableId'), "新增输出表", 580, 1200);
        }else{
            modalWindow("<%=path%>/table/toAdd?id=-1", "新增输出表", 580, 1200);
        }
    }

    function edit(id) {
        modalWindow("<%=path%>/table/toEdit?id=" + id, "编辑输出表", 580, 1200);
    }

    function copyTable(id){
        setCookie('tableId', id, 30);
        modalAlert("提示", "输出表复制成功", searchList, "ok");

    }

    function parseSql() {
        modalWindow("<%=path%>/table/toParseSql", "解析建表语句", 500, 800);
    }

</script>
</body>
</html>
