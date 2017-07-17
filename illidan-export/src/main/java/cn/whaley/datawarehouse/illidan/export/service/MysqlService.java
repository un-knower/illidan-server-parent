package cn.whaley.datawarehouse.illidan.export.service;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.export.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by guohao on 2017/7/14.
 */
@Service
public class MysqlService {
    private static Logger logger = LoggerFactory.getLogger(Start.class);
    @Autowired
    private DbInfoService dbInfoService;
    /**
     * 拼接insert sql
     * @param hiveInfo 从hive获取的数据
     * @return
     */
    public String getInsertSql(List<Map<String, Object>> hiveInfo,Map<String,String> map){

        String tableName = map.get("mysqlTable");
        String database = map.get("mysqlDb");
        Set<String> keys = hiveInfo.get(0).keySet();
        //字段拼接
        StringBuffer fieldSb = new StringBuffer();
        //问号拼接
        StringBuffer markSb = new StringBuffer();
        for(String key:keys){
            fieldSb.append(key.split("\\.")[1]);
            fieldSb.append(",");
            markSb.append("?,");
        }
        String fields = fieldSb.toString();
        fields = fields.substring(0,fields.length()-1);
        String marks = markSb.toString();
        marks = marks.substring(0,marks.length()-1);
        //拼接sql
        String insertSql = "insert into "+database+"."+tableName+" ("+fields+") values("+marks+")";
        logger.info("insertSql is "+insertSql);
        return insertSql;
    }

    public String getDeleteSql(Map<String,String> map){
        String tableName = map.get("mysqlTable");
        String database = map.get("mysqlDb");
        String filerCondition =map.get("filterCondition");
        String deleteSql = "delete from "+database+"."+tableName+" "+filerCondition;
        logger.info("insertSql is "+deleteSql);
        return deleteSql;
    }

    public  Map<String,String> getMysqlDriveInfo(Map<String, String> map){
        return getDriveInfo("mysqlDb",map);
    }

    public Map<String,String> getDriveInfo(String db,Map<String, String> map){
        DbInfoWithStorage dbWithStorageByCode = dbInfoService.getDbWithStorageByCode(map.get(db));
        String url = dbWithStorageByCode.getAddress();
        String driver = dbWithStorageByCode.getDriver();
        String userName = dbWithStorageByCode.getUser();
        String passWord = dbWithStorageByCode.getPassword();
        map.put("url",url);
        map.put("driver",driver);
        map.put("userName",userName);
        map.put("passWord",passWord);
        return map;
    }
}
