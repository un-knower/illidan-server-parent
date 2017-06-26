package cn.whaley.datawarehouse.illidan.common.service.project;



import cn.whaley.datawarehouse.illidan.common.domain.project.Project;

import java.util.List;
import java.util.Map;

public interface ProjectService {
    Project get(final Long id);

    Long insert(final Project project) throws Exception;

    Long insertBatch(final List<Project> list);

    Long update(final Map<String, Object> params);

    Long remove(final Map<String, Object> params);

    List<Project> find(final Map<String, String> params);

    Long count(final Map<String, String> params);

    Long countByProject(final Project project);

    Long updateByProject(final Project project);

    Long updateById(final Project project);

    Long removeByProject(final Project project);

    List<Project> findByProject(final Project project);

    Long removeByIds(final List<Long> ids);

    List<Project> getByCodeLike(String name);
}