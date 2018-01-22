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
import cn.whaley.datawarehouse.illidan.server.service.AuthService;
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
    private AuthorizeService authorizeService;
    @Autowired
    private AuthService authService;

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
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("1", "已发布");
            map.put("0", "未发布");
            if (project == null) {
                project = new ProjectQuery();
            }
//            project.setLimitStart(start);
//            project.setPageSize(length);
//            Long count = projectService.countByProject(project);
            //获取所有owner
            Map<Long,String> ownerMap = new HashMap<>();
            OwnerQuery ownerQuery = new OwnerQuery();
            ownerQuery.setStatus("1");
            List<Owner> owners = ownerService.findByOwner(ownerQuery);
            for (Owner owner:owners){
                ownerMap.put(owner.getId(), owner.getOwnerName());
            }
            //赋值
            List<Project> projects = projectService.findByProject(project);
            for (int i = 0; i <= projects.size() - 1; ++i) {
                projects.get(i).setIsPublish(map.get(projects.get(i).getIsPublish()));
                projects.get(i).setOwnerName(ownerMap.get(projects.get(i).getOwnerId()));
            }
            logger.info("userName: "+getUserNameFromSession(httpSession));
            List<Project> resultProjects = authService.filterProjectList(projects, getUserNameFromSession(httpSession));
            Long count = (long) resultProjects.size();
            logger.info("total num: "+count);
            //分页显示
            List<Project> result = resultProjects.subList(start, (int) (count - start > length ? start + length : count));
            return ServerResponse.responseBySuccessDataAndCount(result, count);
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
            String userName = getUserNameFromSession(httpSession);
            if(!authService.hasProjectPermission(id, "read", userName)) {
                view = new ModelAndView("error");
                view.addObject("msg", "没有查看权限");
                return view;
            }
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
        String userName = getUserNameFromSession(httpSession);
        if(!authService.hasProjectPermission(id, "read", userName)) {
            mav.setViewName("error");
            mav.addObject("msg", "没有查看权限");
            return mav;
        }
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
            if (project == null || project.getId() == null) {
                return ServerResponse.responseByError("编辑失败，参数不合法");
            }
            Long id = project.getId();
            String userName = getUserNameFromSession(httpSession);
            if(!authService.hasProjectPermission(id, "write", userName)) {
                return ServerResponse.responseByError(403, "编辑失败，缺少工程写权限");
            }
            project.setUpdateTime(new Date());
            projectService.updateById(project);
            logger.info("修改项目成功!!!");
            return ServerResponse.responseBySuccessMessage("修改项目成功!!!");

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
            //验证创建权限
            String userName = getUserNameFromSession(httpSession);
            if(!authService.hasCreateProjectPermission(userName)) {
                return ServerResponse.responseByError(403, "缺少创建工程权限，请联系系统管理员");
            }
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
                logger.info("userName: "+getUserNameFromSession(httpSession));
                //创建权限
                Authorize authorize = authService.createProjectAuth(project, getUserNameFromSession(httpSession));
                if (authorize.getNodeId() == null) {
                    //创建失败，回滚
                    projectService.deleteById(project.getId());
                    return ServerResponse.responseByError("创建project权限失败");
                }
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
            if(idList.size() > 1) {
                return ServerResponse.responseByError("不能同时操作多个工程");

            }
            Long id = idList.get(0);
            String userName = getUserNameFromSession(httpSession);
            if(!authService.hasProjectPermission(id, "write", userName)) {
                return ServerResponse.responseByError(403, "编辑失败，缺少工程写权限");
            }
            projectService.removeByIds(idList);
            logger.info("删除了项目：" + ids);
            return ServerResponse.responseBySuccessMessage("删除项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError("删除项目失败:" + e.getMessage());
        }
    }


    public List<Owner> getOwner() {
        return ownerService.findByOwner(new OwnerQuery());
    }


    @RequestMapping("toPublishProject")
    @ResponseBody
    @LoginRequired
    public ServerResponse toPublishProject(Long id, HttpSession httpSession) {
        String userName = getUserNameFromSession(httpSession);
        if(!authService.hasProjectPermission(id, "publish", userName)) {
            return ServerResponse.responseByError(403, "发布失败，缺少工程发布权限");
        }
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
