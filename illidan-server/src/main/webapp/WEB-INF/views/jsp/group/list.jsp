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
    <title>任务组列表</title>
    <meta name="description" content="overview &amp; stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <jsp:include page="../frame.jsp"/>
</head>
<body>
<div class="container">
    <ul class="pager" style="margin-top: 10px;margin-bottom: 0px;">
        <li class="previous"><a href="/project/list"><span aria-hidden="true">&larr;</span> 返回项目列表</a></li>
    </ul>
    <div class="page-header objhid">
        <div class="form-inline ">
            <%--<div class="form-group">--%>
                <%--<input class="form-control" type="hidden" id="projectId" name="projectId" query="query" value="${projectId}"/>--%>
            <%--</div>--%>
            <div class="form-group">
                <input class="form-control" id="groupCode" name="groupCode" query="query" placeholder="任务组code">
            </div>
            <div class="form-group">
                <input class="form-control" id="groupDes" name="groupDes" query="query" placeholder="任务组描述">
            </div>
            <div class="text-center search-btns">
                <button class="btn btn-info" onclick="searchList();">查询</button>
                <button class="btn btn-default" onclick="clearCondition();">重置</button>
            </div>
        </div>
    </div>
    <button type="button" class="btn btn-success" onclick="add('${projectId}')">新增</button>
    <button type="button" class="btn btn-danger" onclick="remove();">删除</button>
    <table id="dynamic-table" name="dynamic-table" class="table table-striped table-hover table-bordered">
        <thead>
        <tr>
            <th class="center"><label class="pos-rel">
                <input type="checkbox" class="ace"/>
                <span class="lbl"></span> </label>
            </th>
            <th >操作</th>
            <th >任务组code</th>
            <th >所属项目</th>
            <th >任务组描述</th>
            <th >调度策略</th>
            <th >任务失败邮件发送人</th>
            <th >调度ID</th>
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
                    url: '<%=path%>/group/groupList',
                    type: 'POST',
                    dataType: 'json',
                    data: getParamWithProjectId()
                },
                aoColumns: [
                    {
                        data: function (row) {
                            return '<label class="pos-rel"><input class="ace" type="checkbox" value="' + row.id + '"><span class="lbl"></span></label>'
                        }
                    },
                    {
                        data: function (row) {
                            return "<a href='javascript:void(0);' onclick='edit(" + row.id + ");'>编辑</a> <a href='javascript:void(0);' onclick='publish(" + row.projectId + ");'>发布</a>";
                        }
                    },
                    {data: "groupCode"},
                    {data: "projectCode"},
                    {data: "groupDes"},
                    {data: "schedule"},
                    {data: "email"},
                    {data: "scheduleId"},
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
                "aoColumnDefs": [
                    {
                        "render": function(data, type, row, meta) {
                            return '<a href="<%=path%>/task/list?groupId=' + row.id + '">' + row.groupCode + '</a>';
                        },
                        //指定是第三列
                        "targets": 2
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
            searchObj = getParamWithProjectId();
            myTable.page("first");
            myTable.ajax.params(searchObj);
            myTable.ajax.reload(null, false);
        }

    }

    function getParamWithProjectId() {
        paramWithProjectId = getParam();
        paramWithProjectId["projectId"] = '${projectId}'
        return paramWithProjectId
    }

    function add(projectId) {
        modalWindow("<%=path%>/group/toAdd?projectId=" + projectId, "新增任务组", 450, 350);
    }

    function edit(id) {
        modalWindow("<%=path%>/group/toEdit?id=" + id, "编辑任务组", 410, 350);
    }

    function publish(projectId) {
        modalConfirm("提示", "你确定要发布吗?", function () {
            publishProject(projectId)
        }, cancle);
    }

    function publishProject(projectId) {
        $.ajax({
            type: 'POST',
            url: '<%=path%>/project/toPublishProject',
            data: "id=" + projectId,
            dataType: 'json',
            async: false,
            success: function (data) {
                if (data.result == true) {
                    modalAlert("提示", data.msg, searchList, "ok");
                } else {
                    modalAlert("提示", data.msg, searchList, "error");
                }
            }
        });
    }

    function project_detail(id){
        modalWindow("<%=path%>/group/detail?id=" + id, "任务组详情", 450, 700);
    }

    function remove() {
        var rows = myTable.rows('.selected').data();
        if (rows.length > 0) {
            var ids = "";
            for (var i = 0; i < rows.length; i++) {
                var column = rows[i];
                if (i == rows.length - 1) {
                    ids += column.id;
                } else {
                    ids += column.id + ",";
                }
            }
            removeRecord(ids);
        } else {
            modalAlert("提示", "请选择要删除的记录", "", "error");
            return false;
        }
    }

    function removeRecord(ids) {
        modalConfirm("提示", "你确定要删除记录吗?", function () {
            deleteGroup(ids)
        }, cancle);
    }

    function cancle() {
        return false;
    }

    function deleteGroup(ids) {
        $.ajax({
            type: 'POST',
            url: '<%=path%>/group/delete',
            data: "ids=" + ids,
            dataType: 'json',
            async: false,
            success: function (data) {
                if (data.result == true) {
                    modalAlert("提示", data.msg, searchList, "ok");
                } else {
                    modalAlert("提示", data.msg, searchList, "error");
                }
            }
        });
    }
</script>
</body>
</html>
