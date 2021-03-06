package cn.whaley.datawarehouse.illidan.common.service.task;


import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by wujiulin on 2017/6/26.
 */
public interface TaskService {
    Task get(final Long id);

    Long insert(final Task task) throws Exception;

    Long insertBatch(final List<Task> list);

    /**
     * 精确查询
     * @param task
     * @return
     */
    List<Task> find(final Task task, final Integer limitStart, final Integer limitEnd);

    Long count(final Task task);

    Long countByTask(final TaskQuery task);

//    Long updateByTask(final Task task);

    Long updateById(final Task task);

    Long removeByTask(final TaskQuery task);

    /**
     * 实现了code和描述的模糊查询
     * @param task
     * @return
     */
    List<TaskQueryResult> findByTask(final TaskQuery task);

    Long removeByIds(final List<Long> ids);

    List<Task> getByCodeLike(String taskCode);

    /**
     * 通过taskCode的精确查询
     * @param taskCode
     * @return
     */
    TaskFull getFullTaskByCode(final String taskCode);

    TaskFull getFullTask(final Long id);

    Long insertFullTask(final TaskFull taskFull) throws Exception;

    Long updateFullTask(final TaskFull taskFull) throws Exception;

    List<Task> findTaskByGroupId(final Long groupId);




}
