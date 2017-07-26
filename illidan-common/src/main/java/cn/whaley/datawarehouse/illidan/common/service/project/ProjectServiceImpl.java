package cn.whaley.datawarehouse.illidan.common.service.project;

import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import cn.whaley.datawarehouse.illidan.common.domain.project.ProjectQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.group.TaskGroupMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.project.ProjectMapper;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectServiceImpl implements ProjectService {
    private Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private TaskGroupMapper taskGroupMapper;

    public Project get(final Long id) {
        if (id == null){
            logger.error("get: id is null.");
            return null;
        }
        return projectMapper.get(id);
    }

    public Long insert(final Project project) throws Exception{
        if (project == null){
            logger.error("insert: project is null.");
            return null;
        }
        Long count = projectMapper.isExistProject(project.getProjectCode(), project.getStatus());
        if (count == null){
            logger.error("insert: count is null.");
            return null;
        }
        if(count > 0){
            throw new Exception("项目已经存在不能重复新增");
        }
        projectMapper.insert(project) ;
        return project.getId();
    }

    public Long insertBatch(final List<Project> list) {
        if (list == null || list.size()<=0){
            logger.error("insertBatch: list is null.");
            return null;
        }
        return projectMapper.insertBatch(list);
    }

    public Long update(final Map<String, Object> params) {
        if (params == null){
            logger.error("update: params is null.");
            return null;
        }
        return projectMapper.update(params);
    }

    public Long remove(final Map<String, Object> params) {
        if (params == null){
            logger.error("remove: params is null.");
            return null;
        }
        return projectMapper.remove(params);
    }

    public List<Project> find(final Map<String, String> params) {
        if (params == null){
            logger.error("find: params is null.");
            return null;
        }
        return projectMapper.find(params);
    }

    public Long count(final Map<String, String> params) {
        if (params == null){
            logger.error("count: params is null.");
            return null;
        }
        return projectMapper.count(params);
    }

    public Long countByProject(final ProjectQuery project) {
        if (project == null){
            logger.error("countByProject: project is null.");
            return null;
        }
        return projectMapper.countByProject(project);
    }

    public Long updateByProject(final Project project) {
        if (project == null){
            logger.error("updateByProject: project is null.");
            return null;
        }
        return projectMapper.updateByProject(project);
    }

    public Long updateById(final Project project) {
        if (project == null){
            logger.error("updateById: project is null.");
            return null;
        }
        return projectMapper.updateById(project);
    }

    public Long removeByProject(final ProjectQuery project) {
        if (project == null){
            logger.error("removeByProject: project is null.");
            return null;
        }
        return projectMapper.removeByProject(project);
    }

    public List<Project> findByProject(final ProjectQuery project) {
        if (project == null){
            logger.error("findByProject: project is null.");
            return null;
        }
        return projectMapper.findByProject(project);
    }

    public Project findOne(final ProjectQuery project) {
        if (project == null){
            logger.error("findOne: project is null.");
            return null;
        }
        project.setLimitStart(0);
        project.setLimitEnd(1);
        List<Project> datas = projectMapper.findByProject(project);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        logger.error("findOne: data list is null. project:"+project.toString());
        return null;
    }

    @Override
    public Long removeByIds(List<Long> ids) {
        if (ids == null || ids.size()<=0){
            logger.error("removeByIds: id list is null.");
            return null;
        }
        return projectMapper.removeByIds(ids);
    }

    @Override
    public List<Project> getByCodeLike(String projectCode) {
        if (projectCode == null || projectCode.equals("")){
            logger.error("getByCodeLike: projectCode is null.");
            return null;
        }
        return projectMapper.getByCodeLike(projectCode);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null){
            logger.error("deleteById: id is null.");
            return;
        }
        projectMapper.deleteById(id);
    }

    @Override
    public Project findProjectByGroupId(Long groupId) {
        if (groupId == null){
            logger.error("findProjectByGroupId: id is null.");
            return null;
        }
        TaskGroup taskGroup = taskGroupMapper.get(groupId);
        if (taskGroup == null){
            logger.error("findProjectByGroupId: taskGroup is null. groupId: "+groupId);
            return null;
        }
        Long projectId = taskGroup.getProjectId();
        if (projectId == null){
            logger.error("findProjectByGroupId: projectId is null. groupId: "+groupId);
        }
        return projectMapper.get(projectId);
    }
}