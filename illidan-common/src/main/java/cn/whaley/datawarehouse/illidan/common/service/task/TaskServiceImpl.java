package cn.whaley.datawarehouse.illidan.common.service.task;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
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

    public Task get(final Long id) {
        return taskMapper.get(id);
    }

    public Long insert(final Task task) throws Exception{
        Long count = taskMapper.isExistTask(task.getTaskCode(),task.getStatus());
        if(count > 0){
            throw new Exception("任务已经存在不能重复新增");
        }
        return taskMapper.insert(task);
    }

    public Long insertBatch(final List<Task> list) {
        return taskMapper.insertBatch(list);
    }

    public Long update(final Map<String, Object> params) {
        return taskMapper.update(params);
    }

    public Long remove(final Map<String, Object> params) {
        return taskMapper.remove(params);
    }

    public List<Task> find(final TaskQuery task) {
        return taskMapper.find(task);
    }

    public Long count(final Map<String, String> params) {
        return taskMapper.count(params);
    }

    public Long countByTask(final TaskQuery task) {
        return taskMapper.countByTask(task);
    }

    public Long updateByTask(final Task task) {
        return taskMapper.updateByTask(task);
    }

    public Long updateById(final Task task) {
        return taskMapper.updateById(task);
    }

    public Long removeByTask(final TaskQuery task) {
        return taskMapper.removeByTask(task);
    }

    public List<Task> findByTask(final TaskQuery task) {
        return taskMapper.findByTask(task);
    }

    public Task findOne(final TaskQuery task) {
        task.setLimitStart(0);
        task.setLimitEnd(1);
        List<Task> datas = taskMapper.find(task);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        return null;
    }

    @Override
    public Long removeByIds(List<Long> ids) {
        return taskMapper.removeByIds(ids);
    }

    @Override
    public List<Task> getByCodeLike(String taskCode) {
        return taskMapper.getByCodeLike(taskCode);
    }

    public TaskFull getFullTaskByCode(final String taskCode){
        return getFullTaskBy(taskCode, null);
    }

    @Override
    public TaskFull getFullTask(Long id) {
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
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return taskFull;
    }

    @Override
    public Long insertFullTask(TaskFull taskFull) throws Exception {
        HashMap<Long,Long> tableIdMap = new HashMap<Long,Long>();
        List<TableWithField> tableList = taskFull.getTableList();
        //判断task是否存在
        Long count = taskMapper.isExistTask(taskFull.getTaskCode(), taskFull.getStatus());
        if (count > 0) {
            throw new Exception("任务已经存在不能重复新增");
        }
        for (int i=0; i<=tableList.size()-1; ++i) {
            DbInfoWithStorage dbInfo = dbInfoService.getDbWithStorage(tableList.get(i).getDbId());
            //存储类型,1:hive,2:mysql
            Long storageType = dbInfo.getStorageType();
            TableWithField tableWithField = tableList.get(i);
            tableWithField.setDbInfo(dbInfo);
            TableInfo tableInfo = new TableInfo();
            //复制tableWithField信息到tableInfo中
            BeanUtils.copyProperties(tableWithField, tableInfo);
            //插入表信息,并返回其主键id
            Long tableId = tableInfoService.insert(tableInfo);
            tableIdMap.put(storageType,tableId);

            //插入字段到field_info
            List<FieldInfo> fieldInfoList = tableWithField.getFieldList();
            List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
            if (fieldInfoList!=null && fieldInfoList.size()>0){
                for (FieldInfo f : fieldInfoList) {
                    FieldInfo fieldInfo = new FieldInfo();
                    BeanUtils.copyProperties(f, fieldInfo);
                    fieldInfo.setTableId(tableId);
                    fieldInfos.add(fieldInfo);
                }
                //批量插入fieldInfos
                //1.删除历史记录
                fieldInfoService.removeByTableId(tableId);
                //2.批量插入
                fieldInfoService.insertBatch(fieldInfos);
            }
        }
        //插入task信息
        Task task = new Task();
        BeanUtils.copyProperties(taskFull,task);
        task.setTableId(tableIdMap.get(1L));//hive
        task.setMysqlTableId(tableIdMap.get(2L));//mysql
        taskMapper.insert(task);
        return task.getId();

    }

    @Override
    public Long updateFullTask(TaskFull taskFull) {
        //task
        Task task = new Task();
        BeanUtils.copyProperties(taskFull,task);

        //table_info
        List<TableWithField> tableList = taskFull.getTableList();
        for (TableWithField t : tableList) {
            DbInfoWithStorage dbInfo = dbInfoService.getDbWithStorage(t.getDbId());
            //存储类型,1:hive,2:mysql
            Long storageType = dbInfo.getStorageType();

            TableInfo tableInfo = new TableInfo();
            BeanUtils.copyProperties(t,tableInfo);
            Long tableId = tableInfo.getId();

            Long mysqlTableId = null;
            String mysqlTableCode = null;
            if (storageType==2){
                mysqlTableId = tableInfo.getId();
                mysqlTableCode = tableInfo.getTableCode();
//                logger.info("mysqlTableId: "+mysqlTableId);
//                logger.info("mysqlTableCode: "+mysqlTableCode);
            }
            //不导出到mysql,将mysqlTableId的值置为空并删除table_info表里的这条记录
            if (mysqlTableId!=null && !taskFull.getIsExport2Mysql()){
//                logger.info("修改成不导出到mysql,task.mysqlTableId=null并删除table_info表里的记录");
                List<Long> idList = new ArrayList<Long>();
                idList.add(mysqlTableId);
                tableInfoMapper.removeByIds(idList);
                logger.error("删除了table：" + idList.toString());
                task.setMysqlTableId(null);
            }
            //导出到mysql,将mysql table的信息插入到table_info
            if (mysqlTableCode!=null && !mysqlTableCode.equals("") && taskFull.getIsExport2Mysql()){
//                logger.info("修改成导出到mysql,修改task.mysqlTableId的值并向table_info表里插入记录");
                try {
                    Long count = tableInfoMapper.isExistTableInfo(mysqlTableCode,tableInfo.getDbId());
//                    logger.info("count: "+count);
                    if (count <= 0){
//                        logger.info("插入到table_info");
                        Long id = tableInfoService.insert(tableInfo);
                        tableInfo.setCreateTime(new Date());
                        task.setMysqlTableId(id);
                    }else {
                        task.setMysqlTableId(mysqlTableId);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
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
        List<Task> taskList = null;
        try {
            TaskQuery task = new TaskQuery();
            task.setGroupId(groupId);
            taskList = findByTask(task);
        }catch (Exception e){
            e.printStackTrace();
        }
        return taskList;
    }

    public Boolean isExport2Mysql(final Long id){
        Boolean flag = false;
        if (taskMapper.get(id).getMysqlTableId() != null){
            flag = true;
        }
        return flag;
    }

}
