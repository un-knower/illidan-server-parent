package cn.whaley.datawarehouse.illidan.server.controller.project;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.owner.OwnerQuery;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;
import cn.whaley.datawarehouse.illidan.common.service.authorize.AuthorizeService;
import cn.whaley.datawarehouse.illidan.common.service.owner.OwnerService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.server.service.AuthorizeHttpService;
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
    private AzkabanService azkabanService;
    @Autowired
    private AuthorizeHttpService authorizeHttpService;
    @Autowired
    private AuthorizeService authorizeService;
    // TODO 后续改成从lion读取
    private String project_node_id = "7_35";
    private String sys_id = "7";
//    private String project_node_id = ConfigUtils.get("newillidan.authorize.project_node_id");
//    private String sys_id = ConfigUtils.get("newillidan.authorize.sys_id");

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
            List<Project> resultProjects = new ArrayList<>();
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
                Long projectId = projects.get(i).getId();
                Authorize authorize = new Authorize();
                authorize.setParentId(projectId);
                List<Authorize> authorizes = authorizeService.findByAuthorize(authorize);
            }
            outputTemplateJson(projects, count);
        } catch (Exception e) {
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
                logger.error("修改项目失败!!!");
                returnResult(false, "修改项目失败!!!");
            } else {
                project.setUpdateTime(new Date());
                projectService.updateById(project);
                logger.info("修改项目成功!!!");
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
                //创建权限
                String dir_name = project.getProjectCode();
                // TODO 接入sso后group_id和uid从sso获取
                String group_id = "3";
                String uid = "wu.jiulin";
                String node_id = authorizeHttpService.createDir(project_node_id, dir_name, group_id, uid);
                String read_id = authorizeHttpService.createDir(node_id, "read", group_id, uid);
                String write_id = authorizeHttpService.createDir(node_id, "write", group_id, uid);
                String publish_id = authorizeHttpService.createDir(node_id, "publish", group_id, uid);
                if ("".equals(node_id) || "".equals(read_id) || "".equals(write_id) || "".equals(publish_id)){
                    //创建失败，回滚
                    projectService.deleteById(project.getId());
                    returnResult(false, "创建project权限失败");
                }
                Authorize authorize = new Authorize();
                authorize.setParentId(project.getId());
                authorize.setNodeId(node_id);
                authorize.setReadId(read_id);
                authorize.setWriteId(write_id);
                authorize.setPublishId(publish_id);
                authorize.setType(1L);//project
                authorizeService.insert(authorize);
                //azkaban创建project
                Owner owner = ownerService.get(project.getOwnerId());
                project.setOwnerName(owner.getOwnerName());
                JSONObject reseult = azkabanService.createProject(project);
                if("error".equals(reseult.getString("status"))){
                    //创建失败，回滚
                    projectService.deleteById(project.getId());
                    authorizeService.deleteById(authorize.getId());
                    returnResult(false, reseult.getString("message").replaceAll("'","\\\\'"));
                }
                logger.info("新增项目成功!!!");
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
            logger.info("删除了项目：" + ids);
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
            logger.info("发布项目成功,项目id: "+id);
            returnResult(true, "发布项目成功");
        }else{
            logger.error("发布项目失败,"+result.getString("message"));
            returnResult(false,result.getString("message").replaceAll("'","\\\\'"));
        }
    }

}
