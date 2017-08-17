package cn.whaley.datawarehouse.illidan.export;


import cn.whaley.datawarehouse.illidan.export.execute.CommonExecute;
import cn.whaley.datawarehouse.illidan.export.execute.ExecuteFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

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
        String phoenixDb = null;
        String phoenixTable = null;
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
            } else if (args[i].equalsIgnoreCase("--phoenixDb")) {
                phoenixDb = args[i + 1];
            } else if (args[i].equalsIgnoreCase("--phoenixTable")) {
                phoenixTable = args[i + 1];
            } else if (args[i].equalsIgnoreCase("--filterCondition")) {
                filterCondition = args[i + 1];
            }

        }

        if (hiveDb == null || hiveTable == null || filterCondition == null) {
            logger.error("please input correct parameter ...");
            System.exit(-1);
        }

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("hiveDb", hiveDb);
        paramMap.put("hiveTable", hiveTable);
        paramMap.put("mysqlDb", mysqlDb);
        paramMap.put("mysqlTable", mysqlTable);
        paramMap.put("phoenixDb", phoenixDb);
        paramMap.put("phoenixTable", phoenixTable);
        paramMap.put("filterCondition", filterCondition);

        System.out.println("filterCondition is ... " + filterCondition);

        CommonExecute commonExecute = ExecuteFactory.create(paramMap);

        commonExecute.start(paramMap);
    }


}
