var winBody = $("body");
/**
 * 弹出iframe窗口
 * @param {String} winTitle 窗口标题
 * @param {Int} winHeight 内容高度
 * @param {Int} winWidth 窗口宽度
 * @param {String} frameUrl iframe地址
 */
function modalWindow(frameUrl, winTitle, winHeight, winWidth, forceWidth) {
    if(ispc()==false){
        winWidth = null;
    }
    if(forceWidth){
        winWidth=null;
    }


    if ($(".modal-window").length > 0) {
        $(".modal-backdrop").remove();
        $(".modal-window").remove();
    }
    var modalIframe = $("<iframe>");
    modalIframe.attr("src", frameUrl);
    modalIframe.css({
        border: 0,
        overflow: "auto",
        width: "100%"
    });
    var insertHtml = '<div class="modal modal-window fade" tabindex="-1" role="dialog" aria-labelledby="windowModalLabel" data-backdrop="static" aria-hidden="true">' +
        '<div class="modal-dialog window-modal-dialog">' +
        '<div class="modal-content">' +
        '<div class="modal-header">' +
        '<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' +
        '<h4 class="modal-title" id="windowModalLabel">' + winTitle + '</h4>' +
        '</div><div class="modal-body window-modal-body" style="padding:10px 0;overflow: auto;"></div>' +
        '</div>' +
        '</div>' +
        '</div>';
    $("body").append(insertHtml);
    if (winWidth) {
        $(".window-modal-dialog").width(winWidth);
    }
    if (winHeight) {
        $(".window-modal-body").height(winHeight);
    }
    $(".window-modal-body").append(modalIframe);
        modalIframe.on("load",function(){
            modalIframe.css({
                width: "100%",
                minHeight: winHeight-10
            })
        });
    return $(".modal-window").modal();
}

// 关闭弹出窗
function closeWindow() {
    $(".modal-window").modal("hide");
}
/**
 * 弹出alert窗口
 * @param {String} winTitle alert标题
 * @param {String} winContent alert内容
 * @param {function} callBack 回调函数
 * @param {String} alertType 提示类型（“ok”：成功提示，"error": 错误提示，“warning”：警告提示）
 */
function modalAlert(winTitle, winContent, callBack, alertType) {
    if(window.parent.document.body){
        winBody = $(window.parent.document.body)
    }
    if (winBody.find(".modal-alert").length > 0) {
        winBody.find(".modal-backdrop").remove();
        winBody.find(".modal-alert").remove();
    }
    var alertParm = {
        icon: "",
        style: "",
        btn: "btn-default",
        backdrop: "static"
    };
    if(winBody.find(".modal-window").length > 0){
        // winBody.find(".modal-window").addClass("modal-window-hide");
        alertParm.backdrop = "false";
    }
    switch (alertType) {
        case "ok":
            alertParm.icon = '<span class="glyphicon glyphicon-ok"></span>';
            alertParm.style = 'text-success';
            alertParm.btn = 'btn-success';
            break;
        case "error":
            alertParm.icon = '<span class="glyphicon glyphicon-remove"></span>';
            alertParm.style = 'text-danger';
            alertParm.btn = 'btn-danger';
            break;
        case "warning":
            alertParm.icon = '<span class="glyphicon glyphicon-info"></span>';
            alertParm.style = 'text-warning';
            alertParm.btn = 'btn-warning';
            break;
        default:
            alertParm.icon = '';
            alertParm.style = '';
            alertParm.btn = 'btn-warning';
            break;
    }
    var insertHtml = '<div class="modal modal-alert fade" tabindex="-1" role="dialog" aria-labelledby="alertModalLabel" data-keyboard="false" data-backdrop="'+alertParm.backdrop+'" aria-hidden="true">' +
        '<div class="modal-dialog">' +
        '<div class="modal-content">' +
        '<div class="modal-header">' +
        '<h4 class="modal-title ' + alertParm.style + '" id="alertModalLabel">' + alertParm.icon + winTitle + '</h4>' +
        '</div>' +
        '<div class="modal-body ' + alertParm.style + '">' + winContent + '</div>' +
        '<div class="modal-footer">' +
        '<button type="button" class="btn ' + alertParm.btn + ' modal-alert-btn">确定</button>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>';
    winBody.append(insertHtml);
    winBody.find(".modal-alert-btn").unbind("click").bind("click", function(e) {
        winBody.find(".modal-alert").modal('hide');
        winBody.removeClass("modal-open").css({"paddingRight":0});
        $(window.document).find(".modal-open").removeClass("modal-open").css({"paddingRight":0});
        // window.frames[0].document.removeClass("modal-open").css({"paddingRight":0});
        winBody.find(".modal-alert").remove();
        winBody.find(".modal-backdrop").remove();
        if (typeof(callBack) == 'function') {
            callBack();
        }
    });
    winBody.find(".modal-alert").modal();
}

/**
 * 弹出Confirm窗口
 * @param {String} winTitle alert标题
 * @param {String} winContent alert内容
 * @param {function} callBack 确定回调函数
 * @param {function} cancelCallback 取消回调函数
 */
function modalConfirm(winTitle, winContent, okCallback, cancelCallback) {
    if(window.parent.document.body){
        winBody = $(window.parent.document.body);
        winObj = window.parent;
    }
    if (winBody.find(".modal-confirm").length > 0) {
        winBody.find(".modal-backdrop").remove();
        winBody.find(".modal-confirm").remove();
    }
    var backdrop="static";
    if(winBody.find(".modal-window").length > 0){
        // winBody.find(".modal-window").addClass("modal-window-hide");
        backdrop = "false";
    }
    var insertHtml = '<div class="modal modal-confirm fade" tabindex="-1" role="dialog" aria-labelledby="alertModalLabel" data-keyboard="false" data-backdrop="'+backdrop+'" aria-hidden="true">' +
        '<div class="modal-dialog">' +
        '<div class="modal-content">' +
        '<div class="modal-header">' +
        '<button type="button" class="close modal-confirm-cancel" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' +
        '<h4 class="modal-title">' + winTitle + '</h4>' +
        '</div>' +
        '<div class="modal-body">' + winContent +
        '</div>' +
        '<div class="modal-footer">' +
        '<button type="button" class="btn btn-primary modal-confirm-ok">确定</button>' +
        '<button type="button" class="btn btn-default modal-confirm-cancel" data-dismiss="modal">取消</button>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>';
    winBody.append(insertHtml);
    winBody.find(".modal-confirm-ok").unbind("click").bind("click", function(e) {
        winBody.find(".modal-confirm").modal('hide');
        winBody.removeClass("modal-open").css({"paddingRight":0});
        winBody.find(".modal-confirm").remove();
        winBody.find(".modal-backdrop").remove();
        if (typeof(okCallback) == 'function') {
            okCallback();
        }
    });
    winBody.find(".modal-confirm-cancel").unbind("click").bind("click", function(e) {
        winBody.find(".modal-confirm").modal('hide');
        winBody.removeClass("modal-open").css({"paddingRight":0});
        winBody.find(".modal-confirm").remove();
        winBody.find(".modal-backdrop").remove();
        if (typeof(cancelCallback) == 'function') {
            cancelCallback();
        }
    });
    winBody.find(".modal-confirm").modal();
}
function ispc() {
    var userAgentInfo = navigator.userAgent;
    var Agents = ["Android", "iPhone",
        "SymbianOS", "Windows Phone",
        "iPad", "iPod"];
    var flag = true;
    for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) {
            flag = false;
            break;
        }
    }
    return flag;
}