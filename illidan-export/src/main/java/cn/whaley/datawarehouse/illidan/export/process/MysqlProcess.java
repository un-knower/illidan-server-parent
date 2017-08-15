package cn.whaley.datawarehouse.illidan.export.process;

import cn.whaley.datawarehouse.illidan.export.driver.JdbcFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by guohao on 2017/7/13.
 */
public class MysqlProcess implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(MysqlProcess.class);
    private JdbcTemplate jdbcTemplate;
    private String threadName;
    private String sql;
    private List<Object[]> data;

    public MysqlProcess(Map<String, String> map, List<Object[]> data, JdbcTemplate jdbcTemplate) {
        this.threadName = map.get("threadName");
        this.sql = map.get("sql");
        this.data = data;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        logger.info("thread :" + threadName + " is running ...........");
        logger.info("thread :" + threadName + " data size is " + data.size());
        jdbcTemplate.batchUpdate(sql, data);
        logger.info("thread :" + threadName + " is end...........");
    }
}
