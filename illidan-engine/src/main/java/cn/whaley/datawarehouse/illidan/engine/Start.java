package cn.whaley.datawarehouse.illidan.engine;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import cn.whaley.datawarehouse.illidan.common.service.user.UserService;
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

//        UserService userService = context.getBean(UserService.class);
//        System.out.println(userService.findAll().size());

        TaskGroup taskGroup = new TaskGroup();
        TaskGroupService taskGroupService = context.getBean(TaskGroupService.class);
        System.out.println("task group count: " + +taskGroupService.findByTaskGroup(taskGroup).size());

        Task task = new Task();
        TaskService taskService = context.getBean(TaskService.class);
        System.out.println("task count: " + taskService.findByTask(task).size());

        HiveService hiveService = context.getBean(HiveService.class);
        System.out.println(hiveService.queryForCount("select count(1) from default.eagle_live_statistics"));
    }
}
