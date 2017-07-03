package cn.whaley.datawarehouse.illidan.server.domain;

import java.io.Serializable;

/**
 * 创建人：郭浩
 * 创建时间：2017/6/26
 * 程序作用：提交文件key,value。类型和路径
 * 数据输入：
 * 数据输出：
 */
public class UploadFileItem implements Serializable {
    private static final long serialVersionUID = -731311554021256395L;
    // The form field name in a form used foruploading a file,
    // such as "upload1" in "<inputtype="file" name="upload1"/>"
    private String formFieldName;
    // File name to be uploaded, thefileName contains path,
    // such as "E:\\some_file.jpg"
    private String fileName;

    public UploadFileItem(String formFieldName, String fileName) {
        this.formFieldName = formFieldName;
        this.fileName = fileName;
    }

    public String getFormFieldName() {
        return formFieldName;
    }

    public void setFormFieldName(String formFieldName) {
        this.formFieldName = formFieldName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileNameWithoutPath() {
        int index = fileName.lastIndexOf("/");
        if (index < 0) {
            return fileName;
        } else {
            return fileName.substring(index);
        }
    }
}
