package cn.whaley.datawarehouse.illidan.common.service.task;


import cn.whaley.datawarehouse.illidan.common.domain.task.Task;

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

    Long countByTask(final Task task);

    Long updateByTask(final Task task);

    Long updateById(final Task task);

    Long removeByTask(final Task task);

    List<Task> findByTask(final Task task);

    Long removeByIds(final List<Long> ids);

    List<Task> getByCodeLike(String taskCode);
}
