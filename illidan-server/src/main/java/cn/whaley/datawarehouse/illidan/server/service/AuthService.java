package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private String projectNodeId = ConfigUtils.get("newillidan.authorize.project_node_id");
    private String tableNodeId = ConfigUtils.get("newillidan.authorize.table_node_id");
    private String sysId = ConfigUtils.get("newillidan.authorize.sys_id");

    public Authorize createProjectAuth(Project project, String createUserName){
        //project_112
        //read_project_112
        //write_project_112
        //publish_project_112
        return null;
    }

    public Authorize createTableAuth(TableInfo tableInfo, String createUserName){
        return null;
    }

    public List<Project> filterProjectList(List<Project> projects, String userName){
        return null;
    }

    public List<TableInfo> filterTableList(List<TableInfo> tableInfos, String userName){
        return null;
    }

    public boolean hasTablePermission(Long tabletId, String operateType, String userName) {
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
