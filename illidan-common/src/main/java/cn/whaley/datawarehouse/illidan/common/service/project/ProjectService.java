package cn.whaley.datawarehouse.illidan.common.service.project;



import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;

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

    Long countByProject(final ProjectQuery project);

    Long updateByProject(final Project project);

    Long updateById(final Project project);

    Long removeByProject(final ProjectQuery project);

    List<Project> findByProject(final ProjectQuery project);

    Long removeByIds(final List<Long> ids);

    List<Project> getByCodeLike(String projectCode);

    void deleteById(Long id);

    Project findProjectByGroupId(Long groupId);
}