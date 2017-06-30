package cn.whaley.datawarehouse.illidan.engine;

import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import cn.whaley.datawarehouse.illidan.engine.service.SubmitService;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

/**
 * Created by lituo on 2017/6/22.
 */
public class Start {
    public static void main(String[] args) {

        if(args.length % 2 != 0) {
            throw new RuntimeException("参数数量错误, 是" + args.length);
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

//        TaskQuery task = new TaskQuery();
//        TaskService taskService = context.getBean(TaskService.class);
//        System.out.println("task count: " + taskService.findByTask(task).size());

//        String taskCode = "testTask1";
        Long taskId = 1L;
        TaskService taskService = context.getBean(TaskService.class);
//        TaskFull taskFull = taskService.getFullTaskByCode(taskCode);
        TaskFull taskFull = taskService.getFullTask(taskId);
        System.out.println("----"+taskFull);
        System.out.println("++++"+taskFull.getExecuteTypeList());
        System.out.println("===="+taskFull.getTable().getDbInfo().getDbCode());
        System.out.println("****"+taskFull.getTable().getDbInfo().getDbDes());
        System.out.println("####"+taskFull.getTable().getFieldList().toString());

//        Long projectId = 1L;
//        TaskGroupService taskGroupService = context.getBean(TaskGroupService.class);
//        List<TaskGroup> taskGroupList = taskGroupService.findTaskGroupByProjectId(projectId);
//        System.out.println(taskGroupList.toString());
//        System.out.println(taskGroupList.get(0).getGroupCode());
//        System.out.println(taskGroupList.get(1).getGroupCode());


//        HiveService hiveService = context.getBean(HiveService.class);
//        System.out.println(hiveService.queryForCount("select count(1) from default.eagle_live_statistics"));
    }
}
