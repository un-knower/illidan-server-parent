package cn.whaley.datawarehouse.illidan.engine.service;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by lituo on 2017/6/23.
 */
public class HiveService {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int queryForCount(String sql) {
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
