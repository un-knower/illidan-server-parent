package cn.whaley.datawarehouse.illidan.server.controller.task;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroup;
import cn.whaley.datawarehouse.illidan.common.domain.group.TaskGroupQuery;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.domain.task.Task;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQuery;
import cn.whaley.datawarehouse.illidan.common.domain.task.TaskQueryResult;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.group.TaskGroupService;
import cn.whaley.datawarehouse.illidan.common.service.storage.StorageInfoService;
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
    @Autowired
    private StorageInfoService storageInfoService;

    @RequestMapping("list")
    public ModelAndView list(Long groupId, ModelAndView mav){
        TaskGroup taskGroup = taskGroupService.get(groupId);
        if(taskGroup == null) {
            mav.addObject("msg","groupId参数不合法");
            mav.setViewName("error");
            return mav;
        }
        mav.addObject("groupId",groupId);
        mav.addObject("projectId",taskGroup.getProjectId());
        mav.setViewName("task/list");
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
            List<TaskQueryResult> tasks = taskService.findByTask(task);
            outputTemplateJson(tasks, count);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "获取任务列表失败" + e.getMessage());
        }
    }

    @RequestMapping("toAdd")
    public ModelAndView toAdd(ModelAndView mav, Long groupId) {
        mav.setViewName("task/add");
        List<TaskGroup> groupList = getTaskGroup();
        List<DbInfo> hiveDbInfoList = dbInfoService.getDbInfo(1L);//hive
//        List<DbInfo> mysqlDbInfoList = dbInfoService.getDbInfo(2L);//mysql
        mav.addObject("group",groupList);
        mav.addObject("hiveDbInfoList",hiveDbInfoList);
//        mav.addObject("mysqlDbInfoList",mysqlDbInfoList);
        mav.addObject("groupId",groupId);
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    public String add(@RequestBody TaskFull taskFull) {
        try {
            if(validateTask(taskFull).equals("ok")) {
                //执行方式(List)
                String[] executeTypeArray = taskFull.getExecuteType().split(",");
                List<String> executeTypeList = new ArrayList<>(Arrays.asList(executeTypeArray));
                taskFull.setExecuteTypeList(executeTypeList);
                //状态默认置成有效
                taskFull.setStatus("1");
                taskFull.setTableId(taskFull.getFullHiveTable().getHiveTable().getId());
                taskService.insertFullTask(taskFull);
                if (getCookieValue("taskId")!=null && !getCookieValue("taskId").equals("")){
                    clearCookie("taskId");
                    logger.info("清除cookie");
                }
                logger.info("新增任务成功!!!");
                return returnResult(true, "新增任务成功!!!");
            }else {
                return returnResult(false, validateTask(taskFull));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return returnResult(false, "新增任务失败: " + e.getMessage());
        }
    }

    @RequestMapping("toEdit")
    public ModelAndView toEdit(Long id, ModelAndView mav, Long groupId, int isCopy) {
        mav.setViewName("/task/edit");
        TaskFull task = taskService.getFullTask(id);
        List<TaskGroup> groupList = getTaskGroup();
        List<DbInfo> dbInfoList = dbInfoService.getDbInfo(1L);
        List<TableInfo> tableList = getTables(task.getFullHiveTable().getHiveTable().getDbId());
        mav.addObject("dbInfo", dbInfoList);
        mav.addObject("tableInfo", tableList);
        mav.addObject("task", task);
        mav.addObject("group", groupList);
        mav.addObject("groupId", groupId);
        mav.addObject("isCopy", isCopy);
        return mav;
    }

    @RequestMapping("edit")
    @ResponseBody
    public String edit(@RequestBody TaskFull taskFull) {
        try {
            if(validateTask(taskFull).equals("ok")) {
                taskFull.setTableId(taskFull.getFullHiveTable().getHiveTable().getId());
                taskService.updateFullTask(taskFull);
                logger.info("修改任务成功!!!");
                return returnResult(true, "修改任务成功!!!");
            }else {
                logger.info("修改任务失败!!!");
                return returnResult(false, "修改任务失败!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return returnResult(false, "修改任务失败," + e.getMessage());
        }
    }

    @RequestMapping("delete")
    @ResponseBody
    public void delete(String ids) {
        try {
            if(StringUtils.isEmpty(ids)){
                returnResult(false, "请选择要删除的记录");
            }else {
                String[] idArray = ids.split(",");
                List<Long> idList = Arrays.asList(idArray).stream().map(x->Long.parseLong(x)).collect(Collectors.toList());
                taskService.removeByIds(idList);
                logger.info("删除了任务：" + ids);
                if (getCookieValue("taskId")!=null && !getCookieValue("taskId").equals("")){
                    clearCookie("taskId");
                    logger.info("清除cookie");
                }
                returnResult(true, "删除任务成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "删除任务失败:" + e.getMessage());
        }
    }

    @RequestMapping("getTables")
    @ResponseBody
    public List<TableInfo> getTables(Long dbId){
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            TableInfoQuery tableInfo = new TableInfoQuery();
            tableInfo.setDbId(dbId);
            tableInfoList = tableInfoService.findTableInfo(tableInfo);
        } catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return tableInfoList;
    }

    public List<TaskGroup> getTaskGroup(){
        return taskGroupService.findByTaskGroup(new TaskGroupQuery());
    }

    public TableInfo getOneTableInfo(String tableCode){
        TableInfoQuery tableInfoQuery = new TableInfoQuery();
        tableInfoQuery.setTableCode(tableCode);
        return tableInfoService.findOne(tableInfoQuery);
    }

    public String validateTask(TaskFull task){
        String result = "ok";
        if(StringUtils.isEmpty(task)){
            return returnResult(false, "失败!!!");
        }
        if(!validateColumnNull(task.getTaskCode())){
            return validateMessage("任务code");
        }
        if (!codeReg(task.getTaskCode())){
            return returnResult(false, "任务code只能由英文字母,数字,-,_组成!!!");
        }
        if (!validateColumnNull(task.getAddUser())){
            return validateMessage("任务添加用户");
        }
        if (!validateColumnNull(task.getFullHiveTable().getHiveTable().getDbId())){
            return validateMessage("目标数据库");
        }
        if (!validateColumnNull(task.getFullHiveTable().getHiveTable().getId())){
            return validateMessage("目标表");
        }
        if (!validateColumnNull(task.getExecuteType())){
            return validateMessage("执行方式");
        }
        //执行方式有hour时要校验所选择的输出表是否有hour_p分区字段
        if(task.getExecuteType().contains("hour")){
            List<String> colNames = new ArrayList<>();
            List<FieldInfo> fieldInfos = fieldInfoService.getByTableId(task.getFullHiveTable().getHiveTable().getId());
            for (FieldInfo fieldInfo:fieldInfos){
                colNames.add(fieldInfo.getColName());
            }
            if(!colNames.contains("hour_p")){
                return returnResult(false, "执行方式包含hour时输出表必须包含hour_p分区字段,请重新选择输出表");
            }
        }
        if (!validateColumnNull(task.getContent())){
            return validateMessage("业务分析语句");
        }
        return result;
    }
}
