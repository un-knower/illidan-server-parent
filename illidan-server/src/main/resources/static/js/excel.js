/**
 * 导出excel
 * @date    2016-09-22 13:55:25
 * @version 1.00
 */


;(function ($, window, document, undefined) {
    var excelModal = function (el, opt) {
        this.$element = el;
        this.defaults = {

            // 请求页数ajax地址
            pageAjaxUrl: "http://" + window.location.host + "/excel/querypage",

            // 下载链接
            downloadUrl: "http://" + window.location.host + "/excel/export",

            // 导出配置
            params: "",

            // keyword
            keyword: 0,

            // 关闭模态框按钮Class
            cancelBtn: ".excelModal-close",

            // 点击页面其他部分是否关闭
            backclose: false,

            // 标题
            exceltitle: "导出excel",

            // 弹出模态框回调
            modalShowCallback: function () {
            },

            // 请求页数成功回调
            pageAjaxCallback: function (isSuccess) {
                if (!isSuccess) {
                    inserHtml = "<div class='excelModal-tips excelModal-error'>查询出错了，请稍后再试！</div>";
                    $(".excelModal-body").html(inserHtml);
                }
            },

            // 关闭模态框回调
            modalCloseCallback: function () {
            },
        }

        this.$modal = $("<div>").addClass('excelModal');

        if (opt) {
            this.options = $.extend({}, this.defaults, opt);
        } else {
            var domOpt = {};
            if (!this.$element.attr("keyword")) {
                console.log("缺少keyword");
            }
            if (!this.$element.attr("params")) {
                console.log("缺少params");
            }
            for (prop in this.defaults) {
                if (this.defaults.hasOwnProperty(prop)) {
                    domOpt[prop] = this.$element.attr(prop) ? this.$element.attr(prop) : this.defaults[prop];
                }
            }

            this.options = $.extend({}, this.defaults, domOpt);
        }
        var excelPlug = this;
        excelPlug.$element.on('click', function (event) {
            event.preventDefault();
            excelPlug.openModal();
            excelPlug.options.modalShowCallback();
        });
        $("body").on('click', excelPlug.options.cancelBtn, function (event) {
            excelPlug.closeModal();
            event.preventDefault();
        })
        if (excelPlug.options.backclose) {
            $("body").on('click', '.excelModal-backdrop', function (event) {
                event.preventDefault();
                excelPlug.closeModal();
            });
        }
        ;
    }
    excelModal.prototype = {
        // 展示模态框
        openModal: function () {
            $("body").append(this.$modal);
            this.$modal.show();
            this.$modal.html('<div class="excelModal-backdrop"></div><div class="excelModal-dialog"><div class="excelModal-content">' +
                '<div class="excelModal-header">' +
                '<span type="button" class="excelModal-close"><span aria-hidden="true">×</span></span>' +
                '<h4 class="excelModal-title" id="myLargeModalLabel">' + this.options.exceltitle + '</h4>' +
                '</div>' +
                '<div class="excelModal-body">' +
                '</div>' +
                '</div>' +
                '</div>');
            $("body").addClass('excelModal-open');
            this.options.modalShowCallback();
            this.getPageNum();
        },
        // 关闭模态框
        closeModal: function () {
            $("body").removeClass('excelModal-open');
            $(".excelModal").remove();
            this.options.modalCloseCallback();
        },
        // 请求页数
        getPageNum: function () {
            var me = this;
            var urlOpt = "params=" + me.options.params + "&keyword=" + me.options.keyword;
            var pageAjaxUrl = this.options.pageAjaxUrl;
            if (this.options.pageAjaxUrl.indexOf("?") > -1) {
                pageAjaxUrl = pageAjaxUrl + "&" + urlOpt
            } else {
                pageAjaxUrl = pageAjaxUrl + "?" + urlOpt
            }
            $.ajax({
                url: pageAjaxUrl,
                dataType: 'json',
            })
                .done(function (responseData) {
                    if (responseData.success) {
                        // 渲染页数
                        me.renderPage(responseData.pageInfo);
                        // 成功回调
                        me.options.pageAjaxCallback(true);
                    } else {
                        // 错误回调
                        me.options.pageAjaxCallback(false);
                    }
                })
                .fail(function () {
                    // 错误回调
                    me.options.pageAjaxCallback(false);
                });

        },
        renderPage: function (pageInfo) {
            var me = this;
            var inserHtml = "";
            var urlOpt = "params=" + me.options.params + "&keyword=" + me.options.keyword;
            var downloadUrl = this.options.downloadUrl;
            if (this.options.downloadUrl.indexOf("?") > -1) {
                downloadUrl = downloadUrl + "&" + urlOpt
            } else {
                downloadUrl = downloadUrl + "?" + urlOpt
            }
            if (parseInt(pageInfo.pageCount) <= 0) {
                inserHtml = "<div class='excelModal-tips excelModal-error'>共查找到0条数据！</div>"
            } else {
                inserHtml = "<div class='excelModal-tips'>共" + pageInfo.pageCount + "页，每页" + pageInfo.pageSize + "条,共" + pageInfo.count + "条记录</div>"
                for (var i = 1; i <= pageInfo.pageCount; i++) {
                    inserHtml += '<a class="excelModal-btn" target="_blank" href="' + downloadUrl + '&pageno=' + i + '">页数' + i + '</a>'
                }
            }
            $(".excelModal-body").html(inserHtml);
        }
    }

    $.fn.excelModal = function (options) {
        //创建haorooms的实体
        var excelPlug = new excelModal(this, options);
        return excelPlug;
    }
    $(function(){
        $("[role=excelDialog]").excelModal();
    });
})(jQuery, window, document);




