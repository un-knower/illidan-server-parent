package cn.whaley.datawarehouse.illidan.server.controller.group;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroupQuery;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.owner.OwnerService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.server.response.ServerResponse;
import cn.whaley.datawarehouse.illidan.server.service.AzkabanService;
import cn.whaley.datawarehouse.illidan.server.controller.Common;
import org.apache.logging.log4j.core.util.CronExpression;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wujiulin on 2017/6/29.
 */

@Controller
@RequestMapping("group")
public class TaskGroupController extends Common {
    private Logger logger = LoggerFactory.getLogger(TaskGroupController.class);

    @Autowired
    private TaskGroupService taskGroupService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private AzkabanService azkabanService;
    @RequestMapping("list")
    public ModelAndView list(Long projectId, ModelAndView mav){
        if (projectId != null){
            mav.addObject("projectId",projectId);
            mav.setViewName("group/list");
        }else {
            mav.addObject("msg","projectId参数不合法");
            mav.setViewName("error");
        }
        return mav;
    }

    @RequestMapping("groupList")
    @ResponseBody
    public ServerResponse groupList(Integer start, Integer length, @ModelAttribute("taskGroup") TaskGroupQuery taskGroup) {
        try {
            if (taskGroup == null) {
                taskGroup = new TaskGroupQuery();
            }

            taskGroup.setLimitStart(start);
            taskGroup.setPageSize(length);
            Long count = taskGroupService.countByTaskGroup(taskGroup);
            List<TaskGroup> taskGroups = taskGroupService.findByTaskGroup(taskGroup);
            for (int i=0;i<=taskGroups.size()-1;++i){
                taskGroups.get(i).setProjectCode(projectService.get(taskGroups.get(i).getProjectId()).getProjectCode());
            }
            return ServerResponse.responseBySuccessDataAndCount(taskGroups, count);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return ServerResponse.responseByError("查询失败:" + e.getMessage());
        }
    }

    @RequestMapping("toAdd")
    public ModelAndView toAdd(ModelAndView mav,Long projectId) {
        List<Project> projectList = getProject();
        mav.addObject("project",projectList);
        mav.addObject("projectId",projectId);
        mav.setViewName("group/add");
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    public ServerResponse add(@RequestBody TaskGroup taskGroup) {
        try {
            //状态默认置成有效
            taskGroup.setStatus("1");
            taskGroup.setCreateTime(new Date());
            taskGroup.setUpdateTime(new Date());
            String result = validTaskGroup(taskGroup);
            if(!result.equals("success")) {
                return ServerResponse.responseByError( result);
            } else {
                taskGroupService.insert(taskGroup);
                logger.info("新增任务组成功!!!");
                return ServerResponse.responseBySuccessMessage( "新增任务组成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError( "新增任务组失败: " + e.getMessage());
        }
    }

    private String validTaskGroup(TaskGroup taskGroup) {
        if(taskGroup == null) {
            return "任务组为空!";
        } else if(taskGroup.getGroupCode() == null || taskGroup.getGroupCode().equals("")){
            return "任务组code不能为空!!!";
        } else if (!codeReg(taskGroup.getGroupCode())){
            return "任务组code只能由英文字母,数字,-,_组成!!!";
        } else if(taskGroup.getSchedule() != null && !taskGroup.getSchedule().isEmpty()
                && !"0".equals(taskGroup.getSchedule())
                && !CronExpression.isValidExpression(taskGroup.getSchedule())) {
            return "cron表达式格式不合法";
        } else {
            return "success";
        }
    }

    @RequestMapping("delete")
    @ResponseBody
    public ServerResponse delete(String ids) {
        try {
            if(StringUtils.isEmpty(ids)){
                return ServerResponse.responseByError( "请选择要删除的记录");
            }else {
                String[] idArray = ids.split(",");
                List<Long> idList = Arrays.asList(idArray).stream().map(x->Long.parseLong(x)).collect(Collectors.toList());
                taskGroupService.removeByIds(idList);
                logger.info("删除了任务组：" + ids);
                return ServerResponse.responseBySuccessMessage( "删除任务组成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError( "删除任务组失败:" + e.getMessage());
        }
    }

    @RequestMapping("toEdit")
    public ModelAndView toEdit(Long id, ModelAndView mav) {
        mav.setViewName("/group/edit");
        TaskGroup taskGroup = taskGroupService.get(id);
        List<Project> projectList = getProject();
        mav.addObject("taskGroup",taskGroup);
        mav.addObject("project",projectList);
        return mav;
    }

    @RequestMapping("edit")
    @ResponseBody
    public ServerResponse edit(@RequestBody TaskGroup taskGroup) {
        try {
            String valid = validTaskGroup(taskGroup);
            if(!valid.equals("success")){
                return ServerResponse.responseByError( valid);
            } else {
                //为了操作azkaban数据异常回退
                TaskGroup oldtaskGroup = taskGroupService.get(taskGroup.getId());
                String cronExpression = taskGroup.getSchedule();
                Project project = projectService.get(oldtaskGroup.getProjectId());
                String isPublish = project.getIsPublish();
                //修改数据库
                taskGroup.setUpdateTime(new Date());
                taskGroupService.updateById(taskGroup);

                if(!"1".equals(isPublish)){
                    //project为未发布，只需改数据库即可
                    return ServerResponse.responseBySuccessMessage( "修改任务组成功!!!");
                }else{
                    if(cronExpression != null && cronExpression.equals(oldtaskGroup.getSchedule())){
                        //cronExpression未修改，只需改数据库即可
                        return ServerResponse.responseBySuccessMessage( "修改任务组成功!!!");
                    }else if(oldtaskGroup.getScheduleId() == null || Integer.parseInt(oldtaskGroup.getScheduleId()) <= 0) {
                        //未设置调度，只需改数据库即可
                        return ServerResponse.responseBySuccessMessage( "修改任务组成功!!!");
                    }
                    else {
                        //cronExpression修改，只需改数据库即可
                        String scheduleId = oldtaskGroup.getScheduleId();
                        JSONObject result = null;
                        //cronExpression修改
                        if(cronExpression == null || cronExpression.isEmpty() || "0".equals(cronExpression)){
                            //取消调度
                            result = azkabanService.deleteSchedule(project.getId(), oldtaskGroup, scheduleId);
                        }else{
                            //修改调度
                            result = azkabanService.setSchedule(project.getId(),oldtaskGroup,cronExpression);
                        }
                        if("success".equals(result.getString("status"))){
                            return ServerResponse.responseBySuccessMessage( "修改任务组成功!!!" );
                        }else{
                            //回退更新数据
                            taskGroupService.updateById(oldtaskGroup);
//                            return ServerResponse.responseBySuccessMessage( "修改任务组成功!!!");
                            return ServerResponse.responseByError( result.getString("message").replaceAll("'","\\\\'") );
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError( "修改任务组失败" + e.getMessage());
        }
    }

    public List<Project> getProject(){
        return projectService.findByProject(new ProjectQuery());
    }
}
