package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.enums.AuthorityTypeEnum;
import cn.whaley.datawarehouse.illidan.common.service.authorize.AuthorizeService;
import cn.whaley.datawarehouse.illidan.server.util.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:spring/application-illidan-*.xml"})
public class HttpClientTest {
    @Autowired
    private AuthorizeService authorizeService;
    private String url = "http://auth-platform.aginomoto.com";


    @Test
    public void test(){
        Authorize authorizeQuery = authorizeService.getByParentId(1L, AuthorityTypeEnum.PROJECT);
        System.out.println("====="+authorizeQuery);
    }

    @Test
    public void authTest(){
        Map<String, String> params = new HashMap<String,String>();

        //create dir
        String dir_id = "";
        params.put("pid", "7_228");
        params.put("dir_name", "publish_project_21");
        params.put("group_id", "5");
        params.put("uid", "wu.jiulin");
        String response = HttpClientUtil.URLPost(url+"/dir", params,"UTF-8");
        JSONObject resultJson = new JSONObject(response);
        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
            JSONObject dir_id_json = resultJson.getJSONObject("data");
            dir_id = dir_id_json.getString("dir_id");
        }
        System.out.println(dir_id);



        //查询用户是否拥有目录权限
//        params.put("uid", "li.tuo");
//        params.put("sys_id", "7");
//        params.put("dir_id", "7_171");
//        Map result = new HashMap();
//        String response = HttpClientUtil.URLGet(url+"/check_auth", params, "UTF-8");
//        JSONObject resultJson = new JSONObject(response);
//        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
//            result = JSON.parseObject(resultJson.getJSONObject("data").toString());
//        }
//        for (Object obj : result.keySet()){
//            System.out.println("key为："+obj+"值为："+result.get(obj));
//        }



        //返回用户所有权限
//        List dirList = new ArrayList();
//        params.put("uid", "wu.jiulin");
//        params.put("sys_id", "7");
//        String response = HttpClientUtil.URLGet(url+"/dirs", params, "UTF-8");
//        JSONObject resultJson = new JSONObject(response);
//        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
//            JSONArray jan = (JSONArray) resultJson.get("data");
//            if(jan!=null||jan.length()!=0) {
//                for (int i = 0; i < jan.length(); i++) {
//                    String str = jan.get(i).toString();
//                    dirList.add(str);
//                }
//            }
//        }
//        System.out.println(dirList);


        //返回用户所属用户组
//        List<Map> result = new ArrayList<>();
//        params.put("uid", "wu.jiulin");
//        params.put("sys_id", "7");
//        String response = HttpClientUtil.URLGet(url+"/groups", params, "UTF-8");
//        JSONObject resultJson = new JSONObject(response);
//        if(resultJson.get("code").toString().equals("200") && resultJson.getString("msg").equals("success")){
//            JSONArray jan = (JSONArray) resultJson.get("data");
//            if(jan!=null||jan.length()!=0) {
//                for (int i = 0; i < jan.length(); i++) {
//                    JSONObject jo = jan.getJSONObject(i);
//                    String id = jo.get("id").toString();
//                    String name = jo.getString("name");
//                    Map<String, String> map = new HashMap<>();
//                    map.put("id", id);
//                    map.put("name", name);
//                    result.add(map);
//                }
//            }
//        }
//        for (Map map : result){
//            System.out.println(map.get("id"));
//            System.out.println(map.get("name"));
//        }

//        List list = new ArrayList();
//        String str1 = new String("abcde");
//        String str2 = new String("abcde");
//        list.add(str1);
//        list.add(str2);
//        System.out.println("list.size()=" + list.size());
//        for (int i = list.size()-1; i >=0; i--) {
//            if (((String) list.get(i)).startsWith("abcde")) {
//                list.remove(i);
//            }
//        }
//        System.out.println("after remove:list.size()=" + list.size());

    }
}
