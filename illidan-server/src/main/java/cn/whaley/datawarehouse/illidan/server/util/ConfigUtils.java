package cn.whaley.datawarehouse.illidan.server.util;

/**
 * Created by Tony on 16/9/24.
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {
    private static ConfigCache configCache = null;

    private ConfigUtils() {
    }

    public static Integer getInteger(String key, Integer defaultValue) {
        Integer result = defaultValue;
        try {
            String value = get(key);
            if (value == null) {
                return result;
            }
            result = Integer.parseInt(value);
        } catch (Exception e) {

        }
        return result;
    }

    public static List<String> getStringListFromJson(String key) {
        List<String> result = new ArrayList<String>();
        try {
            String value = get(key);
            if (value == null) {
                return result;
            }
            JSONArray jsonArray = JSONObject.parseArray(value);
            for(int i = 0; i < jsonArray.size(); i++) {
                result.add(jsonArray.getString(i));
            }
        } catch (Exception e) {

        }
        return result;
    }

    public static String get(String key) {
        if (null == configCache && !initailize()) {
            return null;
        } else {
            try {
                return configCache.getProperty(key);
            } catch (LionException var2) {
                return null;
            }
        }
    }

    private static synchronized boolean initailize() {
        try {
            configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
            return true;
        } catch (LionException var1) {
            return false;
        }
    }
}

