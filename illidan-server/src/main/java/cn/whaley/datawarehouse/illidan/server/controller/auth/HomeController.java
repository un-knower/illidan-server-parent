package cn.whaley.datawarehouse.illidan.server.controller.auth;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;
import cn.whaley.datawarehouse.illidan.common.enums.AuthorityTypeEnum;
import cn.whaley.datawarehouse.illidan.common.service.authorize.AuthorizeService;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import cn.whaley.datawarehouse.illidan.server.auth.LoginRequired;
import cn.whaley.datawarehouse.illidan.server.auth.UserService;
import cn.whaley.datawarehouse.illidan.server.controller.Common;
import cn.whaley.datawarehouse.illidan.server.response.ServerResponse;
import cn.whaley.datawarehouse.illidan.server.service.AuthService;
import cn.whaley.datawarehouse.illidan.server.service.AuthorizeHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by lituo on 2018/1/12.
 */

@Controller
@RequestMapping("/")
public class HomeController extends Common {
    private Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private DbInfoService dbInfoService;
    @Autowired
    private AuthorizeHttpService authorizeHttpService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ProjectService projectService;


    @RequestMapping(value = "login", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView login(String sso_tn, String url, HttpSession httpSession, ModelAndView mav) {
        Map<String, String> loginUser = getUserFromSession(httpSession);
        Map<String, String> user = UserService.getUserInfo(sso_tn);
        if (url == null || url.trim().length() == 0) {
            url = ConfigUtils.get("newillidan.entranceUrl");
        }
        if (user == null) {
            if (loginUser != null || UserService.skipLogin()) {
                return new ModelAndView(new RedirectView(url));
            }
            mav.addObject("msg", "登陆失败，token无效");
            mav.setViewName("error");
            return mav;
        }
        httpSession.setAttribute("user", user);
        return new ModelAndView(new RedirectView(url));

    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView logout(HttpSession httpSession) {
        httpSession.removeAttribute("user");

        String url = ConfigUtils.get("newillidan.sso.server.url");
        String logoutCallbackUrl = ConfigUtils.get("newillidan.sso.callback.logout");
        url = url + "/user/logout?appkey=bigdata-illidan&cb=" + logoutCallbackUrl;

        return new ModelAndView(new RedirectView(url));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView main(HttpSession httpSession, String url) {
        return new ModelAndView(new RedirectView("project/list"));
    }

    @RequestMapping("createAuth")
    @LoginRequired
    public String createAuth(HttpSession httpSession) {
        return "auth/createAuth";
    }

    @RequestMapping("toCreateDBAuth")
    @ResponseBody
    @LoginRequired
    public ServerResponse toCreateDBAuth(HttpSession httpSession) {
        String dbNodeId = ConfigUtils.get("newillidan.authorize.database_node_id");
        Authorize authorize = new Authorize();
        //获取所有数据库
        DbInfoQuery dbInfoQuery = new DbInfoQuery();
        dbInfoQuery.setStatus("1");
        dbInfoQuery.setStorageId(1L);//hive库
        List<DbInfo> dbInfos = dbInfoService.findByDbInfo(dbInfoQuery);
        //创建权限
        List<Long> existAuths = new ArrayList<>();
        for (DbInfo dbInfo:dbInfos){
            String dirName = "database_" + dbInfo.getId();
            String readDirName = "read_database_" + dbInfo.getId();
            String writeDirName = "write_database_" + dbInfo.getId();
            Authorize authorizeQuery = authorizeService.getByParentId(dbInfo.getId(), AuthorityTypeEnum.DATABASE);
            if (authorizeQuery != null){
                existAuths.add(authorizeQuery.getParentId());
                continue;
            }
            String createUserName = getUserNameFromSession(httpSession);
            String nodeId = authorizeHttpService.createAuth(dbNodeId, dirName, Collections.singletonList(createUserName), null);
            String readId = authorizeHttpService.createAuth(nodeId, readDirName, Collections.singletonList(createUserName), null);
            String writeId = authorizeHttpService.createAuth(nodeId, writeDirName, Collections.singletonList(createUserName), null);
            if ("".equals(nodeId) || "".equals(readId) || "".equals(writeId)) {
                return ServerResponse.responseByError( "初始化数据库权限失败!!!");
            }
            authorize.setParentId(dbInfo.getId());
            authorize.setNodeId(nodeId);
            authorize.setReadId(readId);
            authorize.setWriteId(writeId);
            authorize.setType(AuthorityTypeEnum.PROJECT.getCode());
            authorizeService.insert(authorize);
        }
        if (existAuths.size() > 0){
            return ServerResponse.responseByError("权限已经存在!!!" + existAuths);
        }
        return ServerResponse.responseBySuccessMessage("初始化数据库权限成功!!!");
    }

    @RequestMapping("toCreateProjectAuth")
    @ResponseBody
    @LoginRequired
    public ServerResponse toCreateProjectAuth(HttpSession httpSession) {

        ProjectQuery projectQuery = new ProjectQuery();
        projectQuery.setStatus("1");
        List<Project> projects = projectService.findByProject(projectQuery);
        try{
            for (Project project:projects){
                Authorize authorize = authService.createProjectAuth(project, getUserNameFromSession(httpSession));
                if (authorize.getNodeId() == null) {
                    return ServerResponse.responseByError("初始化工程权限失败");
                }
            }
            return ServerResponse.responseBySuccessMessage("初始化工程权限成功!!!");
        } catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError("初始化工程权限失败: " + e.getMessage());
        }

    }

}
