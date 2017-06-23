package cn.whaley.datawarehouse.illidan.engine;

import cn.whaley.datawarehouse.illidan.common.service.UserService;
import cn.whaley.datawarehouse.illidan.engine.service.HiveService;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Created by lituo on 2017/6/22.
 */
public class Start {
    public static void main(String[] args) {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.setValidating(false);
        context.load("classpath*:spring/application-illidan-*.xml");
        context.refresh();
        UserService userService = context.getBean(UserService.class);
        System.out.println(userService.findAll().size());
        HiveService hiveService = context.getBean(HiveService.class);
        System.out.println(hiveService.queryForCount("select count(1) from default.eagle_live_statistics"));
    }
}
