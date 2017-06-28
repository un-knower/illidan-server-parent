package cn.whaley.datawarehouse.illidan.common.service.project;

import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.project.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    public Project get(final Long id) {
        return projectMapper.get(id);
    }

    public Long insert(final Project project) throws Exception{
        Long count = projectMapper.isExistProject(project.getProjectCode(),project.getId());
        if(count > 0){
            throw new Exception("项目已经存在不能重复新增");
        }
        projectMapper.insert(project);
        return project.getId();
    }

    public Long insertBatch(final List<Project> list) {
        return projectMapper.insertBatch(list);
    }

    public Long update(final Map<String, Object> params) {
        return projectMapper.update(params);
    }

    public Long remove(final Map<String, Object> params) {
        return projectMapper.remove(params);
    }

    public List<Project> find(final Map<String, String> params) {
        return projectMapper.find(params);
    }

    public Long count(final Map<String, String> params) {
        return projectMapper.count(params);
    }

    public Long countByProject(final ProjectQuery project) {
        return projectMapper.countByProject(project);
    }

    public Long updateByProject(final Project project) {
        return projectMapper.updateByProject(project);
    }

    public Long updateById(final Project project) {
        return projectMapper.updateById(project);
    }

    public Long removeByProject(final ProjectQuery project) {
        return projectMapper.removeByProject(project);
    }

    public List<Project> findByProject(final ProjectQuery project) {
        return projectMapper.findByProject(project);
    }

    public Project findOne(final ProjectQuery project) {
        project.setLimitStart(0);
        project.setLimitEnd(1);
        List<Project> datas = projectMapper.findByProject(project);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        return null;
    }

    @Override
    public Long removeByIds(List<Long> ids) {
        return projectMapper.removeByIds(ids);
    }

    @Override
    public List<Project> getByCodeLike(String projectCode) {
        return projectMapper.getByCodeLike(projectCode);
    }
}