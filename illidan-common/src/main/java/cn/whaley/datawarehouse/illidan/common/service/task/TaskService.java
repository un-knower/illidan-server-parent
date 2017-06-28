package cn.whaley.datawarehouse.illidan.common.service.task;


import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;

import java.util.List;
import java.util.Map;

/**
 * Created by wujiulin on 2017/6/26.
 */
public interface TaskService {
    Task get(final Long id);

    Long insert(final Task task) throws Exception;

    Long insertBatch(final List<Task> list);

    Long update(final Map<String, Object> params);

    Long remove(final Map<String, Object> params);

    List<Task> find(final Map<String, String> params);

    Long count(final Map<String, String> params);

    Long countByTask(final TaskQuery task);

    Long updateByTask(final Task task);

    Long updateById(final Task task);

    Long removeByTask(final TaskQuery task);

    List<Task> findByTask(final TaskQuery task);

    Long removeByIds(final List<Long> ids);

    List<Task> getByCodeLike(String taskCode);

    TaskFull getFullTaskByCode(final String taskCode);

}
