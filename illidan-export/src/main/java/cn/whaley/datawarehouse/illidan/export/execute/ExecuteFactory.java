package cn.whaley.datawarehouse.illidan.export.execute;

import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.Map;

/**
 * Created by lituo on 2017/8/15.
 */
public class ExecuteFactory {

    public static CommonExecute create(Map<String, String> paramMap) {
        return create("mysql");
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
