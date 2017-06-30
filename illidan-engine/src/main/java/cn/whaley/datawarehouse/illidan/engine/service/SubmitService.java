package cn.whaley.datawarehouse.illidan.engine.service;

import cn.whaley.datawarehouse.illidan.common.domain.task.TaskFull;
import cn.whaley.datawarehouse.illidan.common.service.task.TaskService;
import cn.whaley.datawarehouse.illidan.engine.param.DateFormat;
import cn.whaley.datawarehouse.illidan.engine.param.DateParam;
import cn.whaley.datawarehouse.illidan.engine.param.DateParamMethod;
import cn.whaley.datawarehouse.illidan.engine.param.ExecuteIntervalType;
import cn.whaley.datawarehouse.illidan.engine.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.whaley.datawarehouse.illidan.engine.param.Constants.*;

/**
 * Created by lituo on 2017/6/28.
 */
@Service
public class SubmitService {

    @Autowired
    private HiveService hiveService;

    @Autowired
    private TaskService taskService;

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
            return false;
        }

        //读取任务信息
        TaskFull task = taskService.getFullTaskByCode(taskCode);

        //替换执行sql中的参数
        if (task == null) {
            return false;
        }

        String content = task.getContent();

        //替换自定义参数值，参数优先级高于数据库配置
        if (paramMap != null && paramMap.size() > 0) {
            for (String param : paramMap.keySet()) {
                content = content.replace("${" + param + "}", paramMap.get(param));
            }
        }

        //TODO 数据库参数


        Map<String, String> selectSqlMap = parseTimeInterval(content, task.getExecuteTypeList(), dataDueTime);
        if (selectSqlMap == null || selectSqlMap.size() == 0) {
            return false;
        }

        for (String executeType : selectSqlMap.keySet()) {

            String selectSql = selectSqlMap.get(executeType);

            //拼装insert overwrite语句
            String executeSql = getExecuteSql(selectSql, task, executeType);

            String completeSql = parseSqlParams(executeSql, dataDueTime);

            //执行sql
            int result = hiveService.queryForCount(completeSql);
        }

        return true;
    }

    private String parseSqlParams(String selectSql, Date dataDueTime) {

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

        Pattern invalidPattern = Pattern.compile(INVALID_PARAM_REGEX);
        if (invalidPattern.matcher(result).find()) {
            return null;
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
        DateParam dateParam = DateParam.findByParamName(paramName);
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

    private String getExecuteSql(String selectSql, TaskFull task, String executeType) {
        String tableName = task.getTable().getDbInfo().getDbCode() + "." + task.getTable().getTableCode();


        return selectSql;
    }
}
