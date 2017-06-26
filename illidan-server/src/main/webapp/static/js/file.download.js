
function initFileList(selector, menu, referId, type, deleteCallback){
    $.ajax({
        type: 'POST',
        url: '/file/list',
        data: {
            referMenu:menu,
            referId:referId,
            referType:type,
        },
        async: false,
        success: function (response) {
            if (!response.result) {
                modalAlert("提示", response.msg, "", "error");
                return;
            }
            displayFileList(selector, response.data, deleteCallback);
        }
    });
}

function displayFileList(selector, dataAry, deleteCallback){
    var divObj = $(selector);
    divObj.empty();
    if(!dataAry || dataAry.length <= 0){
        return;
    }
    var olObj = $('<ol></ol>');
    $(dataAry).each(function(i, data){
        var downloadObj = $('<a></a>').attr('target', '_blank').attr('href', data.url + '?name='+data.name)
            .attr('title', 'author:'+data.creator).text(data.name);
        var liObj = $('<li></li>').append(downloadObj);
        if(deleteCallback){
            liObj.append('&nbsp;&nbsp;').append($('<a></a>').addClass('glyphicon glyphicon-remove')
                .attr('href', 'javascript:;').attr('onclick', 'deleteFileById("' + data.id + '", '+deleteCallback+');'));
        }
        olObj.append(liObj);

    });
    divObj.append(olObj);
}

function deleteFileById(id, callback){
    $.ajax({
        type: 'POST',
        url: '/file/delete',
        data: {id:id},
        async: false,
        success: function (response) {
            if (!response.result) {
                modalAlert("删除文件",response.msg, null,"error");
                return;
            }
            if(callback){
                callback();
            }
            modalAlert("删除文件",'删除成功',null,"ok");
        }
    });
}
