package cn.whaley.datawarehouse.illidan.export;


import cn.whaley.datawarehouse.illidan.export.driver.JdbcDriver;
import cn.whaley.datawarehouse.illidan.export.execute.CommonExecute;
import cn.whaley.datawarehouse.illidan.export.execute.MysqlExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.*;

/**
 * Created by guohao on 2017/7/11.
 */
public class Start {
    private static Logger logger = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) {
        if (args.length % 2 != 0) {
            throw new RuntimeException("参数数量错误");
        }
        String hiveDb = null;
        String hiveTable = null;
        String mysqlDb = null;
        String mysqlTable = null;
        String filterCondition = null;
        for (int i = 0; i < args.length; i = i + 2) {
            if (args[i].equalsIgnoreCase("--hiveDb")) {
                hiveDb = args[i + 1];
            } else if (args[i].equalsIgnoreCase("--hiveTable")) {
                hiveTable = args[i + 1];
            } else if (args[i].equalsIgnoreCase("--mysqlDb")) {
                mysqlDb = args[i + 1];
            } else if (args[i].equalsIgnoreCase("--mysqlTable")) {
                mysqlTable = args[i + 1];
            } else if (args[i].equalsIgnoreCase("--filterCondition")) {
                filterCondition = args[i + 1];
            }

        }

        if (hiveDb == null || hiveTable == null || filterCondition == null) {
            logger.error("please input correct parameter ...");
            System.exit(-1);
        }
        //从数据库中获取数据
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.setValidating(false);
        context.load("classpath*:spring/application-illidan-*.xml");
        context.refresh();

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("hiveDb", hiveDb);
        paramMap.put("hiveTable", hiveTable);
        paramMap.put("mysqlDb", mysqlDb);
        paramMap.put("mysqlTable", mysqlTable);
        paramMap.put("filterCondition", filterCondition);

        System.out.println("filterCondition is ... " + filterCondition);
        //获取hive数据
        CommonExecute commonExecute = context.getBean(CommonExecute.class);
        Map<String, String> hiveDriveInfo = commonExecute.getHiveDriveInfo(paramMap);
        JdbcDriver jdbcDriver = new JdbcDriver(hiveDriveInfo);
        List<Map<String, Object>> hiveInfo = commonExecute.getHiveInfo(hiveDriveInfo, jdbcDriver);
        if (hiveInfo == null || hiveInfo.isEmpty()) {
            System.out.println("没有数据 ... ");
            return;
        }
        //把数据放入缓存队列中
        commonExecute.addToQueue(hiveInfo);
        start(context, hiveInfo, hiveDriveInfo);
    }

    public static void start(GenericXmlApplicationContext context, List<Map<String, Object>> hiveInfo, Map<String, String> map) {
        //mysql 插入数据
        MysqlExecute mysqlExecute = context.getBean(MysqlExecute.class);
        Map<String, String> mysqlDriveInfo = mysqlExecute.getMysqlDriveInfo(map);
        mysqlExecute.start(hiveInfo, mysqlDriveInfo);
    }

}
