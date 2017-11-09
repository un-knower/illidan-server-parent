package cn.whaley.datawarehouse.illidan.export.execute;

import cn.whaley.datawarehouse.illidan.common.processor.JdbcFactory;
import cn.whaley.datawarehouse.illidan.export.process.JdbcProcess;
import cn.whaley.datawarehouse.illidan.export.util.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private JdbcFactory jdbcFactory;

    @Override
    protected boolean delete(Map<String, String> paramMap) {
        String deleteSql = MysqlUtils.getDeleteSql(paramMap);
        //根据数据库名查找数据库相关信息
        JdbcTemplate jdbcTemplate = jdbcFactory.create(paramMap.get("mysqlDb"));
        jdbcTemplate.update(deleteSql);
        logger.info("delete success ...");
        return true;
    }

    @Override
    protected Map<String, Object> prepareExecute(Map<String, String> paramMap) {
        String insertSql = MysqlUtils.getInsertSql(hiveColumnNames, paramMap);
        Map<String, Object> processMap = new HashMap<>();
        processMap.put("sql", insertSql);
        return processMap;
    }

    @Override
    protected Runnable process(List<Map<String, Object>> data, Map<String, String> paramMap, Map<String, Object> processMap) {
        String threadName = "thread_" + processMap.get("batchIndex");
        processMap.put("threadName", threadName);
        JdbcTemplate jdbcTemplate = jdbcFactory.create(paramMap.get("mysqlDb"));
        List<Object[]> processData = new ArrayList<>();
        for(Map<String, Object> d : data) {
            processData.add(d.values().toArray());
        }
        JdbcProcess mysqlProcess = new JdbcProcess(processMap, processData, jdbcTemplate);
        return mysqlProcess;
    }


}

