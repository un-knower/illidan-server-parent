package cn.whaley.datawarehouse.illidan.common.service.task;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.db.DbInfoMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.field.FieldInfoMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.table.TableInfoMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.task.TaskMapper;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoServiceImpl;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoServiceImpl;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.common.service.storage.StorageInfoService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wujiulin on 2017/6/26.
 */

@Service
public class TaskServiceImpl implements TaskService {
    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TableInfoMapper tableInfoMapper;
    @Autowired
    private DbInfoMapper dbInfoMapper;
    @Autowired
    private FieldInfoMapper fieldInfoMapper;
    @Autowired
    private TableInfoService tableInfoService;
    @Autowired
    private FieldInfoService fieldInfoService;
    @Autowired
    private StorageInfoService storageInfoService;
    @Autowired
    private DbInfoService dbInfoService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskGroupService taskGroupService;

    public Task get(final Long id) {
        if (id == null){
            logger.error("get: id is null.");
            return null;
        }
        return taskMapper.get(id);
    }

    public Long insert(final Task task) throws Exception{
        if (task == null){
            logger.error("insert: task is null.");
            return null;
        }
        List<Long> groupIdList = getAllGroupId(task);
        if (groupIdList == null || groupIdList.size()<=0){
            logger.error("insert: groupIdList is null. task: "+task.toString());
            return null;
        }
//        Long count = taskMapper.isExistTaskInProject(groupIdList,task.getTaskCode(),task.getStatus());
        Long count = taskMapper.isExistTask(task.getTaskCode(),task.getStatus());
        if (count == null){
            logger.error("insert: count is null. task: "+task.toString());
            return null;
        }
        if(count > 0){
            throw new Exception("任务已经存在不能重复新增");
        }
        return taskMapper.insert(task);
    }

    public Long insertBatch(final List<Task> list) {
        if (list == null || list.size()<=0){
            logger.error("insertBatch: list is null.");
            return null;
        }
        return taskMapper.insertBatch(list);
    }

    public Long update(final Map<String, Object> params) {
        if (params == null){
            logger.error("update: params is null.");
            return null;
        }
        return taskMapper.update(params);
    }

    public Long remove(final Map<String, Object> params) {
        if (params == null){
            logger.error("remove: params is null.");
            return null;
        }
        return taskMapper.remove(params);
    }

    public List<Task> find(final TaskQuery task) {
        if (task == null){
            logger.error("find: task is null.");
            return null;
        }
        return taskMapper.find(task);
    }

    public Long count(final Map<String, String> params) {
        if (params == null){
            logger.error("count: params is null.");
            return null;
        }
        return taskMapper.count(params);
    }

    public Long countByTask(final TaskQuery task) {
        if (task == null){
            logger.error("countByTask: task is null.");
            return null;
        }
        return taskMapper.countByTask(task);
    }

    public Long updateByTask(final Task task) {
        if (task == null){
            logger.error("updateByTask: task is null.");
            return null;
        }
        return taskMapper.updateByTask(task);
    }

    public Long updateById(final Task task) {
        if (task == null){
            logger.error("updateById: task is null.");
            return null;
        }
        return taskMapper.updateById(task);
    }

    public Long removeByTask(final TaskQuery task) {
        if (task == null){
            logger.error("removeByTask: task is null.");
            return null;
        }
        return taskMapper.removeByTask(task);
    }

    public List<Task> findByTask(final TaskQuery task) {
        if (task == null){
            logger.error("findByTask: task is null.");
            return null;
        }
        return taskMapper.findByTask(task);
    }

    public Task findOne(final TaskQuery task) {
        if (task == null){
            logger.error("findOne: task is null.");
            return null;
        }
        task.setLimitStart(0);
        task.setLimitEnd(1);
        List<Task> datas = taskMapper.find(task);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        logger.error("findOne: data list is null. task: "+task.toString());
        return null;
    }

    @Override
    public Long removeByIds(List<Long> ids) {
        if (ids == null || ids.size()<=0){
            logger.error("removeByIds: id list is null.");
            return null;
        }
        return taskMapper.removeByIds(ids);
    }

    @Override
    public List<Task> getByCodeLike(String taskCode) {
        if (taskCode == null || taskCode.equals("")){
            logger.error("getByCodeLike: taskCode is null.");
            return null;
        }
        return taskMapper.getByCodeLike(taskCode);
    }

    public TaskFull getFullTaskByCode(final String taskCode){
        if (taskCode == null || taskCode.equals("")){
            logger.error("getFullTaskByCode: taskCode is null.");
            return null;
        }
        return getFullTaskBy(taskCode, null);
    }

    @Override
    public TaskFull getFullTask(Long id) {
        if (id == null){
            logger.error("getFullTask: id is null.");
            return null;
        }
        return getFullTaskBy(null,id);
    }

