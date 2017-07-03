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

    @RequestMapping("list")
    public ModelAndView list(Long groupId, ModelAndView mav){
        if (groupId != null){
            mav.addObject("groupId",groupId);
            mav.setViewName("task/list");
        }else {
            mav.setViewName("group/list");
        }
        return mav;
    }

    @RequestMapping("taskList")
    public void groupList(Integer start, Integer length, @ModelAttribute("task") TaskQuery task) {
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
                for (int i=0;i<=fieldInfos.size()-1;++i){
                    fieldInfos.get(i).setColType("string");
                    fieldInfos.get(i).setIsPartitionCol("1");
                    fieldInfos.get(i).setCreateTime(new Date());
                    fieldInfos.get(i).setUpdateTime(new Date());
                    if (fieldInfos.get(i).getColName().equals("date_type")){
                        fieldInfos.get(i).setColDes("日期类型");
                        fieldInfos.get(i).setColIndex(1);
                    }else if (fieldInfos.get(i).getColName().equals("product_line")){
                        fieldInfos.get(i).setColDes("产品线");
                        fieldInfos.get(i).setColIndex(2);
                    }else if (fieldInfos.get(i).getColName().equals("month_p")){
                        fieldInfos.get(i).setColDes("月分区");
                        fieldInfos.get(i).setColIndex(3);
                    }else if (fieldInfos.get(i).getColName().equals("day_p")){
                        fieldInfos.get(i).setColDes("天分区");
                        fieldInfos.get(i).setColIndex(4);
                    }
                }

                taskService.insertFullTask(taskFull);
                returnResult(true, "新增任务成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            returnResult(false, "新增任务失败: " + e.getMessage());
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
