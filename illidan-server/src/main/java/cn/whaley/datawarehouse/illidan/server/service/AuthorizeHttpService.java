package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import cn.whaley.datawarehouse.illidan.server.util.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wujiulin on 2018/1/16.
 */
@Service
public class AuthorizeHttpService {
    private Logger logger = LoggerFactory.getLogger(AuthorizeHttpService.class);

    /**
     * 创建目录
     * @param pid 目标父目录ID
     * @param dirName 目录名称
     * @param uids 默认授权的用户
     * @param groupIds 默认授权的用户组
     * @return dir_id 目录id
     * */
    public String createAuth(String pid, String dirName, List<String> uids, List<String> groupIds){
        String dir_id = "";
        Map<String, String> params = new HashMap<String,String>();
        String sys_id = ConfigUtils.get("newillidan.authorize.sys_id");
//        List<Map> groupList = getGroups(uid, sys_id);
//        String groupId = "";
//        for (Map m : groupList){
//            groupId = groupId + m.get("id") + ",";
//        }
//        groupId = groupId.substring(0,groupId.length()-1);
//        logger.info("groupId: "+groupId);
        params.put("pid", pid);
        params.put("dir_name", dirName);
        if(uids!=null && uids.size() > 0) {
            params.put("uid", StringUtils.join(uids, ","));
        }
        if(groupIds!=null && groupIds.size() > 0) {
            params.put("group_id", StringUtils.join(groupIds, ","));
        }
        String url = ConfigUtils.get("newillidan.authorize.url");
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
     * @param dir_ids 目录ID
     * @return 目录权限List, key为目录id， value表示是否有权限 1:有权限 0:没有权限
     * */
    public Map<String, Integer> checkAuth(String uid, String sys_id, List<String> dir_ids){
        Map<String, Integer> result = new HashMap();
        if(dir_ids == null || dir_ids.isEmpty()) {
            return result;
        }
        Map<String, String> params = new HashMap<String,String>();
        params.put("uid", uid);
        params.put("sys_id", sys_id);
        params.put("dir_id", StringUtils.join(dir_ids, ","));
        String url = ConfigUtils.get("newillidan.authorize.url");
        String response = HttpClientUtil.URLGet(url+"/check_auth", params, "UTF-8");
        JSONObject resultJson = new JSONObject(response);
        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
            for(String key: resultJson.getJSONObject("data").keySet()) {
                result.put(key, resultJson.getJSONObject("data").getInt(key));
            }
        }
        return result;
    }

    public boolean checkAuth(String uid, String sys_id, String dir_id) {
        Map map = checkAuth(uid, sys_id, Collections.singletonList(dir_id));
        if(map.get(dir_id) != null && map.get(dir_id).toString().equals("1")) {
            return true;
        }
        return false;
    }

    /**
     * 返回用户所有权限
     * @param uid 用户名
     * @param sys_id 系统id
     * @return 权限目录list
     * */
    public List<String> getAllDirs(String uid, String sys_id){
        List<String> dirList = new ArrayList<String>();
        Map<String, String> params = new HashMap<String,String>();
        params.put("uid", uid);
        params.put("sys_id", sys_id);
        String url = ConfigUtils.get("newillidan.authorize.url");
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
    public List<Map<String, String>> getGroups(String uid, String sys_id){
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> params = new HashMap<String,String>();
        params.put("uid", uid);
        params.put("sys_id", sys_id);
        String url = ConfigUtils.get("newillidan.authorize.url");
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
