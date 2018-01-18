package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.server.util.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wujiulin on 2018/1/16.
 */
@Service
public class AuthorizeHttpService {

    private String url = "http://auth-platform.aginomoto.com";
//    private String url = ConfigUtils.get("newillidan.authorize.url");

    /**
     * 创建目录
     * @param pid 目标父目录ID
     * @param dir_name 目录名称
     * @param group_id 默认授权的组,多个组以逗号隔开
     * @param uid 默认授权的用户,多个用户以逗号隔开
     * @return dir_id 目录id
     * */
    public String createDir(String pid, String dir_name, String group_id, String uid){
        String dir_id = "";
        Map<String, String> params = new HashMap<String,String>();
        params.put("pid", pid);
        params.put("dir_name", dir_name);
        params.put("group_id", group_id);
        params.put("uid", uid);
        String response = HttpClientUtil.URLPost(url+"/dir", params,"UTF-8");
        JSONObject resultJson = new JSONObject(response);
        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
            JSONObject dir_id_json = resultJson.getJSONObject("data");
            dir_id = dir_id_json.getString("dir_id");
        }
        return dir_id;
    }

    /**
     * 查询用户是否拥有目录权限
     * @param uid oa 用户名,如wang.gang
     * @param sys_id 系统ID,系统后台分配的
     * @param dir_id 目录ID,多个以逗号隔开
     * @return 目录权限List, key为目录id， value表示是否有权限 1:有权限 0:没有权限
     * */
    public List<Map> checkAuth(String uid, String sys_id, String dir_id){
        List<Map> result = new ArrayList<>();
        Map<String, String> params = new HashMap<String,String>();
        params.put("uid", uid);
        params.put("sys_id", sys_id);
        params.put("dir_id", dir_id);
        String response = HttpClientUtil.URLGet(url+"/check_auth", params, "UTF-8");
        JSONObject resultJson = new JSONObject(response);
        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
            Map map = JSON.parseObject(resultJson.getJSONObject("data").toString());
            result.add(map);
        }
        return result;
    }

    /**
     * 返回用户所有权限
     * @param uid 用户名
     * @param sys_id 系统id
     * @return 权限目录list
     * */
    public List getAllDirs(String uid, String sys_id){
        List dirList = new ArrayList();
        Map<String, String> params = new HashMap<String,String>();
        params.put("uid", uid);
        params.put("sys_id", sys_id);
        String response = HttpClientUtil.URLGet(url+"/dirs", params, "UTF-8");
        JSONObject resultJson = new JSONObject(response);
        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
            JSONArray jan = (JSONArray) resultJson.get("data");
            if(jan!=null||jan.length()!=0) {
                for (int i = 0; i < jan.length(); i++) {
                    String str = jan.get(i).toString();
                    dirList.add(str);
                }
            }
        }
        return dirList;
    }

    /**
     * 返回用户所属用户组
     * @param uid 默认授权的用户
     * @param sys_id 系统id
     * @return 用户组
     * */
    public List<Map> getGroups(String uid, String sys_id){
        List<Map> result = new ArrayList<>();
        Map<String, String> params = new HashMap<String,String>();
        params.put("uid", uid);
        params.put("sys_id", sys_id);
        String response = HttpClientUtil.URLGet(url+"/groups", params, "UTF-8");
        JSONObject resultJson = new JSONObject(response);
        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
            JSONArray jan = (JSONArray) resultJson.get("data");
            if(jan!=null||jan.length()!=0) {
                for (int i = 0; i < jan.length(); i++) {
                    JSONObject jo = jan.getJSONObject(i);
                    String id = jo.get("id").toString();
                    String name = jo.getString("name");
                    Map<String, String> map = new HashMap<>();
                    map.put("id", id);
                    map.put("name", name);
                    result.add(map);
                }
            }
        }
        return  result;
    }
}
