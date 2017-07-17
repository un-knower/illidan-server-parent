package cn.whaley.datawarehouse.illidan.export.execute;

import cn.whaley.datawarehouse.illidan.export.Start;
import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import cn.whaley.datawarehouse.illidan.export.process.MysqlProcess;
import cn.whaley.datawarehouse.illidan.export.service.MysqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guohao on 2017/7/14.
 */
@Service
public class MysqlExecute extends CommonExecute {
    private static Logger logger = LoggerFactory.getLogger(MysqlExecute.class);
    @Autowired
    private MysqlService mysqlService;
    @Override
    public void execute(List<Map<String, Object>> hiveInfo,Map<String,String> map) {
        logger.info("mysql export start ...");
        String insertSql = mysqlService.getInsertSql(hiveInfo,map);
        String deleteSql = mysqlService.getDeleteSql(map);
        //根据数据库名查找数据库相关信息
        MysqlDriver mysqlDriver = new MysqlDriver(map);
        mysqlDriver.getJdbcTemplate().update(deleteSql);
        logger.info("delete success ...");
        int i = 0;
        Map<String,String> processMap = new HashMap<>();
        processMap.put("sql",insertSql);
        while (!isBreak()){
            int poolParam = mysqlDriver.getPoolParam();
            List<Object[]> data = dataQueue.poll();
            i++;
            String threadName = "thread_"+i;
            processMap.put("threadName",threadName);
            processMap.put("poolParam",String.valueOf(poolParam));
            MysqlProcess mysqlProcess = new MysqlProcess(processMap, data, mysqlDriver);
            Thread thread = new Thread(mysqlProcess);
            thread.start();
        }
    }
    public  Map<String,String> getMysqlDriveInfo(Map<String, String> map){
        return mysqlService.getMysqlDriveInfo(map);
    }

}

