package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.owner.OwnerService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import cn.whaley.datawarehouse.illidan.server.util.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

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
    @Autowired
    private TableInfoService tableInfoService;
    public JSONObject publishProject(Long projectId){
        JSONObject result = new JSONObject();
        try {
            Project project = projectService.get(projectId);
            Owner owner = ownerService.get(project.getOwnerId());
            String projectCode = project.getProjectCode();
            //删除原来project的文件
            String path = ConfigUtils.get("newillidan.package.zipdir");
            String projectPath = path+ File.separator+projectCode;
            File file = new File(projectPath);
            FileUtil.deleteDFile(file);

            List<TaskGroup> taskGroupList = taskGroupService.findTaskGroupByProjectId(projectId);
            //没有group直接返回
            if(taskGroupList == null || taskGroupList.size() == 0){
                result.put("status","error");
                result.put("message","project '"+projectCode+"' has no groups , please add  ...");
                return result;
            }
            Map<String,String> jobMap = new HashMap<>();
            jobMap.put("projectCode",projectCode);
            jobMap.put("path",path);
            for(TaskGroup taskGroup: taskGroupList){
                Long groupId = taskGroup.getId();
                String email = taskGroup.getEmail();
                String groupCode = taskGroup.getGroupCode();

                jobMap.put("groupCode",groupCode);
                jobMap.put("emails",email);
                List<Task> taskList = taskService.findTaskByGroupId(groupId);
                //group下至少有一个task
                if(taskList == null || taskList.size() == 0){
                    result.put("status","error");
                    result.put("message","group '"+groupCode + "' has no task , please add  or delete...");
                    return result;
                }
                //创建工作目录
                FileUtil.createDir(projectPath+File.separator+groupCode);
                //写文件
                List<String> taskNames = new ArrayList<>();

                for(Task task:taskList){
                    String taskCode = task.getTaskCode();
                    jobMap.put("taskCode",taskCode);
                    jobMap.put("jobType","engine");
                    jobMap.put("executeType", task.getExecuteType());
                    //写入 执行任务.job
//                    FileUtil.writeJob(path,projectCode,groupCode,taskCode,email);
                    FileUtil.writeJob(jobMap);
                    TableWithField hiveTableInfo = tableInfoService.getTableWithField(task.getTableId());
                    //是否需要导出
                    Long mysqlTableId = hiveTableInfo.getMysqlTableId();
                    if(mysqlTableId !=null){
                        //需要导出 写入 export.job
                        taskCode = taskCode+"_export";
                        jobMap.put("taskCode",taskCode);
                        jobMap.put("jobType","export");
                        //1.目标mysql table,db信息
                        TableWithField mysqlTableInfo = tableInfoService.getTableWithField(mysqlTableId);
                        jobMap.put("mysqlTable",mysqlTableInfo.getTableCode());
                        jobMap.put("mysqlDb",mysqlTableInfo.getDbInfo().getDbCode());
                        //2.来源hive table,db信息
                        jobMap.put("hiveTable",hiveTableInfo.getTableCode());
                        jobMap.put("hiveDb",hiveTableInfo.getDbInfo().getDbCode());
                        FileUtil.writeJob(jobMap);
                        taskNames.add(taskCode);
                    }else {
                        taskNames.add(taskCode);
                    }
                }
                //写入 结束 end.job
                FileUtil.write2AzkabanProperties(jobMap);
                FileUtil.writeEndJob(path,projectCode,groupCode,taskNames);
            }
            //copy jar,submit.sh
            String[] zipExtensions = {"jar", "sh"};
            Collection<File> files = FileUtils.listFiles(FileUtils.getFile(path), zipExtensions, false);
            File destination = FileUtils.getFile(projectPath);
            for (File eachFile: files) {
                FileUtils.copyFileToDirectory(eachFile, destination);
            }

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
            project.getOwnerId();
            for(TaskGroup taskGroup: taskGroupList){
                String cronExpression = taskGroup.getSchedule();
                //过滤需要调度的group
                if(cronExpression != null && !cronExpression.isEmpty() && !"0".equals(cronExpression)){
                    JSONObject scheduleResult = setSchedule(projectId, taskGroup, cronExpression);
                    if("error".equals(scheduleResult.getString("status"))){
                        return scheduleResult;
                    }
                    /*JSONObject scheduleResult = azkabanUtil.setSchedule(sessionId, projectCode, groupCode+"_end", cronExpression);
                    if("error".equals(scheduleResult.getString("status"))){
                        return scheduleResult;
                    }
                    //设置scheduleId,更新状态
                    int scheduleId = scheduleResult.getInt("scheduleId");
                    taskGroup.setScheduleId(scheduleId+"");
                    taskGroupService.updateById(taskGroup);*/
                }
            }
            result.put("status","success");
            result.put("message","success");
        }catch (Exception e){
            result.put("status","error");
            result.put("message", "发布azkaban过程中出现异常：" + e.getMessage());
        }
        return result ;
    }

    /**
     * 创建azkaban project
     * @param project
     * @return
     */
    public JSONObject createProject(Project project){
        AzkabanUtil azkabanUtil = new AzkabanUtil();
        //获取sessionId
        JSONObject sessionIdResult = azkabanUtil.getSeesionId("azkaban", "azkaban@whaley");
        if("error".equals(sessionIdResult.getString("status"))){
            return sessionIdResult;
        }
        String sessionId = sessionIdResult.getString("status");
        //azkaban创建project
        JSONObject projectResult = azkabanUtil.createProject(sessionId, project.getProjectCode(), project.getProjectDes());

        if("error".equals(projectResult.getString("status"))){
            return projectResult;
        }
        //添加权限
        JSONObject addPermissionResult = azkabanUtil.addPermission(project.getProjectCode(), project.getOwnerName());
        if("error".equals(addPermissionResult.getString("status"))){
            //azkaban上回滚
            azkabanUtil.deleteProject(sessionId,project.getProjectCode());
            return addPermissionResult;
        }
        return addPermissionResult;
    }

    /**
     * 取消调度并且删除taskGroup中的scheduleId
     * @param scheduleId
     * @return
     */
    public JSONObject deleteSchedule(Long projectId,TaskGroup taskGroup,String scheduleId){
        Project project = projectService.get(projectId);
        Owner owner = ownerService.get(project.getOwnerId());
        AzkabanUtil azkabanUtil = new AzkabanUtil();
        //获取sessionId
        JSONObject sessionIdResult = azkabanUtil.getSeesionId(owner.getOwnerName(), owner.getOwnerPassword());
        if("error".equals(sessionIdResult.getString("status"))){
            return sessionIdResult;
        }
        String sessionId = sessionIdResult.getString("status");
        JSONObject deleteScheduleResult = azkabanUtil.deleteSchedule(sessionId, scheduleId);
        if("error".equals(deleteScheduleResult.getString("status"))){
            return deleteScheduleResult;
        }
        //删除taskGroup中的scheduleId,ScheduleId为-1

        TaskGroup newTaskGroup = new TaskGroup();
        newTaskGroup.setId(taskGroup.getId());
        newTaskGroup.setScheduleId("-1");
        taskGroupService.updateById(newTaskGroup);

        return deleteScheduleResult;
    }

    /**
     * 设置调度并且，更改scheduleId到taskGroup中
     * @param projectId
     * @param taskGroup
     * @param cronExpression
     * @return
     */
    public JSONObject setSchedule(Long projectId,TaskGroup taskGroup,String cronExpression){
        Project project = projectService.get(projectId);
        Owner owner = ownerService.get(project.getOwnerId());
        String projectCode = project.getProjectCode();
        AzkabanUtil azkabanUtil = new AzkabanUtil();
        //获取sessionId
        JSONObject sessionIdResult = azkabanUtil.getSeesionId(owner.getOwnerName(), owner.getOwnerPassword());
        if("error".equals(sessionIdResult.getString("status"))){
            return sessionIdResult;
        }
        String sessionId = sessionIdResult.getString("status");
        String groupCode = taskGroup.getGroupCode();
        JSONObject scheduleResult = azkabanUtil.setSchedule(sessionId, projectCode, groupCode+"_end", cronExpression);
        if("error".equals(scheduleResult.getString("status"))){
            return scheduleResult;
        }
        //设置scheduleId,更新taskGroup中设置scheduleId状态
        int scheduleId = scheduleResult.getInt("scheduleId");

        TaskGroup newTaskGroup = new TaskGroup();
        newTaskGroup.setScheduleId(scheduleId+"");
        newTaskGroup.setId(taskGroup.getId());
        taskGroupService.updateById(newTaskGroup);

        return  scheduleResult;
    }

}
