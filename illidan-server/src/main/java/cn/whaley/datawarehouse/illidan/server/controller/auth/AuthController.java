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
import cn.whaley.datawarehouse.illidan.server.controller.Common;
import cn.whaley.datawarehouse.illidan.server.response.ServerResponse;
import cn.whaley.datawarehouse.illidan.server.service.AuthService;
import cn.whaley.datawarehouse.illidan.server.service.AuthorizeHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by lituo on 2018/1/23.
 */

@Controller
@RequestMapping("/")
public class AuthController  extends Common {

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
//        dbInfoQuery.setStorageId(1L);//hive库
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
            List<String> createUserNames = Collections.singletonList(createUserName);
            List<String> adminGroups = Collections.singletonList(ConfigUtils.get("newillidan.authorize.admin.group"));
            String nodeId = authorizeHttpService.createAuth(dbNodeId, dirName, createUserNames, adminGroups);
            String readId = authorizeHttpService.createAuth(nodeId, readDirName, createUserNames, adminGroups);
            String writeId = authorizeHttpService.createAuth(nodeId, writeDirName, createUserNames, adminGroups);
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
            return ServerResponse.responseByError("权限已经存在!!!" + Arrays.toString(existAuths.toArray()));
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

    @RequestMapping("createProjectAuth")
    @ResponseBody
    public ServerResponse createProjectAuth(Long projectId, String user) {

        Project project = projectService.get(projectId);
        if(project == null || user == null || user.trim().length() == 0) {
            return ServerResponse.responseByError("参数不合法");
        }
        try{
            Authorize authorize = authService.createProjectAuth(project, user);
            if (authorize.getNodeId() == null) {
                return ServerResponse.responseByError("初始化工程权限失败");
            }
            return ServerResponse.responseBySuccessMessage("初始化工程权限成功!!!");
        } catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError("初始化工程权限失败: " + e.getMessage());
        }

    }
}
