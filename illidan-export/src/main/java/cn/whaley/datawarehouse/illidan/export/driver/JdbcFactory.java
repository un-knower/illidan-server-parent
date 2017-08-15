package cn.whaley.datawarehouse.illidan.export.driver;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.export.util.ConfigurationManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guohao on 2017/7/14.
 */
@Service
public class JdbcFactory {
    /**
     * 控制线程并发数
     */
    private Logger logger = LoggerFactory.getLogger(JdbcFactory.class);

    private Map<String, JdbcTemplate> jdbcMap = new HashMap<>();

    @Autowired
    private DbInfoService dbInfoService;

    public JdbcTemplate create(String dbName) {
        if (dbName == null || dbName.isEmpty()) {
            return null;
        }
        JdbcTemplate result = jdbcMap.get(dbName);
        if (result != null) {
            return result;
        }

        DbInfoWithStorage dbWithStorageByCode = dbInfoService.getDbWithStorageByCode(dbName);
        String url = dbWithStorageByCode.getAddress();
        String driver = dbWithStorageByCode.getDriver();
        String userName = dbWithStorageByCode.getUser();
        String passWord = dbWithStorageByCode.getPassword();
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("driver", driver);
        map.put("userName", userName);
        map.put("passWord", passWord);
        result = create(map);

        jdbcMap.put(dbName, result);

        return result;
    }

    private JdbcTemplate create(Map<String, String> map) {
        try {
            int minPoolSize = ConfigurationManager.getInteger("mysql.minPoolSize");
            int maxPoolSize = ConfigurationManager.getInteger("mysql.maxPoolSize");
            //初始化线程池
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setJdbcUrl(map.get("url"));
            dataSource.setDriverClass(map.get("driver"));
            dataSource.setUser(map.get("userName"));
            dataSource.setPassword(map.get("passWord"));
            dataSource.setMinPoolSize(minPoolSize);
            dataSource.setMaxPoolSize(maxPoolSize);

            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            return jdbcTemplate;
        } catch (Exception e) {
            logger.error("创建dataSource失败", e);
            throw new RuntimeException("创建dataSource失败, url = " + map.get("url"));
        }
    }

    /**
     * 获取jdbcTemplate链接
     *
     * @return
     */
    public JdbcTemplate getJdbcTemplate(String dbName) {
        return create(dbName);
    }


}


