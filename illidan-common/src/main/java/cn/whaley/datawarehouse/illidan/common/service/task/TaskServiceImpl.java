package cn.whaley.datawarehouse.illidan.common.service.task;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.table.FullHiveTable;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQueryResult;
import cn.whaley.datawarehouse.illidan.common.mapper.db.DbInfoMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.field.FieldInfoMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.table.TableInfoMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.task.TaskMapper;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.project.ProjectService;
import cn.whaley.datawarehouse.illidan.common.service.storage.StorageInfoService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

        Long count = taskMapper.isExistTask(task.getTaskCode(),task.getStatus());
        if (count == null){
            logger.error("insert: count is null. task: "+task.toString());
            return null;
        }
        if(count > 0){
            throw new Exception("任务已经存在不能重复新增");
        }
        taskMapper.insert(task);
        return task.getId();
    }

    public Long insertBatch(final List<Task> list) {
        if (list == null || list.size()<=0){
            logger.error("insertBatch: list is null.");
            return null;
        }
        return taskMapper.insertBatch(list);
    }

    public List<Task> find(final Task task, final Integer limitStart, final Integer limitEnd) {
        if (task == null){
            logger.error("find: task is null.");
            return null;
        }
        return taskMapper.find(task, limitStart, limitEnd);
    }

    public Long count(final Task task) {
        if (task == null){
            logger.error("count: params is null.");
            return null;
        }
        return taskMapper.count(task);
    }

    public Long countByTask(final TaskQuery task) {
        if (task == null){
            logger.error("countByTask: task is null.");
            return null;
        }
        return taskMapper.countByTask(task);
    }

//    public Long updateByTask(final Task task) {
//        if (task == null){
//            logger.error("updateByTask: task is null.");
//            return null;
//        }
//        return taskMapper.updateByTask(task);
//    }

    public Long updateById(final Task task) {
        if (task == null){
            logger.error("updateById: task is null.");
            throw new RuntimeException("参数是null");
        }
        Task oldTask = get(task.getId());
        if(oldTask == null) {
            throw new RuntimeException("更新的任务不存在");
        }
        if (!oldTask.getTaskCode().equals(task.getTaskCode())) {
            Long count = taskMapper.isExistTask(task.getTaskCode(), task.getStatus());
            if (count == null) {
                logger.error("insert: count is null. task: " + task.toString());
                throw new RuntimeException("内部查询异常");
            }
            if (count > 0) {
                throw new RuntimeException("任务已经存在不能重复新增");
            }
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

    public List<TaskQueryResult> findByTask(final TaskQuery task) {
        if (task == null) {
            logger.error("findByTask: task is null.");
            return null;
        }
        List<TaskQueryResult> results = taskMapper.findByTask(task);
        for (TaskQueryResult taskQueryResult : results) {
            if (taskQueryResult.getMysqlTableId() != null && taskQueryResult.getMysqlTableId() > 0) {
                taskQueryResult.setIsExport2Mysql(true);
            } else {
                taskQueryResult.setIsExport2Mysql(false);
            }
        }
        return results;
    }

    public Task findOne(final Task task) {
        if (task == null){
            logger.error("findOne: task is null.");
            return null;
        }
        List<Task> datas = taskMapper.find(task, 0 , 1);
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
        Task taskQuery = new Task();

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
                FullHiveTable fullHiveTable = tableInfoService.getFullHiveTable(task.getTableId());
                TableWithField hiveTableWithField = fullHiveTable.getHiveTable();
                tableWithFieldList.add(hiveTableWithField);
                if (fullHiveTable.getMysqlTable() != null){
                    TableWithField mysqlTableWithField = (TableWithField)fullHiveTable.getMysqlTable();
                    tableWithFieldList.add(mysqlTableWithField);
                }
                taskFull = new TaskFull();
                BeanUtils.copyProperties(task, taskFull);
                taskFull.setTableList(tableWithFieldList);
                taskFull.setExecuteTypeList(executeTypeList);
                taskFull.setFullHiveTable(fullHiveTable);
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
        //判断task是否存在
        Task task = new Task();
        BeanUtils.copyProperties(taskFull,task);

        return insert(task);

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
        /*
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
        */
        updateById(task);
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
            Task task = new Task();
            task.setGroupId(groupId);
            taskList = find(task, null, null);
            if (taskList == null || taskList.size()<=0){
                logger.error("findTaskByGroupId: taskList is null. groupId: "+groupId);
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return taskList;
    }


/*
    public void changeExport(Task task ,TableInfo tableInfo, Long tableId, String tableCode, Boolean isExport) throws Exception{
        //不导出 (是->否)
        if (tableId != null && !isExport) {
            logger.info("修改成不导出,task表的export table code置成空,并删除table_info表里的记录");
            tableInfoMapper.removeById(tableId);
            logger.info("删除了table：" + tableId.toString());
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
*/
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
