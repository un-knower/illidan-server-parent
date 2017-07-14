package cn.whaley.datawarehouse.illidan.export.service;


import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * Created by guohao on 2017/7/11.
 */
@Service
public class HiveService {
    public List<Map<String, Object>> getHiveInfo(Map<String,String> map, MysqlDriver mysqlDriver){
        String hiveDb = map.get("hiveDb");
        String hiveTable = map.get("hiveTable");
        String filerCondition = map.get("filerCondition");
        String sql = "select * from "+hiveDb+"."+hiveTable +" "+filerCondition;
        JdbcTemplate jdbcTemplate = mysqlDriver.getJdbcTemplate();
        return jdbcTemplate.queryForList(sql);
    }

}
