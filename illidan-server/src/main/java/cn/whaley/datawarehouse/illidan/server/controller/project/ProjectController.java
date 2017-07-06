package cn.whaley.datawarehouse.illidan.server.controller.project;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.owner.OwnerQuery;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.owner.OwnerService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import cn.whaley.datawarehouse.illidan.server.service.AzkabanService;
import cn.whaley.datawarehouse.illidan.server.util.*;
import org.json.JSONObject;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
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
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private TaskGroupService taskGroupService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private AzkabanService azkabanService;


    @RequestMapping("list")
    public String list(){
        return "project/list";
    }

    @RequestMapping("selectProject")
    public String selectproject(){
        return "project/selectProject";
    }


    @RequestMapping("projectList")
    public void projectList(Integer start, Integer length, @ModelAttribute("project") ProjectQuery project) {
        try {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("1", "已发布");
            map.put("0", "未发布");
            if (project == null) {
                project = new ProjectQuery();
            }
            project.setLimitStart(start);
            project.setPageSize(length);
            Long count = projectService.countByProject(project);
            List<Project> projects = projectService.findByProject(project);
            for (int i=0;i<=projects.size()-1;++i){
                projects.get(i).setIsPublish(map.get(projects.get(i).getIsPublish()));
                projects.get(i).setOwnerName(ownerService.get(projects.get(i).getOwnerId()).getOwnerName());
            }
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
    public ModelAndView toEdit(Long id, ModelAndView mav) {
        mav.setViewName("/project/edit");
        Project project = projectService.get(id);
        List<Owner> ownerList = getOwner();
        mav.addObject("project",project);
        mav.addObject("owner",ownerList);
        return mav;
    }

    @RequestMapping("edit")
    @ResponseBody
    public void edit(@RequestBody Project project) {
        try {
            if(StringUtils.isEmpty(project)){
                returnResult(false, "修改项目失败!!!");
            } else {
                project.setUpdateTime(new Date());
                projectService.updateById(project);
                returnResult(true, "修改项目成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "修改项目失败" + e.getMessage());
        }
    }

    @RequestMapping("toAdd")
    public ModelAndView toAdd(ModelAndView mav) {
        mav.setViewName("project/add");
        List<Owner> ownerList = getOwner();
        mav.addObject("owner",ownerList);
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    public void add(@RequestBody Project project) {
        try {
            //状态默认置成有效
            project.setStatus("1");
            //发布状态默认置成未发布
            project.setIsPublish("0");
            project.setCreateTime(new Date());
            project.setUpdateTime(new Date());

            if(StringUtils.isEmpty(project)){
                returnResult(false, "新增项目失败!!!");
            } else if(project.getProjectCode() == null || project.getProjectCode().equals("")){
                returnResult(false, "工程code不能为空!!!");
            } else if(!codeReg(project.getProjectCode())){
                returnResult(false, "工程code只能由英文字母,数字,-,_组成!!!");
            } else {
                projectService.insert(project);
                //azkaban创建project
                Owner owner = ownerService.get(project.getOwnerId());
                project.setOwnerName(owner.getOwnerName());
                JSONObject reseult = azkabanService.createProject(project);
                if("error".equals(reseult.getString("status"))){
                    //创建失败，回滚
                    projectService.deleteById(project.getId());
                    returnResult(false, reseult.getString("message").replaceAll("'","\\\\'"));
                }
                returnResult(true, "新增项目成功!!!");
            }
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
//            String ownerId = "1";
            if(StringUtils.isEmpty(ids)){
                returnResult(false, "请选择要删除的记录");
            }
            String[] idArray = ids.split(",");
            List<Long> idList = Arrays.asList(idArray).stream().map(x->Long.parseLong(x)).collect(Collectors.toList());
            projectService.removeByIds(idList);
            logger.error("删除了项目：" + ids);
            returnResult(true, "删除项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "删除项目失败:" + e.getMessage());
        }
    }

//    @RequestMapping("test")
//    public ModelAndView test(){
//        ModelAndView modelAndView = new ModelAndView("/project/test");
//        return modelAndView;
//    }

    public List<Owner> getOwner(){
        return ownerService.findByOwner(new OwnerQuery());
    }



    @RequestMapping("toPublishProject")
    @ResponseBody
    public void toPublishProject(Long id) {
        JSONObject result = azkabanService.publishProject(id);
        if("success".equals(result.getString("status"))){
            returnResult(true, "发布项目成功");
        }else{
            returnResult(false,result.getString("message").replaceAll("'","\\\\'"));
        }
    }

}
