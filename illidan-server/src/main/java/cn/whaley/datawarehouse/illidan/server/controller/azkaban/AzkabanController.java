package cn.whaley.datawarehouse.illidan.server.controller.azkaban;

import cn.whaley.datawarehouse.illidan.server.controller.project.ProjectController;
import cn.whaley.datawarehouse.illidan.server.domain.FormFieldKeyValuePair;
import cn.whaley.datawarehouse.illidan.server.domain.UploadFileItem;
import cn.whaley.datawarehouse.illidan.server.util.ConfigurationManager;
import cn.whaley.datawarehouse.illidan.server.util.HttpClientUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建人：郭浩
 * 创建时间：2017/6/26
 * 程序作用：
 * 数据输入：
 * 数据输出：
 */

public class AzkabanController{
    private Logger log = LoggerFactory.getLogger(ProjectController.class);
    /**
     * 模拟form 表单提交
     * @param sessionId
     * @param projectName
     * @return
     */
    public JSONObject uploadProject(String sessionId,String projectName,String loadFile){
        JSONObject result = new JSONObject();
        String url = ConfigurationManager.getProperty("managerUrl");
        String filePath = ConfigurationManager.getProperty("zipdir")+ File.separator+loadFile.trim();
        ArrayList<FormFieldKeyValuePair> ffkvp = new ArrayList<FormFieldKeyValuePair>();
        ffkvp.add(new FormFieldKeyValuePair("session.id", sessionId));//其他参数
        ffkvp.add(new FormFieldKeyValuePair("ajax", "upload"));
        ffkvp.add(new FormFieldKeyValuePair("project", projectName));
        // 设定要上传的文件
        ArrayList<UploadFileItem> ufi = new ArrayList<UploadFileItem>();
        ufi.add(new UploadFileItem("file", filePath));
        String response = HttpClientUtil.postForm(url,ffkvp,ufi);
        result = new JSONObject(response);
        return result;
    }

    public void deleteProject(String sessionId,String projectName){
        String url = ConfigurationManager.getProperty("managerUrl");
        Map<String, String> params = new HashMap<String,String>();
        params.put("delete","true");
        params.put("session.id",sessionId);
        params.put("project",projectName);
        getGetResponse(url,params,"UTF-8");
        JSONObject result = new JSONObject();
    }

    /**
     * 创建工程
     * @param sessionId
     * @param projectName 工程名
     * @param projectDes 工程描述
     * @return
     */
    public JSONObject createProject(String sessionId,String projectName,String projectDes){
        JSONObject result = new JSONObject();
        String url = ConfigurationManager.getProperty("createUrl");
        Map<String, String> params = new HashMap<String,String>();
        params.put("action","create");
        params.put("session.id",sessionId);
        params.put("name",projectName);
        params.put("description",projectDes);
        String response = getPostResponse(url,params,"UTF-8");
        JSONObject resultJson = new JSONObject(response);
        if(resultJson.getString("status").equals("success")){
            result.put("status","success");
            result.put("message","sucess");
            log.info(" create "+projectName +" project is sucess ...");
        }else{
            result.put("status","error");
            result.put("message",resultJson.getString("message"));
            log.error(" create project error message is : "+resultJson.getString("message"));
        }
        return  result;
    }

    /**
     * 取消调度
     * @param sessionId
     * @param scheduleId
     * @return
     */
    public JSONObject deleteSchedule(String sessionId,String scheduleId){
        Map<String, String> params = new HashMap<String,String>();
        params.put("action","removeSched");
        params.put("scheduleId",scheduleId);
        params.put("azkaban.browser.session.id",sessionId);
        String url = ConfigurationManager.getProperty("scheduleUrl");
        String response = getPostResponse(url,params,"UTF-8");
        JSONObject result = new JSONObject(response);
        return result;
    }
    public JSONObject setSchedule(String sessionId,String projectName,String group,String cronExpression){
        Map<String, String> params = new HashMap<String,String>();
        params.put("ajax","scheduleCronFlow");
        params.put("projectName",projectName);
        params.put("flow",group);
        params.put("azkaban.browser.session.id",sessionId);
        params.put("cronExpression",cronExpression);
        params.put("ajax","scheduleCronFlow");
        String url = ConfigurationManager.getProperty("scheduleUrl");
        String response = getPostResponse(url,params,"UTF-8");
        JSONObject result = new JSONObject(response);
        //调度设置成功后，需要向task_group中添加sh
        /*if(resultJson.getString("status").equals("success")){
            result=resultJson;
            log.info(" create project message is : "+resultJson.getString("message"));
            //同时需要向taskgroup中添加
        }else{
            result.put("status","error");
            result.put("message",resultJson.getString("message"));
            log.error(" create project error message is : "+resultJson.getString("message"));
        }*/
        return result;
    }

    /**
     * 获取sessionId
     * @param username
     * @param password
     * @return
     */
    private JSONObject getSeesionId(String username,String password){
        JSONObject result = new JSONObject();
        String url = ConfigurationManager.getProperty("domainUrl");
        Map<String, String> params = new HashMap<String,String>();
        params.put("action","login");
        params.put("username",username);
        params.put("password",password);
        String response = getPostResponse(url,params,"UTF-8");
        JSONObject resultJson = new JSONObject(response);
        if(resultJson.has("session.id")){
            result.put("status","success");
            result.put("message",resultJson.getString("session.id"));
            log.info("seesionId is : "+result);
        }else{
            result.put("status","error");
            result.put("message","get sessionId error message is : "+response);
            log.error("get seesionId error message is : "+response);
        }
        return result;
    }
    private  String getPostResponse(String url , Map<String, String> params, String enc){
        return HttpClientUtil.URLPost(url,params,enc);
    }
    private  String getGetResponse(String url , Map<String, String> params, String enc){
        return HttpClientUtil.URLGet(url,params,enc);
    }

    public static void main(String[] args) {
        AzkabanController controller = new AzkabanController();
        JSONObject sessionResult = controller.getSeesionId("azkaban", "azkaban@whaley");
        if(sessionResult.getString("status").equals("success")){
            String seesionId = sessionResult.getString("message");
//            System.out.println("session id is : "+ sessionResult.getString("message"));

//            JSONObject createinfo = controller.createProject(seesionId,"bb","bbbbbb");
//            System.out.println(createinfo);

//            JSONObject uploadInfo = controller.uploadProject(seesionId,"bb","DataWarehouseEtlSpark-1.0.0-dimension.zip1");
//            System.out.println(uploadInfo);
//            JSONObject scheduleInfo = controller.setSchedule(seesionId,"bb","dimension_whaley","0 0 2 ? * *");
//            System.out.println(scheduleInfo);
//            JSONObject unscheduleInfo = controller.deleteSchedule(seesionId,"139");
//            System.out.println(unscheduleInfo);
            controller.deleteProject(seesionId,"bbaaa");

        }


    }
}
