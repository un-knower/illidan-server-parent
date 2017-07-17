package cn.whaley.datawarehouse.illidan.export;


import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import cn.whaley.datawarehouse.illidan.export.execute.MysqlExecute;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.*;

/**
 * Created by guohao on 2017/7/11.
 */
public class Start {
    public static void main(String[] args) {
        if(args.length % 2 != 0) {
            throw new RuntimeException("参数数量错误");
        }
        String hiveDb = null;
        String hiveTable = null;
        String mysqlDb = null;
        String mysqlTable = null;
        String startTime = null;
        for (int i = 0; i < args.length; i = i + 2) {
            if(args[i].equalsIgnoreCase("--hiveDb")) {
                hiveDb = args[i + 1];
            }else if(args[i].equalsIgnoreCase("--hiveTable")) {
                hiveTable = args[i + 1];
            } else if(args[i].equalsIgnoreCase("--mysqlDb")) {
                mysqlDb = args[i + 1];
            } else if(args[i].equalsIgnoreCase("--mysqlTable")) {
                mysqlTable = args[i + 1];
            } else if(args[i].equalsIgnoreCase("--startTime")) {
                startTime = args[i + 1];
            }
        }
        //从数据库中获取数据

        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.setValidating(false);
        context.load("classpath*:spring/application-illidan-*.xml");
        context.refresh();


        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("hiveDb",hiveDb);
        paramMap.put("hiveTable",hiveTable);
        paramMap.put("mysqlDb",mysqlDb);
        paramMap.put("mysqlTable",mysqlTable);
        paramMap.put("filerCondition","where day_p ='"+startTime+"' limit 101");
        //获取hive数据
        MysqlExecute mysqlExecute = context.getBean(MysqlExecute.class);
        Map<String, String> map = mysqlExecute.getHiveDriveInfo(paramMap);
        MysqlDriver mysqlDriver = new MysqlDriver(map);
        List<Map<String, Object>> hiveInfo = mysqlExecute.getHiveInfo(map, mysqlDriver);
        Map<String, String> mysqlDriveInfo = mysqlExecute.getMysqlDriveInfo(map);
        mysqlExecute.start(hiveInfo,mysqlDriveInfo);
    }
}
