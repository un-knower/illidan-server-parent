package cn.whaley.datawarehouse.illidan.export.execute;

import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import cn.whaley.datawarehouse.illidan.export.process.MysqlProcess;
import cn.whaley.datawarehouse.illidan.export.service.MysqlService;
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
    @Autowired
    private MysqlService mysqlService;
    @Override
    public void execute(List<Map<String, Object>> hiveInfo,Map<String,String> map) {
        String insertSql = mysqlService.getInsertSql(hiveInfo,map);
        String deleteSql = mysqlService.getDeleteSql(map);
        //根据数据库名查找数据库相关信息
        MysqlDriver mysqlDriver = new MysqlDriver(map);
        mysqlDriver.getJdbcTemplate().update(deleteSql);
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
            new MysqlProcess(processMap,data,mysqlDriver);
        }
    }

}

