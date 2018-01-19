package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoQuery;
import cn.whaley.datawarehouse.illidan.common.service.authorize.AuthorizeService;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:spring/application-illidan-*.xml"})
public class CreateDbAuth {
    private String dbNodeId = ConfigUtils.get("newillidan.authorize.database_node_id");
    @Autowired
    private DbInfoService dbInfoService;
    @Autowired
    private AuthorizeHttpService authorizeHttpService;
    @Autowired
    private AuthorizeService authorizeService;

    @Test
    public void createDBAuth(){
        Authorize authorize = new Authorize();
        //获取所有数据库
        DbInfoQuery dbInfoQuery = new DbInfoQuery();
        dbInfoQuery.setStatus("1");
        dbInfoQuery.setStorageId(1L);//hive库

        List<DbInfo> dbInfos = dbInfoService.findByDbInfo(dbInfoQuery);
        //创建权限
        for (DbInfo dbInfo:dbInfos){
            String dirName = "database_" + dbInfo.getId();
            String readDirName = "read_database_" + dbInfo.getId();
            String writeDirName = "write_database_" + dbInfo.getId();
            String nodeId = authorizeHttpService.createAuth(dbNodeId, dirName, "wu.jiulin");
            String readId = authorizeHttpService.createAuth(nodeId, readDirName, "wu.jiulin");
            String writeId = authorizeHttpService.createAuth(nodeId, writeDirName, "wu.jiulin");
            if ("".equals(nodeId) || "".equals(readId) || "".equals(writeId)) {
                System.out.println("创建工程权限失败");

            }
            authorize.setParentId(dbInfo.getId());
            authorize.setNodeId(nodeId);
            authorize.setReadId(readId);
            authorize.setWriteId(writeId);
            authorize.setType(3L);
            authorizeService.insert(authorize);
        }

    }
}
