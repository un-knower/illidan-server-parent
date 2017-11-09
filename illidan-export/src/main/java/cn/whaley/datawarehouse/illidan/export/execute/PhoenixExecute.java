package cn.whaley.datawarehouse.illidan.export.execute;

import cn.whaley.datawarehouse.illidan.common.processor.JdbcFactory;
import cn.whaley.datawarehouse.illidan.export.process.JdbcProcess;
import cn.whaley.datawarehouse.illidan.export.util.PhoenixUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lituo on 2017/8/15.
 */
public class PhoenixExecute extends CommonExecute {

    private static Logger logger = LoggerFactory.getLogger(PhoenixExecute.class);

    @Autowired
    private JdbcFactory jdbcFactory;

    @Override
    protected boolean delete(Map<String, String> paramMap) {
        return true;
    }

    @Override
    protected Map<String, Object> prepareExecute(Map<String, String> paramMap) {
        List<String> keys = null;
        String insertSql = PhoenixUtils.getUpsertSql(hiveColumnNames, keys, paramMap);
        Map<String, Object> processMap = new HashMap<>();
        processMap.put("sql", insertSql);
        return processMap;
    }

    @Override
    protected Runnable process(List<Map<String, Object>> data, Map<String, String> paramMap, Map<String, Object> processMap) {
        String threadName = "thread_" + processMap.get("batchIndex");
        processMap.put("threadName", threadName);
        JdbcTemplate jdbcTemplate = jdbcFactory.create(paramMap.get("phoenixDb"));
        List<Object[]> processData = new ArrayList<>();
        for(Map<String, Object> d : data) {
            processData.add(d.values().toArray());
        }
        //TODO 增加on duplicate 后部分的数据
        JdbcProcess phoenixProcess = new JdbcProcess(processMap, processData, jdbcTemplate);
        return phoenixProcess;
    }
}
