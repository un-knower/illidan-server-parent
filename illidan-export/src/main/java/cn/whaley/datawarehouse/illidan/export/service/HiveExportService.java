package cn.whaley.datawarehouse.illidan.export.service;


import cn.whaley.datawarehouse.illidan.export.driver.JdbcFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by guohao on 2017/7/11.
 */
@Service
public class HiveExportService {
    private static Logger logger = LoggerFactory.getLogger(HiveExportService.class);
    @Autowired
    private JdbcFactory jdbcFactory;

    public List<Map<String, Object>> getHiveInfo(Map<String, String> map) {
        String hiveDb = map.get("hiveDb");
        String hiveTable = map.get("hiveTable");
        String filerCondition = map.get("filterCondition");
        String sql = "select * from " + hiveDb + "." + hiveTable + " " + filerCondition;
        JdbcTemplate jdbcTemplate = jdbcFactory.create(hiveDb);
        List<Map<String, Object>> hiveData = jdbcTemplate.queryForList(sql);
        logger.info("hive select total data size is " + hiveData.size());
        logger.info("get hive data success ...");
        return hiveData;
    }


}
