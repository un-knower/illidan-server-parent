package cn.whaley.datawarehouse.illidan.common.processor;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JdbcFactory {

    private static final Integer MYSQL_MIN_POOL_SIZE = 1;
    private static final Integer MYSQL_MAX_POOL_SIZE = 5;
    private Logger logger = LoggerFactory.getLogger(JdbcFactory.class);
    private Map<Long, JdbcTemplate> jdbcMap = new HashMap<>();

    @Autowired
    private DbInfoService dbInfoService;

    public JdbcTemplate create(String dbName) {
        if (dbName == null || dbName.isEmpty()) {
            return null;
        }

        DbInfoWithStorage dbWithStorageByCode = dbInfoService.getDbWithStorageByCode(dbName);
        Long storageId = dbWithStorageByCode.getStorageId();

        JdbcTemplate result = jdbcMap.get(storageId);
        if (result != null) {
            return result;
        }
        String url = dbWithStorageByCode.getAddress();
        String driver = dbWithStorageByCode.getDriver();
        String userName = dbWithStorageByCode.getUser();
        String passWord = dbWithStorageByCode.getPassword();
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("driver", driver);
        map.put("userName", userName);
        map.put("passWord", passWord);
        map.put("minPoolSize", MYSQL_MIN_POOL_SIZE.toString());
        map.put("maxPoolSize", MYSQL_MAX_POOL_SIZE.toString());

        result = create(map);

        jdbcMap.put(storageId, result);

        return result;
    }

    private JdbcTemplate create(Map<String, String> map) {
        try {

            //初始化线程池
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setJdbcUrl(map.get("url"));
            dataSource.setDriverClass(map.get("driver"));
            dataSource.setUser(map.get("userName"));
            dataSource.setPassword(map.get("passWord"));
            dataSource.setMinPoolSize(Integer.parseInt(map.get("minPoolSize")));
            dataSource.setMaxPoolSize(Integer.parseInt(map.get("maxPoolSize")));

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


