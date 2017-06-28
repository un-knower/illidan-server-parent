package cn.whaley.datawarehouse.illidan.common.service.task;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.task.TaskMapper;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoServiceImpl;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoServiceImpl;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by wujiulin on 2017/6/26.
 */

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TableInfoService tableInfoService;
    @Autowired
    private FieldInfoService fieldInfoService;



    public Task get(final Long id) {
        return taskMapper.get(id);
    }

    public Long insert(final Task task) throws Exception{
        Long count = taskMapper.isExistTask(task.getTaskCode(),task.getStatus());
        if(count > 0){
            throw new Exception("任务已经存在不能重复新增");
        }
        taskMapper.insert(task);
        return task.getId();
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

    public List<Task> find(final Map<String, String> params) {
        return taskMapper.find(params);
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
        List<Task> datas = taskMapper.findByTask(task);
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
        TaskFull taskFull = null;
        TableWithField tableWithField = null;

        TaskQuery taskQuery = null;
        TableInfoServiceImpl tableInfoService = new TableInfoServiceImpl();
        DbInfoServiceImpl dbInfoService = new DbInfoServiceImpl();
        FieldInfoServiceImpl fieldInfoService = new FieldInfoServiceImpl();

        taskQuery.setTaskCode(taskCode);

        Task task = findOne(taskQuery);

        TableInfo tableInfo = tableInfoService.get(task.getTableId());

        DbInfo dbInfo = dbInfoService.get(tableInfo.getDbId());

        List<FieldInfo> fieldInfoList = fieldInfoService.getByTableId(tableInfo.getId());

        tableWithField.setDbInfo(dbInfo);
        tableWithField.setFieldList(fieldInfoList);

        taskFull.setTable(tableWithField);

        return taskFull;
    }

    @Override
    public TaskFull getFullTask(Long id) {
        return null;
    }

    @Override
    public Long insertFullTask(TaskFull taskFull) throws Exception {
        TableWithField tableInfo = taskFull.getTable();
        //判断task是否存在
        Long count = taskMapper.isExistTask(taskFull.getTaskCode(),taskFull.getStatus());
        if(count >0 ){
            throw new Exception("任务已经存在不能重复新增");
        }
        //插入表信息,并返回其主键id
        Long tableId = tableInfoService.insert(tableInfo);
        //插入字段到field_info
        List<FieldInfo> fieldInfos = taskFull.getTable().getFieldList();
        for(FieldInfo f:fieldInfos ){
            FieldInfo fieldInfo = new FieldInfo();
            BeanUtils.copyProperties(f,fieldInfo);
            fieldInfo.setTableId(tableId);
        }

        //批量插入fieldInfos
        //1.删除历史记录
        fieldInfoService.removeByTableId(tableId);
        //2.批量插入
        fieldInfoService.insertBatch(fieldInfos);


        //插入task信息
        Task task = new Task();
        BeanUtils.copyProperties(taskFull,task);
        task.setTableId(tableId);
        return taskMapper.insert(task);
    }

    @Override
    public Long updateFullTask(TaskFull taskFull) {
        return null;
    }

    @Override
    public List<Task> findTaskByGroupId(Long groupId) {
        return null;
    }
}
