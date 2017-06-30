package cn.whaley.datawarehouse.illidan.engine.service;

import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import cn.whaley.datawarehouse.illidan.engine.param.DateFormat;
import cn.whaley.datawarehouse.illidan.engine.param.DateParam;
import cn.whaley.datawarehouse.illidan.engine.param.DateParamMethod;
import cn.whaley.datawarehouse.illidan.engine.param.ExecuteIntervalType;
import cn.whaley.datawarehouse.illidan.engine.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.whaley.datawarehouse.illidan.engine.param.Constants.*;
import static cn.whaley.datawarehouse.illidan.engine.param.DateParam.findByParamName;

/**
 * Created by lituo on 2017/6/28.
 */
@Service
public class SubmitService {

    Logger logger = LoggerFactory.getLogger(SubmitService.class);

    @Autowired
    private HiveService hiveService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FieldInfoService fieldInfoService;

    /**
     * 任务执行
     *
     * @param taskCode       任务名称
     * @param dataDueTimeStr 计算的数据的截止时间
     * @return 成功返回true
     */
    public boolean submit(String taskCode, String dataDueTimeStr, Map<String, String> paramMap) {

        Date dataDueTime = parseDataDueTime(dataDueTimeStr);
        if (dataDueTime == null) {
            throw new RuntimeException("参与分析的数据截止时间参数不合法：" + dataDueTimeStr);
        }

        //读取任务信息
        TaskFull task = taskService.getFullTaskByCode(taskCode);

        //替换执行sql中的参数
        if (task == null) {
            throw new RuntimeException("任务名称参数不合法：" + taskCode);
        }

        String content = task.getContent();

        //TODO 数据库参数添加到paramMap，优先级低于原paramMap中的参数
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }

        //根据配置的执行周期，生成多条执行sql
        Map<String, String> selectSqlMap = parseTimeInterval(content, task.getExecuteTypeList(), dataDueTime);
        if (selectSqlMap == null || selectSqlMap.size() == 0) {
            throw new RuntimeException("请检查执行周期配置：" + taskCode);
        }

        for (String executeType : selectSqlMap.keySet()) {

            String selectSql = selectSqlMap.get(executeType);

            //拼装insert overwrite语句
            String executeSql = getExecuteSql(selectSql, task, executeType);

            logger.info("完整执行sql：\n" + executeSql);

            String completeSql = parseSqlParams(executeSql, dataDueTime, paramMap);

            //执行sql
            int result = hiveService.queryForCount(completeSql);
        }

        return true;
    }

    private String parseSqlParams(String selectSql, Date dataDueTime, Map<String, String> paramMap) {

        Pattern pattern = Pattern.compile(PARAM_REGEX);
        Matcher matcher = pattern.matcher(selectSql);
        Map<String, String> map = new HashMap<>();
        while (matcher.find()) {
            String param = matcher.group();
            String neatParam = param.substring(2, param.length() - 1);
            String paramValue = parseSqlParam(neatParam, dataDueTime);
            if (paramValue != null) {
                map.put(param, paramValue);
            }
        }
        String result = selectSql;
        for (String key : map.keySet()) {
            result = result.replace(key, map.get(key));
        }

        //自定义参数
        result = parseCustomParams(result, paramMap);

        //验证已经替换所有参数
        Pattern invalidPattern = Pattern.compile(ALL_PARAM_REGEX);
        if (invalidPattern.matcher(result).find()) {
            throw new RuntimeException("发现不能识别的参数：" + matcher.group());
        }

        return result;
    }

    private Date parseDataDueTime(String str) {
        Date result = DateFormat.SHORT_DATE.parse(str);
        if (result != null) {
            return DateUtils.lastSecondOfDate(result);
        }
        result = DateFormat.DATE.parse(str);
        if (result != null) {
            return DateUtils.lastSecondOfDate(result);
        }
        result = DateFormat.DATETIME.parse(str);
        if (result != null) {
            return DateUtils.lastSecondOfDate(result);
        }
        return null;
    }

    private String parseSqlParam(String param, Date dataDueTime) {
        if (param == null) {
            return null;
        }
        String paramName;
        String dateFormat;
        if (param.contains(FORMAT_SPLITTER)) {
            String[] params = param.split(FORMAT_SPLITTER, 2);
            paramName = params[0];
            dateFormat = params[1];

        } else {
            paramName = param;
            dateFormat = DateParam.getDateFormatByParamName(paramName);
        }
        DateParam dateParam = findByParamName(paramName);
        if (dateParam == null) {
            return null;
        }
        Date date = DateParamMethod.invokeMethod(dataDueTime, dateParam.getMethodName());
        return DateFormat.format(date, dateFormat);
    }

    private Map<String, String> parseTimeInterval(String selectSql, List<String> executeTypeList, Date dateDueTime) {
        Map<String, String> result = new HashMap<>();
        if (selectSql.contains("${start_time}") || selectSql.contains("${end_time}")) {
            if (executeTypeList == null) {
                executeTypeList = new ArrayList<>();
            }
            if (executeTypeList.isEmpty()) {
                executeTypeList.add(ExecuteIntervalType.DAY.getName());
            }
            for (String executeType : executeTypeList) {
                ExecuteIntervalType intervalType = ExecuteIntervalType.getByName(executeType);
                if (intervalType == null) {
                    continue;
                }
                if (!intervalType.shouldExecute(dateDueTime)) {
                    continue;
                }
                String sql = selectSql.replace("${start_time}", "${" + intervalType.getStartParam().getParamName() + "}");
                sql = sql.replace("${end_time}", "${" + intervalType.getEndParam().getParamName() + "}");
                result.put(executeType, sql);
            }
        } else {
            result.put(ExecuteIntervalType.DAY.getName(), selectSql);
        }
        return result;
    }

    private String parseCustomParams(String sql, Map<String, String> paramMap) {
        String result = sql;
        //替换自定义参数值
        if (paramMap != null && paramMap.size() > 0) {
            for (String param : paramMap.keySet()) {
                result = result.replace("${" + param + "}", paramMap.get(param));
            }
        }
        return result;
    }

    private String getExecuteSql(String selectSql, TaskFull task, String executeType) {
        String tableName = task.getTable().getDbInfo().getDbCode() + "." + task.getTable().getTableCode();

        List<String> partitionFields = fieldInfoService.findPartitionFields(task.getTableId());
        if (partitionFields == null || partitionFields.isEmpty()) {
            throw new RuntimeException("未获取到分区字段， task=" + task.getTaskCode());
        }

        String insertStatement = "insert overwrite table " + tableName + " partition(";
        for (int i = 0; i < partitionFields.size(); i++) {
            String field = partitionFields.get(i);
            if (field.equalsIgnoreCase("date_type")) {
                insertStatement += "date_type=" + executeType;
            } else if (field.equalsIgnoreCase("product_line")) {
                insertStatement += "product_line=${product_line}";
            } else {
                if (DateParam.findByParamName(field) != null) {
                    insertStatement += field + "=${" + field+ "}";
                }else {
                    throw new RuntimeException("未知的分区字段" + field + "， task=" + task.getTaskCode());
                }
            }
            if (i < partitionFields.size() - 1) {
                insertStatement += ",";
            } else {
                insertStatement += ")";
            }
        }

        return insertStatement +  " " + selectSql;
    }
}
