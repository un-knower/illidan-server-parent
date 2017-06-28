package cn.whaley.datawarehouse.illidan.engine;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroupQuery;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;
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

//        TaskGroupQuery taskGroup = new TaskGroupQuery();
//        TaskGroupService taskGroupService = context.getBean(TaskGroupService.class);
//        System.out.println("task group count: " + +taskGroupService.findByTaskGroup(taskGroup).size());

//        TaskQuery task = new TaskQuery();
//        TaskService taskService = context.getBean(TaskService.class);
//        System.out.println("task count: " + taskService.findByTask(task).size());

        String taskCode = "testTask1";
        Long taskId = 1L;
        TaskService taskService = context.getBean(TaskService.class);
//        TaskFull taskFull = taskService.getFullTaskByCode(taskCode);
        TaskFull taskFull = taskService.getFullTask(taskId);
        System.out.println("----"+taskFull);
        System.out.println("++++"+taskFull.getExecuteTypeList());
        System.out.println("===="+taskFull.getTable().getDbInfo().getDbCode());
        System.out.println("****"+taskFull.getTable().getDbInfo().getDbDes());
        System.out.println("####"+taskFull.getTable().getFieldList().toString());

//        HiveService hiveService = context.getBean(HiveService.class);
//        System.out.println(hiveService.queryForCount("select count(1) from default.eagle_live_statistics"));
    }
}
