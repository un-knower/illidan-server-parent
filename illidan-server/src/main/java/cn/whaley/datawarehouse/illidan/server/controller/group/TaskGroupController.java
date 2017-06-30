package cn.whaley.datawarehouse.illidan.server.controller.group;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroupQuery;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.server.util.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

    @RequestMapping("list")
    public ModelAndView list(Long projectId, ModelAndView mav){
        if (projectId != null){
            mav.addObject("projectId",projectId);
            mav.setViewName("group/list");
        }else {
            mav.setViewName("project/list");
        }
        return mav;
    }

    @RequestMapping("groupList")
    public void groupList(Integer start, Integer length, @ModelAttribute("taskGroup") TaskGroupQuery taskGroup) {
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
            outputTemplateJson(taskGroups, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("toAdd")
    public ModelAndView toAdd(ModelAndView mav) {
        List<Project> projectList = getProject();
        mav.addObject("project",projectList);
        mav.setViewName("group/add");
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    public void add(@RequestBody TaskGroup taskGroup) {
        try {
            //状态默认置成有效
            taskGroup.setStatus("1");
            taskGroup.setCreateTime(new Date());
            taskGroup.setUpdateTime(new Date());
            if(StringUtils.isEmpty(taskGroup)){
                returnResult(false, "新增任务组失败!!!");
            } else if(taskGroup.getGroupCode() == null || taskGroup.getGroupCode().equals("")){
                returnResult(false, "任务组code不能为空!!!");
            } else {
                taskGroupService.insert(taskGroup);
                returnResult(true, "新增任务组成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "新增任务组失败: " + e.getMessage());
        }
    }

    @RequestMapping("delete")
    @ResponseBody
    public void delete(String ids) {
        try {
            if(StringUtils.isEmpty(ids)){
                returnResult(false, "请选择要删除的记录");
            }else {
                String[] idArray = ids.split(",");
                List<Long> idList = Arrays.asList(idArray).stream().map(x->Long.parseLong(x)).collect(Collectors.toList());
                taskGroupService.removeByIds(idList);
                logger.error("删除了任务组：" + ids);
                returnResult(true, "删除任务组成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "删除任务组失败:" + e.getMessage());
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
    public void edit(@RequestBody TaskGroup taskGroup) {
        try {
            if(StringUtils.isEmpty(taskGroup)){
                returnResult(false, "修改任务组失败!!!");
            } else {
                taskGroup.setUpdateTime(new Date());
                taskGroupService.updateById(taskGroup);
                returnResult(true, "修改任务组成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "修改任务组失败" + e.getMessage());
        }
    }

    public List<Project> getProject(){
        return projectService.findByProject(new ProjectQuery());
    }
}
