package cn.whaley.datawarehouse.illidan.export.process;

import cn.whaley.datawarehouse.illidan.export.driver.JdbcDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by guohao on 2017/7/13.
 */
public class MysqlProcess implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(MysqlProcess.class);
    private JdbcDriver jdbcDriver;
    private String threadName;
    private String sql;
    private List<Object[]> data;

    public MysqlProcess(Map<String, String> map, List<Object[]> data, JdbcDriver jdbcDriver) {
        this.threadName = map.get("threadName");
        this.sql = map.get("sql");
        this.data = data;
        this.jdbcDriver = jdbcDriver;
    }

    @Override
    public void run() {
        logger.info("thread :" + threadName + " is running ...........");
        logger.info("thread :" + threadName + " data size is " + data.size());
        jdbcDriver.getJdbcTemplate().batchUpdate(sql, data);
        logger.info("thread :" + threadName + " is end...........");
    }
}
