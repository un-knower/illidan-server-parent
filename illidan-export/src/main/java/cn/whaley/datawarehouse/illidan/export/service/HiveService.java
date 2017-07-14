package cn.whaley.datawarehouse.illidan.export.service;


import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Created by hc on 2017/7/11.
 */
@Service
public class HiveService {
@Autowired
private DbInfoService dbInfoService;
    public List<Map<String, Object>> getHiveInfo(Map<String,String> map, MysqlDriver mysqlDriver){
        String hiveDb = map.get("hiveDb");
        String hiveTable = map.get("hiveTable");
        String filerCondition = map.get("filerCondition");
        String sql = "select * from "+hiveDb+"."+hiveTable +" "+filerCondition;
        JdbcTemplate jdbcTemplate = mysqlDriver.getJdbcTemplate();
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String,String> getHiveDriveInfo(String hiveDb){
        Map<String,String> map = new HashMap<>();
        DbInfoWithStorage dbWithStorageByCode = dbInfoService.getDbWithStorageByCode(hiveDb);
        String address = dbWithStorageByCode.getAddress();
        String url = "jdbc:hive://"+address;
        String driver="org.apache.hive.jdbc.HiveDriver";
        String userName = dbWithStorageByCode.getUser();
        String passWord = dbWithStorageByCode.getPassword();

        map.put("url",url);
        map.put("driver",driver);
        map.put("userName",userName);
        map.put("passWord",passWord);
        return map;
    }


}
