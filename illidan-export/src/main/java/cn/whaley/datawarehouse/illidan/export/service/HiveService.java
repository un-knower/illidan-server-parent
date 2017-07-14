package cn.whaley.datawarehouse.illidan.export.service;


import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Created by hc on 2017/7/11.
 */
@Service
public class HiveService {
    public List<Map<String, Object>> getHiveInfo(Map<String,String> map, MysqlDriver mysqlDriver){
        String hiveDb = map.get("hiveDb");
        String hiveTable = map.get("hiveTable");
        String filerCondition = map.get("filerCondition");
        String sql = "select * from "+hiveDb+"."+hiveTable +" "+filerCondition;
        return mysqlDriver.getJdbcTemplate().queryForList(sql);
    }

    public Map<String,String> getHiveDriveInfo(String hiveDb){

        return null;
    }


}
