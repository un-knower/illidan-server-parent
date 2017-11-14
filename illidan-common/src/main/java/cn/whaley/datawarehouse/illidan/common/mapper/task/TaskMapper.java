package cn.whaley.datawarehouse.illidan.common.mapper.task;

import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQueryResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wujiulin on 2017/6/26.
 */

@Mapper
@Component
public interface TaskMapper {
    Task get(@Param("id") final Long id);

    Long insert(@Param("task") final Task task);

    Long insertBatch(@Param("list") final List<Task> list);

    List<Task> find(@Param("task") final Task task,
                    @Param("limitStart") final Integer limitStart,
                    @Param("limitEnd") final Integer limitEnd);

    Long count(@Param("task") final Task task);

    Long countByTask(@Param("task") final TaskQuery task);

    Long updateByTask(@Param("task") final Task task);

    Long updateById(@Param("task") final Task task);

    Long removeByTask(@Param("task") final TaskQuery task);

    List<TaskQueryResult> findByTask(@Param("task") final TaskQuery task);

    Long isExistTaskInProject(@Param("groupIds") final List<Long> groupIds,@Param("taskCode") String taskCode, @Param("status") String status);

    Long isExistTask(@Param("taskCode") String taskCode, @Param("status") String status);

    Long removeByIds(@Param("ids") final List<Long> ids);

    List<Task> getByCodeLike(@Param("taskCode") final String taskCode);
}
