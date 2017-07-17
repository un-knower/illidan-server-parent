package cn.whaley.datawarehouse.illidan.export.service;


import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
/**
 * Created by guohao on 2017/7/11.
 */
@Service
public class HiveService {
    private static Logger logger = LoggerFactory.getLogger(HiveService.class);
    @Autowired
    private MysqlService mysqlService;
    public List<Map<String, Object>> getHiveInfo(Map<String,String> map, MysqlDriver mysqlDriver){
        String hiveDb = map.get("hiveDb");
        String hiveTable = map.get("hiveTable");
        String filerCondition = map.get("filterCondition");
        String sql = "select * from "+hiveDb+"."+hiveTable +" "+filerCondition;
        List<Map<String, Object>> hiveData = mysqlDriver.getJdbcTemplate().queryForList(sql);
        logger.info("hive select total data size is "+hiveData.size());
        logger.info("get hive data success ...");
        return hiveData;
    }

    /**
     * 获取驱动的相关信息,同时把已有的数据向下传递
     * @param map
     * @return
     */
    public  Map<String,String> getHiveDriveInfo(Map<String, String> map){
        return mysqlService.getDriveInfo("hiveDb",map);
    }

}
