package cn.whaley.datawarehouse.illidan.common.mapper.group;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroupQuery;
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
public interface TaskGroupMapper {
    TaskGroup get(@Param("id") final Long id);

    Long insert(@Param("taskGroup") final TaskGroup taskGroup);

    Long insertBatch(@Param("list") final List<TaskGroup> list);

    Long update(@Param("params") final Map<String, Object> params);

    Long remove(@Param("params") final Map<String, Object> params);

    List<TaskGroup> find(@Param("taskGroup") final Map<String, String> params);

    Long count(@Param("taskGroup") final Map<String, String> params);

    Long countByTaskGroup(@Param("taskGroup") final TaskGroupQuery taskGroup);

    Long updateByTaskGroup(@Param("taskGroup") final TaskGroup taskGroup);

    Long updateById(@Param("taskGroup") final TaskGroup taskGroup);

    Long removeByTaskGroup(@Param("taskGroup") final TaskGroupQuery taskGroup);

    List<TaskGroup> findByTaskGroup(@Param("taskGroup") final TaskGroupQuery taskGroup);

    Long isExistTaskGroup(@Param("groupCode") String groupCode);

    Long removeByIds(@Param("ids") final List<Long> ids);

    List<TaskGroup> getByCodeLike(@Param("groupCode") final String groupCode);
}
