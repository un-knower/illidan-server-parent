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
            List<Task> tasks = taskService.findByTask(task);
            for (int i=0;i<=tasks.size()-1;++i){
                tasks.get(i).setGroupCode(taskGroupService.get(tasks.get(i).getGroupId()).getGroupCode());
            }
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
        List<DbInfo> hiveDbInfoList = getDbInfo(1L);//hive
        List<DbInfo> mysqlDbInfoList = getDbInfo(2L);//mysql
        mav.addObject("group",groupList);
        mav.addObject("hiveDbInfoList",hiveDbInfoList);
        mav.addObject("mysqlDbInfoList",mysqlDbInfoList);
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
                taskFull.setCreateTime(new Date());
                taskFull.setUpdateTime(new Date());
                //table_info的创建和修改时间
                for(TableWithField t : taskFull.getTableList()){
                    t.setCreateTime(new Date());
                    t.setUpdateTime(new Date());
                }
                //field_info的相关字段
                List<FieldInfo> fieldInfos = new ArrayList<>();
                for (TableWithField t : taskFull.getTableList()){
                    if (t.getFieldList()!=null && t.getFieldList().size() >0){
                        fieldInfos = t.getFieldList();
                    }
                }
                fieldInfoService.setFiledValue(fieldInfos);
                taskService.insertFullTask(taskFull);
            }
            return returnResult(true, "新增任务成功!!!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return returnResult(false, "新增任务失败: " + e.getMessage());
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
        List<DbInfo> dbInfoList = getDbInfo(1L);
        List<DbInfo> mysqlDbInfoList = getDbInfo(2L);
        List<TableWithField> tableList = task.getTableList();
        for (TableWithField t : tableList){
            if (t.getDbInfo().getStorageType() == 1L){
                mav.addObject("hiveDbId" , t.getDbId());
                mav.addObject("hiveTable" , t.getTableCode());
                mav.addObject("tableId", t.getId());
            }
            if (t.getDbInfo().getStorageType() == 2L){
                mav.addObject("mysqlDbId" , t.getDbId());
                mav.addObject("mysqlTable" , t.getTableCode());
            }
        }
        Boolean flag = taskService.isExport2Mysql(task.getId());
        mav.addObject("flag" , flag);
        mav.addObject("dbInfo", dbInfoList);
        mav.addObject("mysqlDbInfoList", mysqlDbInfoList);
        mav.addObject("task", task);
        mav.addObject("group", groupList);
        mav.addObject("groupId", groupId);
        mav.addObject("fieldList", fieldList);
        mav.addObject("mysqlTableId", task.getMysqlTableId());
        return mav;
    }

    @RequestMapping("edit")
    @ResponseBody
    public String edit(@RequestBody TaskFull taskFull) {
        try {
            if(validateTask(taskFull).equals("ok")) {
                taskFull.setUpdateTime(new Date());
                for (TableWithField t : taskFull.getTableList()){
                    t.setUpdateTime(new Date());
                }
//                taskFull.getTable().setUpdateTime(new Date());
                taskService.updateFullTask(taskFull);
            }
            return returnResult(true, "修改任务成功!!!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return returnResult(false, "修改任务失败" + e.getMessage());
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
                logger.error("删除了任务：" + ids);
                returnResult(true, "删除任务成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "删除任务失败:" + e.getMessage());
        }
    }

    public List<TaskGroup> getTaskGroup(){
        return taskGroupService.findByTaskGroup(new TaskGroupQuery());
    }

    public List<DbInfo> getDbInfo(Long storageType){
        StorageInfoQuery storageInfo = new StorageInfoQuery();
        storageInfo.setStorageType(storageType);
        List<StorageInfo> storageInfos = storageInfoService.findByStorageInfo(storageInfo);

        List<DbInfo> dbInfoList = new ArrayList<>();
        DbInfoQuery dbInfo = new DbInfoQuery();
        for (StorageInfo s : storageInfos){
            dbInfo.setStorageId(s.getId());
            List<DbInfo> dbInfos = dbInfoService.findByDbInfo(dbInfo);
            dbInfoList.addAll(dbInfos);
        }
        return dbInfoList;
    }

    public TableInfo getOneTableInfo(String tableCode){
        TableInfoQuery tableInfoQuery = new TableInfoQuery();
        tableInfoQuery.setTableCode(tableCode);
        return tableInfoService.findOne(tableInfoQuery);
    }

    public String validateTask(TaskFull taskFull){
        String result = "ok";
        if(StringUtils.isEmpty(taskFull)){
            return returnResult(false, "失败!!!");
        }
        if(!validateColumnNull(taskFull.getTaskCode())){
            return validateMessage("任务code");
        }
        if (!codeReg(taskFull.getTaskCode())){
            return returnResult(false, "任务code只能由英文字母,数字,-,_组成!!!");
        }
        if (!validateColumnNull(taskFull.getAddUser())){
            return validateMessage("任务添加用户");
        }
        List<TableWithField> tableList = taskFull.getTableList();
        if (!validateColumnNull(tableList.get(0).getDbId())){
            return validateMessage("目标数据库");
        }
        if (!validateColumnNull(tableList.get(0).getTableCode())){
            return validateMessage("目标表");
        }
        if (tableList.get(0).getFieldList()==null || tableList.get(0).getFieldList().size()==0){
            return validateMessage("分区字段");
        }
        if (!validateColumnNull(tableList.get(0).getDataType())){
            return validateMessage("存储格式");
        }
        if (!validateColumnNull(tableList.get(0).getDbId())){
            return validateMessage("mysql目标数据库");
        }
        if (!validateColumnNull(tableList.get(0).getTableCode())){
            return validateMessage("mysql目标表");
        }
        if (!validateColumnNull(taskFull.getExecuteType())){
            return validateMessage("执行方式");
        }
        if (!validateColumnNull(taskFull.getContent())){
            return validateMessage("业务分析语句");
        }
        return result;
    }
}
