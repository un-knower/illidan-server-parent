package cn.whaley.datawarehouse.illidan.server.controller.project;

import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wujiulin on 17/1/16.
 */
@Controller
@RequestMapping("project")
public class ProjectController extends Common {
    private Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @RequestMapping("list")
    public String list(){
        return "project/list";
    }

    @RequestMapping("selectProject")
    public String selectproject(){
        return "project/selectProject";
    }


    @RequestMapping("projectList")
    public void projectList(Integer start, Integer length, @ModelAttribute("project") Project project) {
        try {
            if (project == null) {
                project = new Project();
            }
            project.setLimitStart(start);
            project.setPageSize(length);
            Long count = projectService.countByProject(project);
            List<Project> projects = projectService.findByProject(project);
            outputTemplateJson(projects, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("detail")
    public ModelAndView detail(Long id, ModelMap modelMap){
        ModelAndView view = null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Project project = projectService.get(id);
            String createTime;
            if (project.getCreateTime() != null){
                createTime = sdf.format(project.getCreateTime());
            }else {
                createTime = "";
            }
            modelMap.addAttribute("project",project);
            modelMap.addAttribute("createTime",createTime);
            view = new ModelAndView("/project/detail");
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @RequestMapping("toEdit")
    public ModelAndView toEdit(Long id, ModelMap modelMap) {
        Project project = projectService.get(id);
        modelMap.addAttribute("project", project);
        ModelAndView modelAndView = new ModelAndView("/project/edit");
        return modelAndView;
    }

    @RequestMapping("edit")
    @ResponseBody
    public void edit(@RequestBody Project project) {
        try {
            if(StringUtils.isEmpty(project)){
                returnResult(false, "修改项目失败!!!");
            }
            projectService.updateById(project);
            returnResult(true, "修改项目成功!!!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "修改项目失败" + e.getMessage());
        }
    }

    @RequestMapping("toAdd")
    public ModelAndView toAdd(ModelAndView mav) {
        mav.setViewName("project/add");
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    public void add(@RequestBody Project project) {
        try {
            project.setOwnerId("1");
            project.setCreateTime(new Date());
            project.setUpdateTime(new Date());
            if(StringUtils.isEmpty(project)){
                returnResult(false, "新增项目失败!!!");
            }
            projectService.insert(project);
            returnResult(true, "新增项目成功!!!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "新增项目失败: " + e.getMessage());
        }
    }

    @RequestMapping("delete")
    @ResponseBody
    public void delete(String ids) {
        try {
            String ownerId = "1";
            if(StringUtils.isEmpty(ids)){
                returnResult(false, "请选择要删除的记录");
            }
            String[] idArray = ids.split(",");
            List<Long> idList = Arrays.asList(idArray).stream().map(x->Long.parseLong(x)).collect(Collectors.toList());
            projectService.removeByIds(idList);
            logger.error(ownerId + "删除了项目：" + ids);
            returnResult(true, "删除项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "删除项目失败:" + e.getMessage());
        }
    }

}
