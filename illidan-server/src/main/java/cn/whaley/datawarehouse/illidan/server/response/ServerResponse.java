package cn.whaley.datawarehouse.illidan.server.response;


import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by luoziyu on 2017/12/21.
 * 通用请求响应结构体
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ServerResponse<T> {
    private int status;
    private String msg;
    private T data;
    private Long iTotalRecords;

    public static <T> ServerResponse<T> responseBySuccessMessage(String msg) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> responseBySuccess() {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> responseBySuccess(T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> responseBySuccessDataAndCount(T data, Long count) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data, count);
    }

    public static <T> ServerResponse<T> responseBySuccess(String msg, T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> responseByError() {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }

    public static <T> ServerResponse<T> responseByError(String errorMsg) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMsg);
    }

    public static <T> ServerResponse<T> responseByError(int errorCode, String errorMsg) {
        return new ServerResponse<T>(errorCode, errorMsg);
    }

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, T data, Long count) {
        this.status = status;
        this.data = data;
        this.iTotalRecords = count;
    }

    public ServerResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(Long iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }
}
