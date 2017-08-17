package cn.whaley.datawarehouse.illidan.export.execute;

import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.Map;

/**
 * Created by lituo on 2017/8/15.
 */
public class ExecuteFactory {

    public static CommonExecute create(Map<String, String> paramMap) {
        if (paramMap.get("mysqlDb") != null && !paramMap.get("mysqlDb").trim().isEmpty()
                && paramMap.get("mysqlTable") != null && !paramMap.get("mysqlTable").trim().isEmpty()) {
            return create("mysql");
        }
        if (paramMap.get("phoenixDb") != null && !paramMap.get("phoenixDb").trim().isEmpty()
                && paramMap.get("phoenixTable") != null && !paramMap.get("phoenixTable").trim().isEmpty()) {
            return create("phoenix");
        }
        return null;
    }

    public static CommonExecute createMysqlExecute() {
        return create("mysql");
    }

    private static CommonExecute create(String type) {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.setValidating(false);
        context.load("classpath*:spring/application-illidan-*.xml");
        context.refresh();

        if ("mysql".equals(type)) {
            return context.getBean(MysqlExecute.class);
        }
        if ("phoenix".equals(type)) {
            return context.getBean(MysqlExecute.class);
        }

        return null;
    }


}
