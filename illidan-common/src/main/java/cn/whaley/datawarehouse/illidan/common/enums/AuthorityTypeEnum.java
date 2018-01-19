package cn.whaley.datawarehouse.illidan.common.enums;

/**
 * Created by lituo on 2018/1/19.
 */
public enum  AuthorityTypeEnum {
    PROJECT(1, "工程"),
    TABLE(2, "数据表"),
    DATABASE(3, "数据库");

    AuthorityTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private int code;
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
