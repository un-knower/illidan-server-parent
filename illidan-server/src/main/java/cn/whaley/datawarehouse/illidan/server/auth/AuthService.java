package cn.whaley.datawarehouse.illidan.server.auth;

import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import cn.whaley.datawarehouse.illidan.server.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lituo on 2018/1/17.
 */
public class AuthService {
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    public static boolean skipLogin() {
        String skipLogin = ConfigUtils.get("streaming.skipLogin");
        if (skipLogin != null && skipLogin.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public static Map<String, String> getUserInfo(String token) {
        if (token == null) {
            logger.warn("token is null");
            return null;
        }
        String serverUrl = ConfigUtils.get("streaming.sso.server.url");
        String url = "http://" + serverUrl + "/user/getinfo?sso_tn=" + token;
        String response = HttpUtils.get(url);
        if (response == null) {
            logger.warn("获取用户信息失败，url=" + url);
            return null;
        }
        JSONObject jsonObject = (JSONObject) JSON.parse(response);
        int status = jsonObject.getIntValue("status");
        if (status != 1) {
            logger.warn("获取用户信息错误，msg =" + jsonObject.getString("msg") + ", url=" + url);
            return null;
        }

        JSONObject userInfo = jsonObject.getJSONObject("memberdata");

        Map<String, String> result = new HashMap<>();
        for (String key : userInfo.keySet()) {
            result.put(key, userInfo.getString(key));
        }
        return result;
    }
}
