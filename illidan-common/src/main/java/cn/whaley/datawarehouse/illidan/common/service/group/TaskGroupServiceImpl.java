package cn.whaley.datawarehouse.illidan.common.service.group;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroupQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.group.TaskGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by wujiulin on 2017/6/26.
 */

@Service
public class TaskGroupServiceImpl implements TaskGroupService {
    private Logger logger = LoggerFactory.getLogger(TaskGroupServiceImpl.class);
    @Autowired
    private TaskGroupMapper taskGroupMapper;

    public TaskGroup get(final Long id) {
        if (id == null){
            logger.error("get: id is null.");
            return null;
        }
        return taskGroupMapper.get(id);
    }

    public Long insert(final TaskGroup taskGroup) throws Exception{
        if (taskGroup == null){
            logger.info("insert: taskGroup is null.");
            return null;
        }
        Long count = taskGroupMapper.isExistTaskGroup(taskGroup.getGroupCode(), taskGroup.getStatus());
        if (count == null){
            logger.error("insert: count is null. taskGroupCode: "+taskGroup.getGroupCode());
            return null;
        }
        if(count > 0){
            throw new Exception("任务组已经存在不能重复新增");
        }
        return taskGroupMapper.insert(taskGroup);
    }

    public Long insertBatch(final List<TaskGroup> list) {
        if (list == null || list.size()<=0){
            logger.error("insertBatch: taskGroup list is null.");
            return null;
        }
        return taskGroupMapper.insertBatch(list);
    }

    public Long update(final Map<String, Object> params) {
        if (params == null){
            logger.error("update: params is null.");
            return null;
        }
        return taskGroupMapper.update(params);
    }

    public Long remove(final Map<String, Object> params) {
        if (params == null){
            logger.error("remove: params is null.");
            return null;
        }
        return taskGroupMapper.remove(params);
    }

    public List<TaskGroup> find(final Map<String, String> params) {
        if (params == null){
            logger.error("find: params is null.");
            return null;
        }
        return taskGroupMapper.find(params);
    }

    public Long count(final Map<String, String> params) {
        if (params == null){
            logger.error("count: params is null.");
            return null;
        }
        return taskGroupMapper.count(params);
    }

    public Long countByTaskGroup(final TaskGroupQuery taskGroup) {
        if (taskGroup == null){
            logger.error("countByTaskGroup: taskGroup is null.");
            return null;
        }
        return taskGroupMapper.countByTaskGroup(taskGroup);
    }

    public Long updateByTaskGroup(final TaskGroup taskGroup) {
        if (taskGroup == null){
            logger.error("updateByTaskGroup: taskGroup is null.");
            return null;
        }
        return taskGroupMapper.updateByTaskGroup(taskGroup);
    }

    public Long updateById(final TaskGroup taskGroup) {
        if (taskGroup == null){
            logger.error("updateById: taskGroup is null.");
            return null;
        }
        return taskGroupMapper.updateById(taskGroup);
    }

    public Long removeByTaskGroup(final TaskGroupQuery taskGroup) {
        if (taskGroup == null){
            logger.error("removeByTaskGroup: taskGroup is null.");
            return null;
        }
        return taskGroupMapper.removeByTaskGroup(taskGroup);
    }

    public List<TaskGroup> findByTaskGroup(final TaskGroupQuery taskGroup) {
        if (taskGroup == null){
            logger.error("findByTaskGroup: taskGroup is null.");
            return null;
        }
        return taskGroupMapper.findByTaskGroup(taskGroup);
    }

    public TaskGroup findOne(final TaskGroupQuery taskGroup) {
        if (taskGroup == null){
            logger.error("findOne: taskGroup is null.");
            return null;
        }
        taskGroup.setLimitStart(0);
        taskGroup.setLimitEnd(1);
        List<TaskGroup> datas = taskGroupMapper.findByTaskGroup(taskGroup);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        logger.error("findOne: data list is null. taskGroup: "+taskGroup.toString());
        return null;
    }

    @Override
    public Long removeByIds(List<Long> ids) {
        if (ids == null || ids.size()<=0){
            logger.error("removeByIds: id list is null.");
            return null;
        }
        return taskGroupMapper.removeByIds(ids);
    }

    @Override
    public List<TaskGroup> getByCodeLike(String groupCode) {
        if (groupCode == null || groupCode.equals("")){
            logger.error("getByCodeLike: groupCode is null.");
            return null;
        }
        return taskGroupMapper.getByCodeLike(groupCode);
    }

    @Override
    public List<TaskGroup> findTaskGroupByProjectId(Long projectId) {
        if (projectId == null){
            logger.error("findTaskGroupByProjectId: projectId is null.");
            return null;
        }
        List<TaskGroup> taskGroupList = null;
        try {
            TaskGroupQuery taskGroup = new TaskGroupQuery();
            taskGroup.setProjectId(projectId);
            taskGroupList = findByTaskGroup(taskGroup);
            if (taskGroupList == null || taskGroupList.size()<=0){
                logger.error("findTaskGroupByProjectId: taskGroupList is null. projectId: "+projectId);
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return taskGroupList;
    }
}
