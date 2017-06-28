package cn.whaley.datawarehouse.illidan.common.service.group;


import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroupQuery;

import java.util.List;
import java.util.Map;

/**
 * Created by wujiulin on 2017/6/26.
 */
public interface TaskGroupService {
    TaskGroup get(final Long id);

    Long insert(final TaskGroup taskGroup) throws Exception;

    Long insertBatch(final List<TaskGroup> list);

    Long update(final Map<String, Object> params);

    Long remove(final Map<String, Object> params);

    List<TaskGroup> find(final Map<String, String> params);

    Long count(final Map<String, String> params);

    Long countByTaskGroup(final TaskGroupQuery taskGroup);

    Long updateByTaskGroup(final TaskGroup taskGroup);

    Long updateById(final TaskGroup taskGroup);

    Long removeByTaskGroup(final TaskGroupQuery taskGroup);

    List<TaskGroup> findByTaskGroup(final TaskGroupQuery taskGroup);

    Long removeByIds(final List<Long> ids);

    List<TaskGroup> getByCodeLike(String groupCode);
}
