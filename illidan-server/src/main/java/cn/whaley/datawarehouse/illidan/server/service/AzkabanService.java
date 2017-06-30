package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.owner.OwnerService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import cn.whaley.datawarehouse.illidan.server.util.AzkabanUtil;
import cn.whaley.datawarehouse.illidan.server.util.ConfigurationManager;
import cn.whaley.datawarehouse.illidan.server.util.FileUtil;
import cn.whaley.datawarehouse.illidan.server.util.ZipUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hc on 2017/6/30.
 */

@Service
public class AzkabanService {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private TaskGroupService taskGroupService;
    @Autowired
    private TaskService taskService;

    public JSONObject publishProject(Long projectId){
        JSONObject result = new JSONObject();
        try {
            Project project = projectService.get(projectId);
            Owner owner = ownerService.get(project.getOwnerId());
            String projectCode = project.getProjectCode();
            //删除原来project的文件
            String path = ConfigurationManager.getProperty("zipdir");
            String projectPath = path+ File.separator+projectCode;
            File file = new File(projectPath);
            System.out.println("projectPath ... "+projectPath);
            FileUtil.deleteDFile(file);
            List<TaskGroup> taskGroupList = taskGroupService.findTaskGroupByProjectId(projectId);
            for(TaskGroup taskGroup: taskGroupList){
                Long groupId = taskGroup.getId();
                String email = taskGroup.getEmail();
                String groupCode = taskGroup.getGroupCode();
                String schedule = taskGroup.getSchedule();
                //创建工作目录
                FileUtil.createDir(projectPath+File.separator+groupCode);
                //创建结束 end.job
//            FileUtil.createFile(path,projectCode,groupCode,groupCode);
                List<Task> taskList = taskService.findTaskByGroupId(groupId);
                List<String> taskNames = new ArrayList<>();
                for(Task task:taskList){
                    String taskCode = task.getTaskCode();
                    //写入 执行任务.job
                    FileUtil.writeJob(path,projectCode,groupCode,taskCode,email);
                    taskNames.add(taskCode);
                }
                //写入 结束 end.job
                FileUtil.writeEndJob(path,projectCode,groupCode,taskNames);
            }
            //copy properties,submit.sh
            FileUtil.copyFile(path,projectPath,"pro.properties");
            FileUtil.copyFile(path,projectPath,"submit.sh");
            //压缩zip包
            ZipUtils zipUtils = new ZipUtils(projectPath + ".zip");
            zipUtils.compress(projectPath);
            //上传zip
            AzkabanUtil azkabanUtil = new AzkabanUtil();
            //获取sessionId
            JSONObject sessionIdResult = azkabanUtil.getSeesionId(owner.getOwnerName(), owner.getOwnerPassword());
            if("error".equals(sessionIdResult.getString("status"))){
                return sessionIdResult;
            }
            String sessionId = sessionIdResult.getString("message");
            JSONObject uploadResult = azkabanUtil.uploadProject(sessionId, projectCode, projectPath + ".zip");
            if("error".equals(uploadResult.getString("status"))){
                return uploadResult;
            }
            //更改发布状态,设置发布时间
            project.setIsPublish("1");
            project.setPublishTime(new Date());
            projectService.updateById(project);
            //设置调度
            for(TaskGroup taskGroup: taskGroupList){
                String cronExpression = taskGroup.getSchedule();
                String groupCode = taskGroup.getGroupCode();
                if(!"0".equals(cronExpression)){
                    JSONObject scheduleResult = azkabanUtil.setSchedule(sessionId, projectCode, groupCode+"_end", cronExpression);
                    System.out.println("sesss ... "+scheduleResult);
                    if("error".equals(scheduleResult.getString("status"))){
                        return scheduleResult;
                    }
                    //设置scheduleId,更新状态
                    int scheduleId = scheduleResult.getInt("scheduleId");
                    taskGroup.setScheduleId(scheduleId+"");
                    taskGroupService.updateById(taskGroup);
                }
            }
            result.put("status","success");
            result.put("message","success");
        }catch (Exception e){
            result.put("status","error");
            result.put("message",e.getMessage());
        }
        return result ;
    }
}
