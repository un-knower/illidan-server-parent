package cn.whaley.datawarehouse.illidan.export.execute;

import cn.whaley.datawarehouse.illidan.export.driver.JdbcFactory;
import cn.whaley.datawarehouse.illidan.export.process.MysqlProcess;
import cn.whaley.datawarehouse.illidan.export.service.MysqlService;
import cn.whaley.datawarehouse.illidan.export.util.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guohao on 2017/7/14.
 */
@Service
public class MysqlExecute extends CommonExecute {
    private static Logger logger = LoggerFactory.getLogger(MysqlExecute.class);
    @Autowired
    private MysqlService mysqlService;
    @Autowired
    private JdbcFactory jdbcFactory;

    @Override
    public void execute(List<Map<String, Object>> hiveInfo, Map<String, String> map) {
        logger.info("mysql export start ...");
        String insertSql = mysqlService.getInsertSql(hiveInfo, map);
        String deleteSql = mysqlService.getDeleteSql(map);
        //根据数据库名查找数据库相关信息
        JdbcTemplate jdbcTemplate = jdbcFactory.create(map.get("mysqlDb"));
        jdbcTemplate.update(deleteSql);
        logger.info("delete success ...");
        int i = 0;
        Map<String, String> processMap = new HashMap<>();
        processMap.put("sql", insertSql);
        ExecutorService exec = Executors.newFixedThreadPool(ConfigurationManager.getInteger("threadNum"));
        while (!isBreak()) {
            List<Object[]> data = dataQueue.poll();
            i++;
            String threadName = "thread_" + i;
            processMap.put("threadName", threadName);
            MysqlProcess mysqlProcess = new MysqlProcess(processMap, data, jdbcTemplate);
            exec.execute(mysqlProcess);
        }
        exec.shutdown();
    }


}

