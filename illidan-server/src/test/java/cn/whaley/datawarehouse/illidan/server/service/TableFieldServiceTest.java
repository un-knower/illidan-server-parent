package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by lituo on 2017/11/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:spring/application-illidan-*.xml"})
public class TableFieldServiceTest {

    @Autowired
    private TableFieldService tableFieldService;

    @Test
    public void getCreateSqlFromHiveTest() {
        String sql = tableFieldService.getCreateSqlFromHive("dws_orca_bi","hivetable4");
        Assert.assertTrue(sql != null);
    }

    @Test
    public void getTableInfoFromHiveTest() {
        TableWithField tableWithField = tableFieldService.getTableInfoFromHive(342L);
        Assert.assertTrue(tableWithField != null);
    }

    @Test
    public void completeTableInfoAllTest() {
        tableFieldService.completeTableInfoAll();
    }
}
