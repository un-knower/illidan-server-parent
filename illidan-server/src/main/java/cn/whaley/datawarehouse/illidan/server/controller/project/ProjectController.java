package cn.whaley.datawarehouse.illidan.server.controller.project;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.owner.OwnerQuery;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;
import cn.whaley.datawarehouse.illidan.common.service.authorize.AuthorizeService;
import cn.whaley.datawarehouse.illidan.common.service.owner.OwnerService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.server.auth.LoginRequired;
import cn.whaley.datawarehouse.illidan.server.controller.Common;
import cn.whaley.datawarehouse.illidan.server.response.ServerResponse;
import cn.whaley.datawarehouse.illidan.server.service.AuthorizeHttpService;
import cn.whaley.datawarehouse.illidan.server.service.AzkabanService;
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

import javax.servlet.http.HttpSession;
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
    @LoginRequired
    public String list(HttpSession httpSession) {
        return "project/list";
    }

    @RequestMapping("selectProject")
    @LoginRequired
    public String selectproject(HttpSession httpSession) {
        return "project/selectProject";
    }


    @RequestMapping("projectList")
    @LoginRequired
    @ResponseBody
    public ServerResponse projectList(Integer start, Integer length, @ModelAttribute("project") ProjectQuery project, HttpSession httpSession) {
        try {
            List<Project> resultProjects = new ArrayList<>();
            HashMap<String, String> map = new HashMap<String, String>();
            String dir_id = "";
            map.put("1", "已发布");
            map.put("0", "未发布");
            if (project == null) {
                project = new ProjectQuery();
            }
            project.setLimitStart(start);
            project.setPageSize(length);
//            Long count = projectService.countByProject(project);
            List<Project> projects = projectService.findByProject(project);
            for (int i = 0; i <= projects.size() - 1; ++i) {
                projects.get(i).setIsPublish(map.get(projects.get(i).getIsPublish()));
                projects.get(i).setOwnerName(ownerService.get(projects.get(i).getOwnerId()).getOwnerName());
            }
            //获取project的读权限目录id
            Authorize authorize1 = new Authorize();
            authorize1.setType(1L);
            List<Authorize> authorizes = authorizeService.findByAuthorize(authorize1);
            if (authorizes != null && authorizes.size() > 0) {
                for (Authorize a : authorizes) {
                    String readId = a.getReadId();
                    dir_id = dir_id + readId + ",";
                }
            }
            //检查当前用户是否有权限查看project
            dir_id = dir_id.substring(0, dir_id.length() - 1);
            System.out.println("dir_id: " + dir_id);
            // TODO 接入sso后从sso获取
            String uid = "wu.jiulin";
            Map authMap = authorizeHttpService.checkAuth(uid, sys_id, dir_id);
            for (Object obj : authMap.keySet()) {
                System.out.println("key为：" + obj + ", 值为：" + authMap.get(obj));
                if (authMap.get(obj).toString().equals("1")) {
                    System.out.println("====");
                    Authorize authorizeQuery = new Authorize();
                    authorizeQuery.setReadId(obj.toString());
                    Authorize authorize = authorizeService.getByAuthorize(authorizeQuery);
                    Long projectId = authorize.getParentId();
                    System.out.println("projectId: " + projectId);
                    for (int i = projects.size() - 1; i >= 0; i--) {
                        if (projects.get(i).getId().equals(projectId)) {
                            resultProjects.add(projects.get(i));
                        }
                    }
                }
            }

            System.out.println(resultProjects.size());
            Long count = (long) resultProjects.size();
            return ServerResponse.responseBySuccessDataAndCount(resultProjects, count);
//            outputTemplateJson(projects, count);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ServerResponse.responseByError("获取失败：" + e.getMessage());
        }
    }

    @RequestMapping("detail")
    @LoginRequired
    public ModelAndView detail(Long id, ModelMap modelMap, HttpSession httpSession) {
        ModelAndView view = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Project project = projectService.get(id);
            String createTime;
            if (project.getCreateTime() != null) {
                createTime = sdf.format(project.getCreateTime());
            } else {
                createTime = "";
            }
            modelMap.addAttribute("project", project);
            modelMap.addAttribute("createTime", createTime);
            view = new ModelAndView("/project/detail");
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return view;
    }

    @RequestMapping("toEdit")
    @LoginRequired
    public ModelAndView toEdit(Long id, ModelAndView mav, HttpSession httpSession) {
        mav.setViewName("/project/edit");
        Project project = projectService.get(id);
        List<Owner> ownerList = getOwner();
        mav.addObject("project", project);
        mav.addObject("owner", ownerList);
        return mav;
    }

    @RequestMapping("edit")
    @ResponseBody
    @LoginRequired
    public ServerResponse edit(@RequestBody Project project, HttpSession httpSession) {
        try {
            if (StringUtils.isEmpty(project)) {
                logger.error("修改项目失败!!!");
                return ServerResponse.responseByError("修改项目失败!!!");
            } else {
                project.setUpdateTime(new Date());
                projectService.updateById(project);
                logger.info("修改项目成功!!!");
                return ServerResponse.responseBySuccessMessage("修改项目成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError("修改项目失败" + e.getMessage());
        }
    }

    @RequestMapping("toAdd")
    @LoginRequired
    public ModelAndView toAdd(ModelAndView mav, HttpSession httpSession) {
        mav.setViewName("project/add");
        List<Owner> ownerList = getOwner();
        mav.addObject("owner", ownerList);
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    @LoginRequired
    public ServerResponse add(@RequestBody Project project, HttpSession httpSession) {
        try {
            //状态默认置成有效
            project.setStatus("1");
            //发布状态默认置成未发布
            project.setIsPublish("0");
            project.setCreateTime(new Date());
            project.setUpdateTime(new Date());

            if (StringUtils.isEmpty(project)) {
                return ServerResponse.responseByError("新增项目失败!!!");
            } else if (project.getProjectCode() == null || project.getProjectCode().equals("")) {
                return ServerResponse.responseByError("工程code不能为空!!!");
            } else if (!codeReg(project.getProjectCode())) {
                return ServerResponse.responseByError("工程code只能由英文字母,数字,-,_组成!!!");
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
                if ("".equals(node_id) || "".equals(read_id) || "".equals(write_id) || "".equals(publish_id)) {
                    //创建失败，回滚
                    projectService.deleteById(project.getId());
                    return ServerResponse.responseByError("创建project权限失败");
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
                if ("error".equals(reseult.getString("status"))) {
                    //创建失败，回滚
                    projectService.deleteById(project.getId());
                    authorizeService.deleteById(authorize.getId());
                    return ServerResponse.responseByError(reseult.getString("message").replaceAll("'", "\\\\'"));
                }
                logger.info("新增项目成功!!!");
                return ServerResponse.responseBySuccessMessage("新增项目成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError("新增项目失败: " + e.getMessage());
        }
    }

    @RequestMapping("delete")
    @ResponseBody
    @LoginRequired
    public ServerResponse delete(String ids, HttpSession httpSession) {
        try {
//            String ownerId = "1";
            if (StringUtils.isEmpty(ids)) {
                return ServerResponse.responseByError("请选择要删除的记录");
            }
            String[] idArray = ids.split(",");
            List<Long> idList = Arrays.asList(idArray).stream().map(x -> Long.parseLong(x)).collect(Collectors.toList());
            projectService.removeByIds(idList);
            logger.info("删除了项目：" + ids);
            return ServerResponse.responseBySuccessMessage("删除项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError("删除项目失败:" + e.getMessage());
        }
    }

//    @RequestMapping("test")
//    public ModelAndView test(){
//        ModelAndView modelAndView = new ModelAndView("/project/test");
//        return modelAndView;
//    }

    public List<Owner> getOwner() {
        return ownerService.findByOwner(new OwnerQuery());
    }


    @RequestMapping("toPublishProject")
    @ResponseBody
    @LoginRequired
    public ServerResponse toPublishProject(Long id, HttpSession httpSession) {
        JSONObject result = azkabanService.publishProject(id);
        if ("success".equals(result.getString("status"))) {
            logger.info("发布项目成功,项目id: " + id);
            return ServerResponse.responseBySuccessMessage("发布项目成功");
        } else {
            logger.error("发布项目失败," + result.getString("message"));
            return ServerResponse.responseByError(result.getString("message").replaceAll("'", "\\\\'"));
        }
    }

}