/**
 * 导入excel
 * @date    2016-09-22 13:55:25
 * @version 1.00
 */


;
(function($, window, document, undefined) {
    var excelUpload = function(el, opt) {
        this.$element = el;
        this.defaults = {

            loadFileTypes: /(.xls|.xlsx)$/,

            // 上传地址
            uploadUrl: "http://" + window.location.host + "/excel/upload",

            // 下载链接
            downloadUrl: "http://" + window.location.host + "/excel/download",

            // 模板名称
            keyword: "",

            // 关闭模态框按钮Class
            cancelBtn: ".excelUpload-close",

            // 点击页面其他部分是否关闭
            backclose: false,

            // 标题
            exceltitle: "导入excel",

            // 导入错误提示模板
            errorTip: "ID为{$key}{$value}",


            // 弹出模态框回调
            modalShowCallback: function() {},

            // 请求页数成功回调
            downloadCallback: function(isSuccess) {
                if (!isSuccess) {
                    inserHtml = "<div class='excelUpload-tips excelUpload-error'>查询出错了，请稍后再试！</div>";
                    $(".excelUpload-body").html(inserHtml);
                }
            },

            // 关闭模态框回调
            modalCloseCallback: function() {},
        }

        this.$modal = $("<div>").addClass('excelUpload');

        this.$fileupload = $("<input type='file' name='file'>").addClass('excelUpload-file');
        this.$fileuploadbtn = $('<a href="javascript:void(0);" class="excelUpload-btn excelUpload-btn-upload">浏览...</a>');
        this.timeOut = false;
        if (opt) {
            this.options = $.extend({}, this.defaults, opt);
        } else {
            var domOpt = {};
            if (!this.$element.attr("keyword")) {
                console.log("缺少keyword");
            }
            for (prop in this.defaults) {
                if (this.defaults.hasOwnProperty(prop)) {
                    domOpt[prop] = this.$element.attr(prop) ? this.$element.attr(prop) : this.defaults[prop];
                }
            }

            this.options = $.extend({}, this.defaults, domOpt);
        }
        var excelPlug = this;
        excelPlug.$element.on('click', function(event) {
            event.preventDefault();
            excelPlug.openModal();
            excelPlug.options.modalShowCallback();
        });
        this.$fileupload.on('change', function(event) {
            event.preventDefault();
            var $input = $(this);
            $input.prev('input').val($input.val());
        });
        this.$fileuploadbtn.on('click', function(event) {
            event.preventDefault();
            excelPlug.$fileupload.click();
        });
        $("body").on('click', excelPlug.options.cancelBtn, function(event) {
            excelPlug.closeModal();
            event.preventDefault();
        })
        if (excelPlug.options.backclose) {
            $("body").on('click', '.excelUpload-backdrop', function(event) {
                event.preventDefault();
                excelPlug.closeModal();
            });
        };
    }
    excelUpload.prototype = {
        // 展示模态框
        openModal: function() {
            $("body").append(this.$modal);
            this.$modal.show();
            this.$modal.html('<div class="excelUpload-backdrop"></div><div class="excelUpload-dialog"><div class="excelUpload-content">' +
                '<div class="excelUpload-header">' +
                '<span type="button" class="excelUpload-close"><span aria-hidden="true">×</span></span>' +
                '<h4 class="excelUpload-title" id="myLargeModalLabel">' + this.options.exceltitle + '</h4>' +
                '</div>' +
                '<div class="excelUpload-body">' +
                '<div class="excelUpload-upload"><input type="text" class="excelUpload-input"><a href="'+this.options.downloadUrl+'?keyword='+this.options.keyword+'" target="_blank" class="excelUpload-link excelUpload-link-download">excel模板下载</a>' +
                '</div>' +
                '</div><div class="excelUpload-footer"></div>' +
                '</div>' +
                '</div>');
            $(".excelUpload-upload input").after(this.$fileuploadbtn).after(this.$fileupload);
            $("body").addClass('excelUpload-open');
            this.options.modalShowCallback();
            var excelPlug = this;
            this.$fileupload.fileupload({
                url: this.options.uploadUrl,
                dataType: 'json',
                formData: {keyword: this.options.keyword},
                add: function(e, data) {
                    if(excelPlug.options.loadFileTypes){
                        for (var i = 0; i < data.files.length; i++) {
                            if (!excelPlug.options.loadFileTypes.test(data.files[i].name)) {
                                $(".excelUploadError").remove();
                                $(".excelUpload-body").append("<p class='excelUploadError'>文件  "+data.files[i].name+"  格式错误，请选择.xls或.xlsx文件！</p>");
                                $(".excelUpload-footer").html("");
                                return false;
                            }
                        }
                    }
                    $(".excelUploadError").remove();
                    data.context = $('<a href="javascript:void(0);" class="excelUpload-btn excelUpload-btn-submit">导入模板</a>');
                    $(".excelUpload-footer").html(data.context);
                    data.context.unbind('click').bind('click', function(event) {
                        if (data.context.hasClass('excel-uploading')) {
                            return;
                        }else{
                            data.context.addClass('excel-uploading');
                            data.context.text('导入中...');
                            data.submit();
                        }
                    });
                },
                fail: function(e, data){
                    showFail(e,data,excelPlug)
                },
                done: function(e, data) {
                    var result = data.result;
                    if (!result.success) {
                        showFail(e,data,excelPlug);
                    };
                    if (result.success&&!result.errors) {
                        showSuccess(e,data,excelPlug);
                    }else if(result.success&&result.errors){
                        showFail(e,data,excelPlug,result.errors);
                    }
                },
                progress: function(e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    console.log(progress);
                }
            });
            var showFail = function(e,data,excelPlug,errors){
                data.context.text('导入失败');
                $(".excelUploadError").remove();
                if (errors) {
                    var errorTips = "";
                    for (prop in errors) {
                        if (errors.hasOwnProperty(prop)) {
                            var errorTmp = excelPlug.options.errorTip;
                            var error = errorTmp.replace("{$key}",prop).replace("{$value}",errors[prop])+"<br>";
                            errorTips+=error;
                        }
                    }
                    $(".excelUpload-body").append("<p class='excelUploadError'>"+errorTips+"</p>");
                }else{
                    $(".excelUpload-body").append("<p class='excelUploadError'>导入失败，请稍后重试！</p>");
                }
                if (excelPlug.timeOut) {
                    window.clearTimeout(excelPlug.timeOut);
                }
                var me = data;
                excelPlug.timeOut = window.setTimeout(function(){
                    me.context.removeClass('excel-uploading');
                    me.context.text('重新导入');
                }, 2000);
            };
            var showSuccess = function(e,data,excelPlug){
                data.context.text('导入成功');
                $(".excelUploadError").remove();
                $(".excelUpload-body").append("<p class='excelUploadError excelUploadSuccess'>导入成功！</p>");
                if (excelPlug.timeOut) {
                    window.clearTimeout(excelPlug.timeOut);
                }
                var me = data;
                excelPlug.timeOut = window.setTimeout(function(){
                    me.context.removeClass('excel-uploading');
                    me.context.text('导入模板');
                }, 2000);
            };
        },
        // 关闭模态框
        closeModal: function() {
            $("body").removeClass('excelUpload-open');
            $(".excelUpload").remove();
            this.options.modalCloseCallback();
        },
        renderPage: function(pageInfo) {

            $(".excelUpload-body").html(inserHtml);
        }

    }

    $.fn.excelUpload = function(options) {
        //创建haorooms的实体
        var excelPlug = new excelUpload(this, options);
        return excelPlug;
    }
    $(function(){
        $("[role=excelUpload]").excelUpload();
    });
})(jQuery, window, document);
