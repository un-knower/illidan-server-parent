package cn.whaley.datawarehouse.illidan.engine.service;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.util.DateUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;

/**
 * Created by lituo on 2017/6/23.
 */
public class HiveService {

    private static Logger logger = LoggerFactory.getLogger(HiveService.class);
/*
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int queryForCount(String sql) {
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int update(String sql) {
        if (sql == null) {
            return -1;
        }
        if (!sql.contains(";")) {
            return this.jdbcTemplate.update(sql);
        } else {
            String[] sqls = sql.split(";");
            int[] results = this.jdbcTemplate.batchUpdate(sqls);
            return results[results.length - 1];
        }
    }*/

    static public int execute(TaskFull task, String sql) {
        if (sql == null) {
            return -1;
        }
        try {
            DbInfoWithStorage dbWithStorageByCode = task.getTable().getDbInfo();
            String url = dbWithStorageByCode.getAddress();
            String queryName = task.getAddUser() + "_illidan_" + task.getTaskCode();
            url += "?tez.queue.name=bi;hive.query.name=" + queryName;
//                    + ";hive.session.id=" + queryName + "_" + DateUtils.shortDateFormatBySecond.get().format(new Date());
            String driver = dbWithStorageByCode.getDriver();
            String userName = dbWithStorageByCode.getUser();
            String passWord = dbWithStorageByCode.getPassword();

            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setJdbcUrl(url);
            dataSource.setDriverClass(driver);
            dataSource.setUser(userName);
            dataSource.setPassword(passWord);
            dataSource.setMinPoolSize(1);

            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            if (!sql.contains(";")) {
                return jdbcTemplate.update(sql);
            } else {
                String[] sqls = sql.split(";");
                int[] results = jdbcTemplate.batchUpdate(sqls);
                return results[results.length - 1];
            }
        }catch (Exception e) {
            logger.error("执行失败", e);
            throw new RuntimeException("执行失败, task = " + task.getTaskCode() + ", msg: " + e.getMessage());
        }
    }
}
