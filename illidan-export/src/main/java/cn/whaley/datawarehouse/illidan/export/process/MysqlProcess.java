package cn.whaley.datawarehouse.illidan.export.process;

import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by hc on 2017/7/13.
 */
public class MysqlProcess implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(MysqlProcess.class);
    private MysqlDriver mysqlDriver;
    private String threadName ;
    private String sql ;
    private List<Object[]> data ;
    private int poolParam;
    public MysqlProcess(Map<String,String> map,List<Object[]> data,MysqlDriver mysqlDriver){
        this.threadName = map.get("threadName") ;
        this.sql = map.get("sql");
        this.poolParam =Integer.valueOf(map.get("poolParam"));
        this.data = data;

        this.mysqlDriver=mysqlDriver;
    }
    @Override
    public void run() {
        logger.info("thread :"+threadName+" is running ...........");
        logger.info("thread :"+threadName+" data size is "+data.size());
        mysqlDriver.getJdbcTemplate().batchUpdate(sql,data);
        mysqlDriver.push(poolParam);
        logger.info("thread :"+threadName+" is end...........");
    }
}
