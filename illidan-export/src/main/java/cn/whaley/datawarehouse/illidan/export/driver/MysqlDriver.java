package cn.whaley.datawarehouse.illidan.export.driver;

import cn.whaley.datawarehouse.illidan.export.util.ConfigurationManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by guohao on 2017/7/14.
 */
public class MysqlDriver{
    /**
     * 控制线程并发数
     */
    private LinkedList<Integer> pools = new LinkedList<Integer>();
    private Logger logger = LoggerFactory.getLogger(MysqlDriver.class);
    private JdbcTemplate jdbcTemplate ;
    private ComboPooledDataSource dataSource ;
    private String driver;
    private String url ;
    private String userName ;
    private String passWord ;
    private int minPoolSize ;
    private int maxPoolSize ;
    private int poolSize ;
    public MysqlDriver(Map<String,String> map){
        this.driver = map.get("driver");
        this.url =map.get("url");
        this.userName =  map.get("userName");
        this.passWord = map.get("passWord");
        this.minPoolSize= ConfigurationManager.getInteger("mysql.minPoolSize");
        this.maxPoolSize= ConfigurationManager.getInteger("mysql.maxPoolSize");
        this.poolSize = ConfigurationManager.getInteger("poolSize");
        logger.info("pool size is "+poolSize);
        for(int i=0;i<poolSize;i++){
            pools.add(i);
        }
        //初始化线程池
        this.dataSource = getDataSource();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    /**
     * 获取jdbcTemplate链接
     * @return
     */
    public JdbcTemplate getJdbcTemplate(){
        return this.jdbcTemplate;
    }
    /**
     * 创建dataSource
     * @return
     */
    private ComboPooledDataSource getDataSource(){
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setJdbcUrl(url);
            dataSource.setDriverClass(driver);
            dataSource.setUser(userName);
            dataSource.setPassword(passWord);
            dataSource.setMinPoolSize(minPoolSize);
            dataSource.setMaxPoolSize(maxPoolSize);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return dataSource;
    }

    public synchronized int getPoolParam(){
        while(pools.size() == 0){
            try {
                logger.info("pools size is 0 sleep ... ");
                Thread.sleep(300);
            }catch (Exception e){
                logger.error("get pools error ... "+e.getMessage());
            }
        }
        return pools.poll();
    }

    /**
     * 释放 param 到池中
     * @param param
     */
    public void push(int param){
        pools.push(param);
    }

}


