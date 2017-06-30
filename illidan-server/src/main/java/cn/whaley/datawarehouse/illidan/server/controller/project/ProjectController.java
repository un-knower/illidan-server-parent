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
                projects.get(i).setOwnerId(ownerService.get(Long.parseLong(projects.get(i).getOwnerId())).getOwnerName());
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
            } else {
                projectService.insert(project);
                //azkaban创建project
                JSONObject reseult = createProject(project);
                if("error".equals(reseult.getString("status"))){
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

    /**
     * 创建azkaban project
     * @param project
     * @return
     */
    private JSONObject createProject(Project project){
        AzkabanUtil azkabanUtil = new AzkabanUtil();
        //获取sessionId
        JSONObject sessionIdResult = azkabanUtil.getSeesionId("azkaban", "azkaban@whaley");
        if("error".equals(sessionIdResult.getString("status"))){
            projectService.deleteById(project.getId());
            return sessionIdResult;
        }
        String sessionId = sessionIdResult.getString("status");
        //azkaban创建project
        JSONObject projectResult = azkabanUtil.createProject(sessionId, project.getProjectCode(), project.getProjectDes());

        if("error".equals(projectResult.getString("status"))){
            projectService.deleteById(project.getId());
            return projectResult;
        }
        return projectResult;
    }

    @RequestMapping("toPublishProject")
    @ResponseBody
    public void toPublishProject(Long id) {
        publishProject(id);
    }

    public void publishProject(Long projectId){
        Project project = projectService.get(projectId);
//        project.get
//        ownerService
        String projectCode = project.getProjectCode();
        //删除原来project的文件
        String path = ConfigurationManager.getProperty("zipdir");
        String projectPath = path+File.separator+projectCode;
        File file = new File(projectPath);
        System.out.println("projectPath ... "+projectPath);
        FileUtil.deleteDFile(file);

        List<TaskGroup> taskGroupList = taskGroupService.findTaskGroupByProjectId(projectId);
        System.out.println("taskGroupList ... "+taskGroupList.toString());
        for(TaskGroup taskGroup: taskGroupList){
            Long groupId = taskGroup.getId();
            String email = taskGroup.getEmail();
            String groupCode = taskGroup.getGroupCode();
            String schedule = taskGroup.getSchedule();
            System.out.println("projectCode ... "+projectCode);
            System.out.println("groupCode ... "+groupCode);
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

    }

}
