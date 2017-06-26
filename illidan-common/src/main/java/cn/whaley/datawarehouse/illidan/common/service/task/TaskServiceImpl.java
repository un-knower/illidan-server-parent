package cn.whaley.datawarehouse.illidan.common.service.task;

import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.mapper.task.TaskMapper;
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

    public Task get(final Long id) {
        return taskMapper.get(id);
    }

    public Long insert(final Task task) throws Exception{
        Long count = taskMapper.isExistTask(task.getTaskCode(),task.getId());
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

    public Long countByTask(final Task task) {
        return taskMapper.countByTask(task);
    }

    public Long updateByTask(final Task task) {
        return taskMapper.updateByTask(task);
    }

    public Long updateById(final Task task) {
        return taskMapper.updateById(task);
    }

    public Long removeByTask(final Task task) {
        return taskMapper.removeByTask(task);
    }

    public List<Task> findByTask(final Task task) {
        return taskMapper.findByTask(task);
    }

    public Task findOne(final Task task) {
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
}
