package cn.whaley.datawarehouse.illidan.server.response;

/**
 * Created by luoziyu on 2017/9/25.
 */
public enum ResponseCode {

    SUCCESS(200, "SUCCESS"),
    ERROR(400, "ERROR"),
    NEED_LOGIN(401, "NEED_LOGIN"),
    NO_AUTHORITY(403, "NO_AUTHORITY"),
    ILLEGAL_ARGUMENT(404, "ILLEGAL_ARGUMENT");

    private int code;
    private String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
