package cn.whaley.datawarehouse.illidan.server.domain;

import java.io.Serializable;

/**
 * 创建人：郭浩
 * 创建时间：2017/6/26
 * 程序作用：form 表单提交，key,value
 * 数据输入：
 * 数据输出：
 */
public class FormFieldKeyValuePair implements Serializable {
    private static final long serialVersionUID = 7591563589332220509L;
    // The form field used for receivinguser's input,
    //such as "username" in "<inputtype="text" name="username"/>"
    private String key;
    private String value;

    public FormFieldKeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
