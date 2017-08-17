package cn.whaley.datawarehouse.illidan.export.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by guohao on 2017/7/13.
 */
public class JdbcProcess implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(JdbcProcess.class);
    private JdbcTemplate jdbcTemplate;
    private String threadName;
    private String sql;
    private List<Object[]> data;

    public JdbcProcess(Map<String, Object> map, List<Object[]> data, JdbcTemplate jdbcTemplate) {
        this.threadName = map.get("threadName").toString();
        illidan-export/        this.data = data;
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
