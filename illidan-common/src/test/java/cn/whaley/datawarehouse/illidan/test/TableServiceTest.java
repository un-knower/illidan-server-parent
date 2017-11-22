package cn.whaley.datawarehouse.illidan.test;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.FullHiveTable;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by lituo on 2017/11/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:spring/application-illidan-*.xml"})
public class TableServiceTest {

    @Autowired
    private TableInfoService tableInfoService;

    @Test
    public void updateTable() throws Exception {
        Long hiveTableId = 342L;
        FullHiveTable fullHiveTable = tableInfoService.getFullHiveTable(hiveTableId);
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setColName("test7");
        fieldInfo.setColType("string");
        fieldInfo.setTableId(hiveTableId);
        fieldInfo.setIsPartitionCol("0");
        fieldInfo.setColIndex(12);
        fullHiveTable.getHiveTable().getFieldList().add(fieldInfo);
//        fullHiveTable.getHiveTable().setMysqlTableId(null);
//        fullHiveTable.setMysqlTable(null);

        Long result = tableInfoService.updateFullHiveTable(fullHiveTable);

        Assert.assertTrue(result >= 0);
    }

    @Test
    public void dropTableTest()  throws Exception {
        tableInfoService.dropFullHiveTable(342L);
    }

}
