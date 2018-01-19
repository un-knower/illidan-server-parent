package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.service.authorize.AuthorizeService;
import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    private Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private AuthorizeHttpService authorizeHttpService;
    @Autowired
    private AuthorizeService authorizeService;
    private String projectNodeId = ConfigUtils.get("newillidan.authorize.project_node_id");
    private String tableNodeId = ConfigUtils.get("newillidan.authorize.table_node_id");
    private String sysId = ConfigUtils.get("newillidan.authorize.sys_id");

    public Authorize createProjectAuth(Project project, String createUserName) throws Exception{
        Authorize authorize = new Authorize();
        Long projectId = project.getId();
        String dirName = "project_" + projectId;
        String readDirName = "read_project_" + projectId;
        String writeDirName = "write_project_" + projectId;
        String publishDirName = "publish_project_" + projectId;
        String nodeId = authorizeHttpService.createAuth(projectNodeId, dirName, createUserName);
        String readId = authorizeHttpService.createAuth(nodeId, readDirName, createUserName);
        String writeId = authorizeHttpService.createAuth(nodeId, writeDirName, createUserName);
        String publishId = authorizeHttpService.createAuth(nodeId, publishDirName, createUserName);
        if ("".equals(nodeId) || "".equals(readId) || "".equals(writeId) || "".equals(publishId)) {
            throw new Exception("创建工程权限失败");
        }
        authorize.setParentId(projectId);
        authorize.setNodeId(nodeId);
        authorize.setReadId(readId);
        authorize.setWriteId(writeId);
        authorize.setPublishId(publishId);
        authorize.setType(1L);
        authorizeService.insert(authorize);
        return authorize;
    }

    public Authorize createTableAuth(TableInfo tableInfo, String createUserName) throws Exception {
        Authorize authorize = new Authorize();
        Long tableId = tableInfo.getId();
        logger.info("tableId: "+tableId);
        String dirName = "table_" + tableId;
        String readDirName = "read_table_" + tableId;
        String writeDirName = "write_table_" + tableId;
        String nodeId = authorizeHttpService.createAuth(tableNodeId, dirName, createUserName);
        String readId = authorizeHttpService.createAuth(nodeId, readDirName, createUserName);
        String writeId = authorizeHttpService.createAuth(nodeId, writeDirName, createUserName);
        if ("".equals(nodeId) || "".equals(readId) || "".equals(writeId)) {
            throw new Exception("创建目标表权限失败");
        }
        authorize.setParentId(tableId);
        authorize.setNodeId(nodeId);
        authorize.setReadId(readId);
        authorize.setWriteId(writeId);
        authorize.setType(2L);
        authorizeService.insert(authorize);
        return authorize;
    }

    public List<Project> filterProjectList(List<Project> projects, String userName){
        List<Project> resultProjects = new ArrayList<>();
        //获取project的读权限目录id
        String dir_id = "";
        Authorize authorize1 = new Authorize();
        authorize1.setType(1L);
        List<Authorize> authorizes = authorizeService.findByAuthorize(authorize1);
        if (authorizes != null && authorizes.size() > 0) {
            for (Authorize a : authorizes) {
                String readId = a.getReadId();
                dir_id = dir_id + readId + ",";
            }
        }
        dir_id = dir_id.substring(0, dir_id.length() - 1);
        logger.info("dir_id: " + dir_id);
        //检查当前用户是否有权限查看project
        Map authMap = authorizeHttpService.checkAuth(userName, sysId, dir_id);
        for (Object obj : authMap.keySet()) {
            if (authMap.get(obj).toString().equals("1")) {
                Authorize authorizeQuery = new Authorize();
                authorizeQuery.setReadId(obj.toString());
                Authorize authorize = authorizeService.getByAuthorize(authorizeQuery);
                Long projectId = authorize.getParentId();
                for (int i = projects.size() - 1; i >= 0; i--) {
                    if (projects.get(i).getId().equals(projectId)) {
                        resultProjects.add(projects.get(i));
                    }
                }
            }
        }
        return resultProjects;
    }

    public List<TableInfo> filterTableList(List<TableInfo> tableInfos, String userName){
        List<TableInfo> tableInfoList = new ArrayList<>();
        //获取table的读权限目录id
        String dir_id = "";
        Authorize authorize1 = new Authorize();
        authorize1.setType(2L);
        List<Authorize> authorizes = authorizeService.findByAuthorize(authorize1);
        if (authorizes != null && authorizes.size() > 0) {
            for (Authorize a : authorizes) {
                String readId = a.getReadId();
                dir_id = dir_id + readId + ",";
            }
        }
        dir_id = dir_id.substring(0, dir_id.length() - 1);
        logger.info("dir_id: " + dir_id);
        //检查当前用户是否有权限查看table
        Map tableAuthMap = authorizeHttpService.checkAuth(userName, sysId, dir_id);
        for (Object obj : tableAuthMap.keySet()) {
            if (tableAuthMap.get(obj).toString().equals("1")) {
                Authorize authorizeQuery = new Authorize();
                authorizeQuery.setReadId(obj.toString());
                Authorize authorize = authorizeService.getByAuthorize(authorizeQuery);
                Long tableId = authorize.getParentId();
                for (int i = 0; i<= tableInfos.size() - 1; i++) {
                    if (tableInfos.get(i).getId().equals(tableId)) {
                        tableInfoList.add(tableInfos.get(i));
                    }
                }
            }
        }
        return tableInfoList;
    }

    public boolean hasTablePermission(Long tabletId, String operateType, String userName) {
        return true;
    }

    public boolean hasDbPermission(Long dbId, String operateType, String userName) {
        return true;
    }

    public boolean hasProjectPermission(Long projectId, String operateType, String userName) {
        return true;
    }

    public boolean hasTaskGroupPermission(Long taskGroupId, String operateType, String userName) {
        return true;
    }

    public boolean hasTaskPermission(Long taskId, String operateType, String userName) {
        return true;
    }

}
