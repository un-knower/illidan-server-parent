package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.enums.AuthorityTypeEnum;
import cn.whaley.datawarehouse.illidan.common.service.authorize.AuthorizeService;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
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
    @Autowired
    private TableInfoService tableInfoService;
    @Autowired
    private TaskGroupService taskGroupService;
    @Autowired
    private TaskService taskService;

    public Authorize createProjectAuth(Project project, String createUserName) throws Exception {
        Authorize authorize = new Authorize();
        Long projectId = project.getId();
        String dirName = "project_" + projectId;
        String readDirName = "read_project_" + projectId;
        String writeDirName = "write_project_" + projectId;
        String publishDirName = "publish_project_" + projectId;
        String projectNodeId = ConfigUtils.get("newillidan.authorize.project_node_id");
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
        authorize.setType(AuthorityTypeEnum.PROJECT.getCode());
        authorizeService.insert(authorize);
        return authorize;
    }

    public Authorize createTableAuth(TableInfo tableInfo, String createUserName) throws Exception {
        Authorize authorize = new Authorize();
        Long tableId = tableInfo.getId();
        logger.info("tableId: " + tableId);
        String dirName = "table_" + tableId;
        String readDirName = "read_table_" + tableId;
        String writeDirName = "write_table_" + tableId;
        String tableNodeId = ConfigUtils.get("newillidan.authorize.table_node_id");
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
        authorize.setType(AuthorityTypeEnum.TABLE.getCode());
        authorizeService.insert(authorize);
        return authorize;
    }

    public List<Project> filterProjectList(List<Project> projects, String userName) {
        List<Project> resultProjects = new ArrayList<>();
        //获取project的读权限目录id
        List<String> dir_ids = new ArrayList<>();
        Authorize authorize1 = new Authorize();
        authorize1.setType(AuthorityTypeEnum.PROJECT.getCode());
        List<Authorize> authorizes = authorizeService.findByAuthorize(authorize1);
        if (authorizes != null && authorizes.size() > 0) {
            for (Authorize a : authorizes) {
                String readId = a.getReadId();
                dir_ids.add(readId);
            }
        }
        //检查当前用户是否有权限查看project
        String sysId = ConfigUtils.get("newillidan.authorize.sys_id");
        Map authMap = authorizeHttpService.checkAuth(userName, sysId, dir_ids);
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

    public List<TableInfo> filterTableList(List<TableInfo> tableInfos, String userName) {
        List<TableInfo> tableInfoList = new ArrayList<>();
        //获取table的读权限目录id
        List<String> dir_ids = new ArrayList<>();
        Authorize authorize1 = new Authorize();
        authorize1.setType(AuthorityTypeEnum.TABLE.getCode());
        List<Authorize> authorizes = authorizeService.findByAuthorize(authorize1);
        if (authorizes != null && authorizes.size() > 0) {
            for (Authorize a : authorizes) {
                String readId = a.getReadId();
                dir_ids.add(readId);
            }
        }
        //检查当前用户是否有权限查看table
        String sysId = ConfigUtils.get("newillidan.authorize.sys_id");
        Map tableAuthMap = authorizeHttpService.checkAuth(userName, sysId, dir_ids);
        for (Object obj : tableAuthMap.keySet()) {
            if (tableAuthMap.get(obj).toString().equals("1")) {
                Authorize authorizeQuery = new Authorize();
                authorizeQuery.setReadId(obj.toString());
                Authorize authorize = authorizeService.getByAuthorize(authorizeQuery);
                Long tableId = authorize.getParentId();
                for (int i = 0; i <= tableInfos.size() - 1; i++) {
                    if (tableInfos.get(i).getId().equals(tableId)) {
                        tableInfoList.add(tableInfos.get(i));
                    }
                }
            }
        }
        return tableInfoList;
    }

    public List<Long> filterTableListByDb(String userName) {
        List<TableInfo> tableInfoList = new ArrayList<>();
        List<Long> dbIds = new ArrayList<>();
        List<String> dir_ids = new ArrayList<>();
        Authorize authorize1 = new Authorize();
        authorize1.setType(AuthorityTypeEnum.DATABASE.getCode());
        List<Authorize> authorizes = authorizeService.findByAuthorize(authorize1);
        if (authorizes != null && authorizes.size() > 0) {
            for (Authorize a : authorizes) {
                String readId = a.getReadId();
                dir_ids.add(readId);
            }
        }
        String sysId = ConfigUtils.get("newillidan.authorize.sys_id");
        Map dbAuthMap = authorizeHttpService.checkAuth(userName, sysId, dir_ids);
        for (Object obj : dbAuthMap.keySet()) {
            if (dbAuthMap.get(obj).toString().equals("1")) {
                Authorize authorizeQuery = new Authorize();
                authorizeQuery.setReadId(obj.toString());
                Authorize authorize = authorizeService.getByAuthorize(authorizeQuery);
                Long dbId = authorize.getParentId();
                dbIds.add(dbId);
            }
        }
        System.out.println("dbIds: " + dbIds);
        return dbIds;
    }

    public boolean hasTablePermission(Long tabletId, String operateType, String userName) {
        try {
            TableInfo tableInfo = tableInfoService.get(tabletId);
            if (tableInfo == null || tableInfo.getDbId() == null) {
                return false;
            }
            return hasDbPermission(tableInfo.getDbId(), operateType, userName);
        } catch (Exception e) {
            logger.warn("查询数据表权限异常", e);
            return false;
        }
    }

    public boolean hasDbPermission(Long dbId, String operateType, String userName) {
        try {
            Authorize authorize = authorizeService.getByParentId(dbId, AuthorityTypeEnum.DATABASE);
            String nodeId;
            if ("read".equals(operateType)) {
                nodeId = authorize.getReadId();
            } else if ("write".equals(operateType)) {
                nodeId = authorize.getWriteId();
            } else {
                nodeId = null;
            }
            String sysId = ConfigUtils.get("newillidan.authorize.sys_id");
            return authorizeHttpService.checkAuth(userName, sysId, nodeId);
        } catch (Exception e) {
            logger.warn("查询数据库权限异常", e);
            return false;
        }
    }

    public boolean hasProjectPermission(Long projectId, String operateType, String userName) {
        try {
            Authorize authorize = authorizeService.getByParentId(projectId, AuthorityTypeEnum.PROJECT);
            String nodeId;
            if ("read".equals(operateType)) {
                nodeId = authorize.getReadId();
            } else if ("write".equals(operateType)) {
                nodeId = authorize.getWriteId();
            } else if ("publish".equals(operateType)) {
                nodeId = authorize.getPublishId();
            } else {
                nodeId = null;
            }
            String sysId = ConfigUtils.get("newillidan.authorize.sys_id");
            return authorizeHttpService.checkAuth(userName, sysId, nodeId);
        } catch (Exception e) {
            logger.warn("查询工程权限异常", e);
            return false;
        }
    }

    public boolean hasTaskGroupPermission(Long taskGroupId, String operateType, String userName) {
        try {
            TaskGroup taskGroup = taskGroupService.get(taskGroupId);
            if (taskGroup == null || taskGroup.getProjectId() == null) {
                return false;
            }
            return hasProjectPermission(taskGroup.getProjectId(), operateType, userName);
        } catch (Exception e) {
            logger.warn("查询任务组权限异常", e);
            return false;
        }
    }

    public boolean hasTaskPermission(Long taskId, String operateType, String userName) {
        try {
            Task task = taskService.get(taskId);
            if (task == null || task.getGroupId() == null) {
                return false;
            }
            return hasTaskGroupPermission(task.getGroupId(), operateType, userName);
        } catch (Exception e) {
            logger.warn("查询任务权限异常", e);
            return false;
        }
    }

    public boolean hasCreateProjectPermission(String userName) {
        try {
            String node = ConfigUtils.get("newillidan.authorize.create_project_node_id");
            String sysId = ConfigUtils.get("newillidan.authorize.sys_id");
            return authorizeHttpService.checkAuth(userName, sysId, node);
        } catch (Exception e) {
            logger.warn("查询查询工程权限异常", e);
            return false;
        }
    }

}
