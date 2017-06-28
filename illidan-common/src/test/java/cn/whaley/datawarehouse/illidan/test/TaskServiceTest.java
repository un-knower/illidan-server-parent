package cn.whaley.datawarehouse.illidan.test;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hc on 2017/6/28.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:spring/application-illidan-*.xml"})
public class TaskServiceTest {
    private TaskService taskService ;
    private DbInfoService dbInfoService;

    @Test
    public void insertFullTaskTask(){
        TaskFull taskFull = new TaskFull();

        List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();

        FieldInfo fieldInfo1 = new FieldInfo();
        fieldInfo1.setColName("name");
        fieldInfo1.setColType("string");
        fieldInfo1.setColDes("姓名");
        fieldInfo1.setColIndex(1);
        fieldInfo1.setIsPartitionCol("0");
        fieldInfo1.setCreateTime(new Date());
        fieldInfo1.setUpdateTime(new Date());

        FieldInfo fieldInfo2 = new FieldInfo();
        fieldInfo2.setColName("age");
        fieldInfo2.setColType("int");
        fieldInfo2.setColDes("年龄");
        fieldInfo2.setColIndex(2);
        fieldInfo2.setIsPartitionCol("0");
        fieldInfo2.setCreateTime(new Date());
        fieldInfo2.setUpdateTime(new Date());

        FieldInfo fieldInfo3 = new FieldInfo();
        fieldInfo3.setColName("day_p");
        fieldInfo3.setColType("string");
        fieldInfo3.setColDes("天分区");
        fieldInfo3.setColIndex(1);
        fieldInfo3.setIsPartitionCol("1");
        fieldInfo3.setCreateTime(new Date());
        fieldInfo3.setUpdateTime(new Date());
        fieldInfos.add(fieldInfo1);
        fieldInfos.add(fieldInfo2);
        fieldInfos.add(fieldInfo3);

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableCode("tableCode1");
        tableInfo.setTableDes("tableCode1");
        tableInfo.setDataType("parquet");
        tableInfo.setDbId(1L);
        tableInfo.setCreateTime(new Date());
        tableInfo.setUpdataTime(new Date());

        DbInfo dbInfo = dbInfoService.get(1L);

        TableWithField tableWithField = new TableWithField();
        BeanUtils.copyProperties(tableInfo,tableWithField);
        tableWithField.setFieldList(fieldInfos);
        tableWithField.setDbInfo(dbInfo);




        System.out.println(tableWithField.toString());





    }
}
