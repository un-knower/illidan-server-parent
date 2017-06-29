package cn.whaley.datawarehouse.illidan.engine;

import cn.whaley.datawarehouse.illidan.engine.service.SubmitService;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lituo on 2017/6/22.
 */
public class Start {
    public static void main(String[] args) {

        if(args.length % 2 != 0) {
            throw new RuntimeException("参数数量错误");
        }

        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.setValidating(false);
        context.load("classpath*:spring/application-illidan-*.xml");
        context.refresh();

        SubmitService submitService = context.getBean(SubmitService.class);
        String taskCode = null;
        String dataDueDateStr = null;
        Map<String, String> paramMap = new HashMap<>();
        for (int i = 0; i < args.length; i = i + 2) {
            if(args[i].equalsIgnoreCase("--task")) {
                taskCode = args[i + 1];
            } else if(args[i].equalsIgnoreCase("--time")) {
                dataDueDateStr = args[i + 1];
            } else if(args[i].startsWith("--")) {
                paramMap.put(args[i].substring(2).trim(), args[i + 1]);
            }
        }

        submitService.submit(taskCode, dataDueDateStr, paramMap);

    }
}
