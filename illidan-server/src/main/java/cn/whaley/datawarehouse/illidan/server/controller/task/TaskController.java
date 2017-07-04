package cn.whaley.datawarehouse.illidan.server.controller.task;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroupQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import cn.whaley.datawarehouse.illidan.server.util.Common;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wujiulin on 2017/6/30.
 */

@Controller
@RequestMapping("task")
public class TaskController extends Common {
    private Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskGroupService taskGroupService;
    @Autowired
    private DbInfoService dbInfoService;
    @Autowired
    private TableInfoService tableInfoService;
    @Autowired
    private FieldInfoService fieldInfoService;

    @RequestMapping("list")
    public ModelAndView list(Long groupId, ModelAndView mav){
        if (groupId != null){
            TaskGroup taskGroup = taskGroupService.get(groupId);
            if(taskGroup == null) {
                returnResult(false, "groupId参数不合法");
            }
            mav.addObject("groupId",groupId);
            mav.addObject("projectId",taskGroup.getProjectId());
            mav.setViewName("task/list");
        }else {
            mav.setViewName("group/list");
        }
        return mav;
    }

    @RequestMapping("taskList")
    public void taskList(Integer start, Integer length, @ModelAttribute("task") TaskQuery task) {
        try {
            if (task == null) {
                task = new TaskQuery();
            }

            task.setLimitStart(start);
            task.setPageSize(length);
            Long count = taskService.countByTask(task);
            List<Task> tasks = taskService.findByTask(task);
            for (int i=0;i<=tasks.size()-1;++i){
                tasks.get(i).setGroupCode(taskGroupService.get(tasks.get(i).getGroupId()).getGroupCode());
            }
            outputTemplateJson(tasks, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("toAdd")
    public ModelAndView toAdd(ModelAndView mav, Long groupId) {
        mav.setViewName("task/add");
        List<TaskGroup> groupList = getTaskGroup();
        List<DbInfo> dbInfoList = getDbInfo();
        mav.addObject("group",groupList);
        mav.addObject("dbInfo",dbInfoList);
        mav.addObject("groupId",groupId);
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    public void add(@RequestBody TaskFull taskFull) {
        try {
            if(StringUtils.isEmpty(taskFull)){
                returnResult(false, "新增任务失败!!!");
            } else if(taskFull.getTaskCode() == null || taskFull.getTaskCode().equals("")){
                returnResult(false, "任务code不能为空!!!");
            } else if (!codeReg(taskFull.getTaskCode())){
                returnResult(false, "任务code只能由英文字母,数字,-,_组成!!!");
            } else {
                //执行方式(List)
                String[] executeTypeArray = taskFull.getExecuteType().split(",");
                List<String> executeTypeList = new ArrayList<>(Arrays.asList(executeTypeArray));
                taskFull.setExecuteTypeList(executeTypeList);
                //状态默认置成有效
                taskFull.setStatus("1");
                taskFull.setCreateTime(new Date());
                taskFull.setUpdateTime(new Date());
                //table_info的创建和修改时间
                taskFull.getTable().setCreateTime(new Date());
                taskFull.getTable().setUpdateTime(new Date());
                //field_info的相关字段
                List<FieldInfo> fieldInfos = taskFull.getTable().getFieldList();
                fieldInfoService.setFiledValue(fieldInfos);
                taskService.insertFullTask(taskFull);
                returnResult(true, "新增任务成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "新增任务失败: " + e.getMessage());
        }
    }

    @RequestMapping("toEdit")
    public ModelAndView toEdit(Long id, ModelAndView mav, Long groupId) {
        mav.setViewName("/task/edit");
        TaskFull task = taskService.getFullTask(id);
        List<FieldInfo> fieldInfoList = task.getTable().getFieldList();
        String fieldList = "";
        for (int i=0;i<=fieldInfoList.size()-1;++i){
            String field = fieldInfoList.get(i).getColName();
            fieldList = fieldList + "," + field;
        }
        fieldList = fieldList.substring(1,fieldList.length());
        List<TaskGroup> groupList = getTaskGroup();
        List<DbInfo> dbInfoList = getDbInfo();
        mav.addObject("dbInfo",dbInfoList);
        mav.addObject("task",task);
        mav.addObject("group",groupList);
        mav.addObject("groupId",groupId);
        mav.addObject("fieldList",fieldList);
        mav.addObject("tableId",task.getTable().getId());
        return mav;
    }

    @RequestMapping("edit")
    @ResponseBody
    public void edit(@RequestBody TaskFull taskFull) {
        try {
            if(StringUtils.isEmpty(taskFull)){
                returnResult(false, "修改任务失败!!!");
            } else {
                taskFull.setUpdateTime(new Date());
                taskFull.getTable().setUpdateTime(new Date());
                taskService.updateFullTask(taskFull);
                returnResult(true, "修改任务成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "修改任务失败" + e.getMessage());
        }
    }

    public List<TaskGroup> getTaskGroup(){
        return taskGroupService.findByTaskGroup(new TaskGroupQuery());
    }

    public List<DbInfo> getDbInfo(){
        return dbInfoService.findByDbInfo(new DbInfoQuery());
    }

    public TableInfo getOneTableInfo(String tableCode){
        TableInfoQuery tableInfoQuery = new TableInfoQuery();
        tableInfoQuery.setTableCode(tableCode);
        return tableInfoService.findOne(tableInfoQuery);
    }
}
