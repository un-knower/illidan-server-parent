package cn.whaley.datawarehouse.illidan.common.service.group;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.mapper.group.TaskGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by wujiulin on 2017/6/26.
 */

@Service
public class TaskGroupServiceImpl implements TaskGroupService {
    @Autowired
    private TaskGroupMapper taskGroupMapper;

    public TaskGroup get(final Long id) {
        return taskGroupMapper.get(id);
    }

    public Long insert(final TaskGroup taskGroup) throws Exception{
        Long count = taskGroupMapper.isExistTaskGroup(taskGroup.getGroupCode(),taskGroup.getId());
        if(count > 0){
            throw new Exception("任务组已经存在不能重复新增");
        }
        taskGroupMapper.insert(taskGroup);
        return taskGroup.getId();
    }

    public Long insertBatch(final List<TaskGroup> list) {
        return taskGroupMapper.insertBatch(list);
    }

    public Long update(final Map<String, Object> params) {
        return taskGroupMapper.update(params);
    }

    public Long remove(final Map<String, Object> params) {
        return taskGroupMapper.remove(params);
    }

    public List<TaskGroup> find(final Map<String, String> params) {
        return taskGroupMapper.find(params);
    }

    public Long count(final Map<String, String> params) {
        return taskGroupMapper.count(params);
    }

    public Long countByTaskGroup(final TaskGroup taskGroup) {
        return taskGroupMapper.countByTaskGroup(taskGroup);
    }

    public Long updateByTaskGroup(final TaskGroup taskGroup) {
        return taskGroupMapper.updateByTaskGroup(taskGroup);
    }

    public Long updateById(final TaskGroup taskGroup) {
        return taskGroupMapper.updateById(taskGroup);
    }

    public Long removeByTaskGroup(final TaskGroup taskGroup) {
        return taskGroupMapper.removeByTaskGroup(taskGroup);
    }

    public List<TaskGroup> findByTaskGroup(final TaskGroup taskGroup) {
        return taskGroupMapper.findByTaskGroup(taskGroup);
    }

    public TaskGroup findOne(final TaskGroup taskGroup) {
        taskGroup.setLimitStart(0);
        taskGroup.setLimitEnd(1);
        List<TaskGroup> datas = taskGroupMapper.findByTaskGroup(taskGroup);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        return null;
    }

    @Override
    public Long removeByIds(List<Long> ids) {
        return taskGroupMapper.removeByIds(ids);
    }

    @Override
    public List<TaskGroup> getByCodeLike(String groupCode) {
        return taskGroupMapper.getByCodeLike(groupCode);
    }
}
