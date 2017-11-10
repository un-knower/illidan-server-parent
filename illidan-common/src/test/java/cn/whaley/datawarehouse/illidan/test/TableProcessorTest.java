package cn.whaley.datawarehouse.illidan.test;

import cn.whaley.datawarehouse.illidan.common.processor.TableProcessor;
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
public class TableProcessorTest {

    @Autowired
    private TableProcessor tableProcessor;

    @Test
    public void createTable() throws Exception {
        Long hiveTableId = 327L;
        boolean result = tableProcessor.createTable(hiveTableId);

        Assert.assertTrue(result);
    }

}
