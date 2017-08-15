package cn.whaley.datawarehouse.illidan.export.driver;

import cn.whaley.datawarehouse.illidan.export.util.ConfigurationManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by guohao on 2017/7/14.
 */
public class JdbcDriver {
    /**
     * 控制线程并发数
     */
    private Logger logger = LoggerFactory.getLogger(JdbcDriver.class);
    private JdbcTemplate jdbcTemplate;
    private ComboPooledDataSource dataSource;
    private String driver;
    private String url;
    private String userName;
    private String passWord;
    private int minPoolSize;
    private int maxPoolSize;

    public JdbcDriver(Map<String, String> map) {
        this.driver = map.get("driver");
        this.url = map.get("url");
        this.userName = map.get("userName");
        this.passWord = map.get("passWord");
        this.minPoolSize = ConfigurationManager.getInteger("mysql.minPoolSize");
        this.maxPoolSize = ConfigurationManager.getInteger("mysql.maxPoolSize");
        //初始化线程池
        this.dataSource = getDataSource();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 获取jdbcTemplate链接
     *
     * @return
     */
    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    /**
     * 创建dataSource
     *
     * @return
     */
    private ComboPooledDataSource getDataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setJdbcUrl(url);
            dataSource.setDriverClass(driver);
            dataSource.setUser(userName);
            dataSource.setPassword(passWord);
            dataSource.setMinPoolSize(minPoolSize);
            dataSource.setMaxPoolSize(maxPoolSize);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return dataSource;
    }


}


