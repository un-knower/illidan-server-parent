package cn.whaley.datawarehouse.illidan.common.mapper.project;

import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface ProjectMapper {
    Project get(@Param("id") final Long id);

    Long insert(@Param("project") final Project project);

    Long insertBatch(@Param("list") final List<Project> list);

    Long update(@Param("params") final Map<String, Object> params);

    Long remove(@Param("params") final Map<String, Object> params);

    List<Project> find(@Param("project") final Map<String, String> params);

    Long count(@Param("project") final Map<String, String> params);

    Long countByProject(@Param("project") final Project project);

    Long updateByProject(@Param("project") final Project project);

    Long updateById(@Param("project") final Project project);

    Long removeByProject(@Param("project") final Project project);

    List<Project> findByProject(@Param("project") final Project project);

    Long isExistProject(@Param("projectCode") String projectCode, @Param("id") Long id);

    Long removeByIds(@Param("ids") final List<Long> ids);

    List<Project> getByCodeLike(@Param("name") final String name);
}