    public TaskFull getFullTaskBy(String taskCode, Long id){
        TaskFull taskFull = null;
        TaskQuery taskQuery = new TaskQuery();

        try {
            if (taskCode != null){
                taskQuery.setTaskCode(taskCode);
            }
            if (id != null){
                taskQuery.setId(id);
            }
            Task task = findOne(taskQuery);
            if (task != null){
                List<String> executeTypeList = java.util.Arrays.asList(task.getExecuteType().split(","));
                List<TableWithField> tableWithFieldList = new ArrayList<TableWithField>();
                TableWithField hiveTableWithField = tableInfoService.getTableWithField(task.getTableId());
                tableWithFieldList.add(hiveTableWithField);
                if (task.getMysqlTableId()!=null){
                    TableWithField mysqlTableWithField = tableInfoService.getTableWithField(task.getMysqlTableId());
                    tableWithFieldList.add(mysqlTableWithField);
                }
                taskFull = new TaskFull();
                BeanUtils.copyProperties(task, taskFull);
                taskFull.setTableList(tableWithFieldList);
                taskFull.setExecuteTypeList(executeTypeList);
            } else {
                logger.error("getFullTaskBy: task is null. taskCode: "+taskCode+" ,taskId: "+id);
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return taskFull;
    }

    @Override
    public Long insertFullTask(TaskFull taskFull) throws Exception {
        if (taskFull == null){
            logger.error("insertFullTask: taskFull is null.");
            return null;
        }
        HashMap<Long,Long> tableIdMap = new HashMap<Long,Long>();
        List<TableWithField> tableList = taskFull.getTableList();
        //判断task是否存在
        Task task = new Task();
        BeanUtils.copyProperties(taskFull,task);
        List<Long> groupIdList = getAllGroupId(task);
        if (groupIdList == null || groupIdList.size()<=0){
            logger.error("insertFullTask: groupIdList is null. taskFull: "+taskFull.toString());
            return null;
        }
//        Long count = taskMapper.isExistTaskInProject(groupIdList,task.getTaskCode(), task.getStatus());
        Long count = taskMapper.isExistTask(task.getTaskCode(), task.getStatus());
        if (count > 0) {
            throw new Exception("任务已经存在不能重复新增");
        }
        tableIdMap = tableInfoService.insertTableWithField(tableList);
//        for (int i=0; i<=tableList.size()-1; ++i) {
//            DbInfoWithStorage dbInfo = dbInfoService.getDbWithStorage(tableList.get(i).getDbId());
//            if (dbInfo == null){
//                logger.error("insertFullTask: dbInfo is null. taskFull: "+taskFull.toString());
//                return null;
//            }
//            //存储类型,1:hive,2:mysql
//            Long storageType = dbInfo.getStorageType();
//            TableWithField tableWithField = tableList.get(i);
//            tableWithField.setDbInfo(dbInfo);
//            TableInfo tableInfo = new TableInfo();
//            //复制tableWithField信息到tableInfo中
//            BeanUtils.copyProperties(tableWithField, tableInfo);
//            //插入表信息,并返回其主键id
//            Long tableId = tableInfoService.insert(tableInfo);
//            if (tableId == null){
//                logger.error("insertFullTask: 插入table_info返回的tableId is null. tableInfo: "+tableInfo.toString());
//                return null;
//            }
//            tableIdMap.put(storageType,tableId);
//
//            //插入字段到field_info
//            List<FieldInfo> fieldInfoList = tableWithField.getFieldList();
//            List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
//            if (fieldInfoList!=null && fieldInfoList.size()>0){
//                for (FieldInfo f : fieldInfoList) {
//                    FieldInfo fieldInfo = new FieldInfo();
//                    BeanUtils.copyProperties(f, fieldInfo);
//                    fieldInfo.setTableId(tableId);
//                    fieldInfos.add(fieldInfo);
//                }
//                //批量插入fieldInfos
//                //1.删除历史记录
//                fieldInfoService.removeByTableId(tableId);
//                //2.批量插入
//                fieldInfoService.insertBatch(fieldInfos);
//            }
//        }
        //插入task信息
        task.setTableId(tableIdMap.get(1L));//hive
        task.setMysqlTableId(tableIdMap.get(2L));//mysql
        taskMapper.insert(task);
        return task.getId();

    }

    @Override
    public Long updateFullTask(TaskFull taskFull) throws Exception {
        if (taskFull == null){
            logger.error("updateFullTask: taskFull is null.");
            return null;
        }
        //task
        Task task = new Task();
        BeanUtils.copyProperties(taskFull,task);

        //table_info
        List<TableWithField> tableList = taskFull.getTableList();
        if (tableList == null || tableList.size()<=0){
            logger.error("updateFullTask: tableList is null. taskFull: "+taskFull.toString());
            return null;
        }
        for (TableWithField t : tableList) {
            DbInfoWithStorage dbInfo = dbInfoService.getDbWithStorage(t.getDbId());
            if (dbInfo == null){
                logger.error("updateFullTask: dbInfo is null. taskFull: "+taskFull);
                return null;
            }
            //存储类型,1:hive,2:mysql
            Long storageType = dbInfo.getStorageType();

            TableInfo tableInfo = new TableInfo();
            BeanUtils.copyProperties(t,tableInfo);
            Long tableId = tableInfo.getId();

            Long exportTableId;
            String exportTableCode;
            if (storageType==2) {
                exportTableId = tableInfo.getId();
                exportTableCode = tableInfo.getTableCode();
                changeExport(task,tableInfo,exportTableId,exportTableCode,taskFull.getIsExport2Mysql());
            }

            tableInfoMapper.updateById(tableInfo);
            //field_info,先删除再添加
            List<FieldInfo> fieldList = t.getFieldList();
            List<FieldInfo> fieldInfoList1 = new ArrayList<FieldInfo>();
            if (fieldList!=null && fieldList.size()>0){
                for(int i=0;i<=fieldList.size()-1;++i ){
                    FieldInfo field = new FieldInfo();
                    BeanUtils.copyProperties(fieldList.get(i),field);
                    field.setTableId(tableId);
                    fieldInfoList1.add(field);
                }
                fieldInfoService.setFiledValue(fieldInfoList1);
                //1.删除历史记录
                fieldInfoService.removeByTableId(tableId);
                //2.批量插入
                fieldInfoService.insertBatch(fieldInfoList1);
            }
        }
        taskMapper.updateById(task);
        return task.getId();
    }

    @Override
    public List<Task> findTaskByGroupId(Long groupId) {
        if (groupId == null){
            logger.error("findTaskByGroupId: groupId is null.");
            return null;
        }
        List<Task> taskList = null;
        try {
            TaskQuery task = new TaskQuery();
            task.setGroupId(groupId);
            taskList = findByTask(task);
            if (taskList == null || taskList.size()<=0){
                logger.error("findTaskByGroupId: taskList is null. groupId: "+groupId);
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return taskList;
    }

    public Boolean isExport2Mysql(final Long id){
        if (id == null){
            logger.error("isExport2Mysql: id is null.");
            return null;
        }
        Boolean flag = false;
        if (taskMapper.get(id).getMysqlTableId() != null){
            flag = true;
        }
        return flag;
    }

    public void changeExport(Task task ,TableInfo tableInfo, Long tableId, String tableCode, Boolean isExport) throws Exception{
        //不导出 (是->否)
        if (tableId != null && !isExport) {
            logger.info("修改成不导出,task表的export table code置成空,并删除table_info表里的记录");
            List<Long> idList = new ArrayList<Long>();
            idList.add(tableId);
            tableInfoMapper.removeByIds(idList);
            logger.info("删除了table：" + idList.toString());
            task.setMysqlTableId(null);
        } else if (tableId == null && tableCode != null && !tableCode.equals("") && isExport) {//导出 (否->是)
                logger.info("修改成导出,修改task表export table的值并向table_info表里插入记录");
                Long count = tableInfoMapper.isExistTableInfo(tableCode, tableInfo.getDbId());
                if (count <= 0) {
                    Long id = tableInfoService.insert(tableInfo);
                    logger.info("插入了table: "+ id);
                    tableInfo.setCreateTime(new Date());
                    task.setMysqlTableId(id);
                } else {
                    throw new Exception(tableCode+" 表已经存在不能重复新增");
                }
            } else {
                TableInfoQuery tableInfoQuery = new TableInfoQuery();
                tableInfoQuery.setTableCode(tableCode);
                tableInfoQuery.setDbId(tableInfo.getDbId());
                TableInfo tableInfo1 = tableInfoService.findOne(tableInfoQuery);
                task.setMysqlTableId(tableInfo1.getId());
            }
    }

    public List<Long> getAllGroupId(Task task){
        if (task == null){
            logger.error("getAllGroupId: task is null.");
            return null;
        }
        Project project = projectService.findProjectByGroupId(task.getGroupId());
        if (project == null){
            logger.error("getAllGroupId: project is null. task: "+task.toString());
            return null;
        }
        List<TaskGroup> taskGroupList = taskGroupService.findTaskGroupByProjectId(project.getId());
        if (taskGroupList == null || taskGroupList.size()<=0){
            logger.error("getAllGroupId: taskGroupList is null. task: "+task.toString());
            return null;
        }
        List<Long> groupIdList = new ArrayList<Long>();
        for (TaskGroup taskGroup : taskGroupList){
            groupIdList.add(taskGroup.getId());
        }
        return groupIdList;
    }

}
