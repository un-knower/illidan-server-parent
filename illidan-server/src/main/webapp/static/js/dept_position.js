/*
部门 岗位 公用JS
 */

function toAddDept() {
    modalWindow("/dept/toAdd","新增部门",450,400);
}

function toEdit() {
    var selected = $('#tree').treeview('getSelected');
    if(selected.length==0){
        modalAlert("提示","没有选择的部门或岗位!",null,"error");
    }else{
        var thisNode = selected[0];
        if(thisNode.type=="D"){
            modalWindow("/dept/toEdit?id="+thisNode.id,"编辑部门",450,400);
        }else{
            modalWindow("/position/toEdit?id="+thisNode.id,"编辑岗位",500,400);
        }
    }
}

function cancle() {
    return false;
}
function toDel() {
    var selected = $('#tree').treeview('getSelected');
    if(selected.length==0){
        modalAlert("提示","没有选择的部门或岗位!",null,"error");
    }else{
        var thisNode = selected[0];
        if(thisNode.type=="D"){
            modalConfirm("提示","你确定要删除部门["+thisNode.text+"]吗?",function(){doDel(thisNode.id,"D")},cancle);
        }else{
            modalConfirm("提示","你确定要删除岗位["+thisNode.text+"]吗?",function(){doDel(thisNode.id,"P")},cancle);
        }
    }
}
function doDel(id,type) {
    if(type=="D"){
        $.ajax({
            type: 'POST',
            url: '/dept/delete',
            data: "id="+id,
            dataType: 'json',
            async: false,
            success: function (data) {
                if (data.result == true) {
                    modalAlert("提示",data.msg,getTree,"ok");
                } else {
                    modalAlert("提示",data.msg,getTree,"error");
                }
            }
        });
    }else{
        $.ajax({
            type: 'POST',
            url: '/position/delete',
            data: "id="+id,
            dataType: 'json',
            async: false,
            success: function (data) {
                if (data.result == true) {
                    modalAlert("提示",data.msg,getTree,"ok");
                } else {
                    modalAlert("提示",data.msg,getTree,"error");
                }
            }
        });
    }
}

function toAddPosition(){
    modalWindow("/position/toAdd","新增岗位",450,400);
}

function toSetRole() {
    var selected = $('#tree').treeview('getSelected');
    if(selected.length==0){
        modalAlert("提示","没有选择的岗位!",null,"error");
    }else{
        var thisNode = selected[0];
        if(thisNode.type=="D"){
            modalAlert("提示","请选择一个岗位!",null,"error");
        }else{
            doSetRole(thisNode.id);
        }
    }
}

function doSetRole(id) {
    modalWindow("/role/toSet?id="+id,"设置岗位权限",600,1200);
}

function toSetDash() {
    var selected = $('#tree').treeview('getSelected');
    if(selected.length==0){
        modalAlert("提示","没有选择的岗位!",null,"error");
    }else{
        var thisNode = selected[0];
        if(thisNode.type=="D"){
            modalAlert("提示","请选择一个岗位!",null,"error");
        }else{
            doSetDash(thisNode.id);
        }
    }
}

function doSetDash(id) {
    modalWindow("/dashConf/toSet?id="+id,"设置岗位仪表盘",600,600);
}