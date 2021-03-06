package cn.whaley.datawarehouse.illidan.server.controller.table;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.FullHiveTable;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.service.authorize.AuthorizeService;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.server.auth.LoginRequired;
import cn.whaley.datawarehouse.illidan.server.response.ServerResponse;
import cn.whaley.datawarehouse.illidan.server.service.AuthService;
import cn.whaley.datawarehouse.illidan.server.service.AuthorizeHttpService;
import cn.whaley.datawarehouse.illidan.server.service.TableFieldService;
import cn.whaley.datawarehouse.illidan.server.controller.Common;
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

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wujiulin on 2017/11/7.
 */

@Controller
@RequestMapping("table")
public class TableInfoController extends Common {
    private Logger logger = LoggerFactory.getLogger(TableInfoController.class);
    @Autowired
    private TableInfoService tableInfoService;
    @Autowired
    private FieldInfoService fieldInfoService;
    @Autowired
    private DbInfoService dbInfoService;
    @Autowired
    private TableFieldService tableFieldService;
    @Autowired
    private AuthorizeHttpService authorizeHttpService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private AuthService authService;

    @RequestMapping("list")
    @LoginRequired
    public String list(HttpSession httpSession){
        return "table/list";
    }

    @RequestMapping("tableList")
    @LoginRequired
    @ResponseBody
    public ServerResponse tableList(Integer start, Integer length, @ModelAttribute("tableInfo") TableInfoQuery tableInfo, HttpSession httpSession) {
        try {
            if(tableInfo == null){
                tableInfo = new TableInfoQuery();
            }
//            tableInfo.setLimitStart(start);
//            tableInfo.setPageSize(length);
//            Long count = tableInfoService.countByTableInfo(tableInfo);
//            List<TableInfo> tableInfos = tableInfoService.findByTableInfo(tableInfo);
            List<Long> resultDbIds = authService.filterTableListByDb(getUserNameFromSession(httpSession));
            tableInfo.setDbIdList(resultDbIds);
            List<TableInfo> tableInfoList = tableInfoService.findByTableInfo(tableInfo);
            Long count = (long) tableInfoList.size();
            //分页显示
            List<TableInfo> result = tableInfoList.subList(start, (int) (count - start > length ? start + length : count));
            return ServerResponse.responseBySuccessDataAndCount(result, count);
        } catch (Exception e){
            logger.error(e.getMessage());
            return ServerResponse.responseByError("查询失败：" + e.getMessage());
        }

    }

    @RequestMapping("toAdd")
    @LoginRequired
    public ModelAndView toAdd(ModelAndView mav, Long id, HttpSession httpSession) {
        FullHiveTable fullHiveTable;
        if(!id.equals(-1L)){
            //复制
            fullHiveTable = tableInfoService.getFullHiveTable(id);
            TableWithField mysqlTable = (TableWithField) fullHiveTable.getMysqlTable();
            Boolean flag = fullHiveTable.getHiveTable().getMysqlTableId() != null;
            mav.addObject("flag" , flag);
            mav.addObject("hiveTable",fullHiveTable.getHiveTable());
            mav.addObject("mysqlTable",fullHiveTable.getMysqlTable());
            String mysqlTableDbCode = "";
            if(mysqlTable!=null){
                mysqlTableDbCode = mysqlTable.getDbInfo().getDbCode();
            }
            mav.addObject("mysqlTableDbCode",mysqlTableDbCode);
        }
        List<DbInfo> hiveDbInfoList = dbInfoService.getDbInfo(1L);//hive
        List<DbInfo> mysqlDbInfoList = dbInfoService.getDbInfo(2L);//mysql
        mav.addObject("hiveDbInfoList",authService.filterDbList(hiveDbInfoList, getUserNameFromSession(httpSession)));
        mav.addObject("mysqlDbInfoList",authService.filterDbList(mysqlDbInfoList, getUserNameFromSession(httpSession)));
        mav.addObject("isCopy",id);
        mav.setViewName("table/add");
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    @LoginRequired
    public ServerResponse add(@RequestBody FullHiveTable fullHiveTable, HttpSession httpSession) {
        try {
            String validResult = validateTable(fullHiveTable);
            if(validResult.equals("ok")) {
                String userName = getUserNameFromSession(httpSession);
                Long dbId = fullHiveTable.getHiveTable().getDbId();
                if (!authService.hasDbPermission(dbId, "write", userName)) {
                    return ServerResponse.responseByError(403, "新增失败，缺少数据库写权限");
                }
                List<FieldInfo> fieldInfos = new ArrayList<>();
                TableWithField hiveTable = fullHiveTable.getHiveTable();
                TableInfo mysqlTable = fullHiveTable.getMysqlTable();
                hiveTable.setStatus("1");
                if(mysqlTable!=null){
                    mysqlTable.setStatus("1");
                }
                if (hiveTable.getFieldList() != null && hiveTable.getFieldList().size() > 0) {
                    fieldInfos = hiveTable.getFieldList();
                }
                fieldInfoService.setFiledValue(fieldInfos);
                Long tableId = tableInfoService.insertFullHiveTable(fullHiveTable);
                if (getCookieValue("tableId")!=null && !getCookieValue("tableId").equals("")){
                    clearCookie("tableId");
                    logger.info("清除cookie");
                }
                logger.info("新增输出表成功!!!");
                //创建权限
//                fullHiveTable.getHiveTable().setId(tableId);
//                Authorize authorize = authService.createTableAuth(fullHiveTable.getHiveTable(), getUserNameFromSession(httpSession));
//                if (authorize.getNodeId() == null) {
//                    //创建失败，回滚
//                    tableInfoService.dropFullHiveTable(tableId);
//                    return ServerResponse.responseByError( "创建table权限失败");
//                }
                return ServerResponse.responseBySuccessMessage( "新增输出表成功!!!");
            }else {
                return ServerResponse.responseByError( "新增输出表失败:" + validResult);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError( "新增输出表失败: " + e.getMessage());
        }
    }

    @RequestMapping("toEdit")
    @LoginRequired
    public ModelAndView toEdit(Long id, ModelAndView mav, HttpSession httpSession) {
        mav.setViewName("table/edit");
        String userName = getUserNameFromSession(httpSession);
        if (!authService.hasTablePermission(id, "read", userName)) {
            mav.setViewName("error");
            mav.addObject("msg", "没有查看权限");
            return mav;
        }
        FullHiveTable fullHiveTable = tableInfoService.getFullHiveTable(id);
        TableWithField mysqlTable = (TableWithField) fullHiveTable.getMysqlTable();
        Boolean flag = fullHiveTable.getHiveTable().getMysqlTableId() != null;
        mav.addObject("flag" , flag);
        mav.addObject("hiveTable",fullHiveTable.getHiveTable());
        mav.addObject("mysqlTable",fullHiveTable.getMysqlTable());
        mav.addObject("mysqlDbInfoList",dbInfoService.getDbInfo(2L));
        String mysqlTableDbCode = "";
        if(mysqlTable!=null){
            mysqlTableDbCode = mysqlTable.getDbInfo().getDbCode();
        }
        mav.addObject("mysqlTableDbCode",mysqlTableDbCode);
        return mav;
    }

    @RequestMapping("edit")
    @ResponseBody
    @LoginRequired
    public ServerResponse edit(@RequestBody FullHiveTable fullHiveTable, HttpSession httpSession) {
        try {
            String validateResult = validateTable(fullHiveTable);
            if(validateResult.equals("ok")) {
                String userName = getUserNameFromSession(httpSession);
                if (!authService.hasTablePermission(fullHiveTable.getHiveTable().getId(), "write", userName)) {
                    return ServerResponse.responseByError(403, "编辑失败，缺少数据表写权限");
                }
                tableInfoService.updateFullHiveTable(fullHiveTable);
                logger.info("修改任务成功!!!");
                return ServerResponse.responseBySuccessMessage( "修改输出表成功!!!");
            }else {
                logger.info("修改输出表失败!!!");
                return ServerResponse.responseByError( "修改输出表失败" + validateResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError( "修改输出表失败," + e.getMessage());
        }
    }

    @RequestMapping("checkTable")
    @ResponseBody
    @LoginRequired
    public ServerResponse checkTable(HttpSession httpSession) {
        try {
            tableFieldService.completeTableInfoAll();
            return ServerResponse.responseBySuccessMessage( "检查字段完成，结果见日志");
        } catch (Exception e) {
            logger.error("补充字段异常", e);
            return ServerResponse.responseByError( "检查字段失败," + e.getMessage());
        }
    }

    @RequestMapping("toParseSql")
    @LoginRequired
    public ModelAndView toParseSql(ModelAndView mav, HttpSession httpSession) {
        mav.setViewName("table/parseSql");
        return mav;
    }

    @RequestMapping("parseSql")
    @ResponseBody
    @LoginRequired
    public ServerResponse parseSql(String createSql, HttpSession httpSession) {
        try {
            if (StringUtils.isEmpty(createSql)){
                return ServerResponse.responseByError( "建表语句不能为空");
            }
            logger.info("createSql:\n "+createSql);
            TableWithField tableWithField = tableFieldService.parseHiveFromCreateSql(createSql,null);
            FullHiveTable fullHiveTable = new FullHiveTable();
            fullHiveTable.setHiveTable(tableWithField);
            String validResult = validateTable(fullHiveTable);
            if(validResult.equals("ok")) {
                String userName = getUserNameFromSession(httpSession);
                Long dbId = fullHiveTable.getHiveTable().getDbId();
                if (!authService.hasDbPermission(dbId, "write", userName)) {
                    return ServerResponse.responseByError(403, "新增失败，缺少数据库写权限");
                }
                List<FieldInfo> fieldInfos = new ArrayList<>();
                TableWithField hiveTable = fullHiveTable.getHiveTable();
                TableInfo mysqlTable = fullHiveTable.getMysqlTable();
                hiveTable.setStatus("1");
                if(mysqlTable!=null){
                    mysqlTable.setStatus("1");
                }
                if (hiveTable.getFieldList() != null && hiveTable.getFieldList().size() > 0) {
                    fieldInfos = hiveTable.getFieldList();
                }
                fieldInfoService.setFiledValue(fieldInfos);
                tableInfoService.insertFullHiveTable(fullHiveTable);
                logger.info("解析建表语句成功,新增输出表"+fullHiveTable.getHiveTable().getTableCode());
                return ServerResponse.responseBySuccessMessage( "解析建表语句成功,新增输出表"+fullHiveTable.getHiveTable().getTableCode());
            }else {
                return ServerResponse.responseByError( "解析建表语句失败:" + validResult);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError( "解析建表语句失败," + e.getMessage());
        }

    }

    @RequestMapping("delete")
    @ResponseBody
    @LoginRequired
    public ServerResponse delete(String ids, HttpSession httpSession) {
        try {
            if(StringUtils.isEmpty(ids)){
                return ServerResponse.responseByError( "请选择要删除的记录");
            }
            String[] idArray = ids.split(",");
            List<Long> idList = Arrays.asList(idArray).stream().map(x->Long.parseLong(x)).collect(Collectors.toList());
            if(idList.size() > 1) {
                ServerResponse.responseByError( "删除目标表失败:不支持同时删除多个表");
            }
            Long id = idList.get(0);
            String userName = getUserNameFromSession(httpSession);
            if (!authService.hasTablePermission(id, "write", userName)) {
                return ServerResponse.responseByError(403, "删除失败，缺少数据表写权限");
            }
            tableInfoService.dropFullHiveTable(id);

            logger.info("删除了目标表：" + ids);
            return ServerResponse.responseBySuccessMessage( "删除目标表成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ServerResponse.responseByError( "删除目标表失败:" + e.getMessage());
        }
    }

    public String validateTable(FullHiveTable fullHiveTable){
        List<String> partitionColumns = new ArrayList<String>();
        partitionColumns.add("day_p");
        partitionColumns.add("month_p");
        partitionColumns.add("date_type");
        partitionColumns.add("hour_p");
        String result = "ok";
        if(StringUtils.isEmpty(fullHiveTable)){
            return "失败!!!";
        }
        if(!validateColumnNull(fullHiveTable.getHiveTable().getTableCode())){
            return validateMessage("hive表名");
        }
        if (!tableReg(fullHiveTable.getHiveTable().getTableCode())){
            return "hive表名只能由英文字母,数字,_组成!!!";
        }
        if (fullHiveTable.getHiveTable().getDbId() == -1){
            return validateMessage("hive数据库");
        }
        List<FieldInfo> fieldInfos = fullHiveTable.getHiveTable().getFieldList();
        if(StringUtils.isEmpty(fieldInfos)){
            return "字段不能为空";
        }
        List<String> partitionColNames = new ArrayList<>();
        for(FieldInfo fieldInfo:fieldInfos){
            if (!validateColumnNull(fieldInfo.getColName())){
                return validateMessage("字段名称");
            }
            if (!tableReg(fieldInfo.getColName())){
                return "字段名称只能由英文字母,数字,_组成!!!";
            }
            if (fieldInfo.getColType().equals("-1")){
                return validateMessage("字段类型");
            }
            if (fieldInfo.getIsPartitionCol().equals("-1")){
                return validateMessage("分区字段");
            }
            //分区字段只能为day_p,month_p,date_type,product_line四个值
            if (fieldInfo.getIsPartitionCol().equals("1")){
                if(!partitionColumns.contains(fieldInfo.getColName())){
                    return "分区字段目前只能为date_type,month_p,day_p,hour_p四个值";
                }
                partitionColNames.add(fieldInfo.getColName());
            }
        }
        //输出表的分区字段必须包含date_type和day_p
        if(!partitionColNames.contains("date_type") || !partitionColNames.contains("day_p")){
            return "输出表的分区字段必须包含date_type和day_p";
        }
        if (fullHiveTable.getMysqlTable()!=null){
            if(!validateColumnNull(fullHiveTable.getMysqlTable().getTableCode())){
                return validateMessage("mysql表名");
            }
            if (!tableReg(fullHiveTable.getMysqlTable().getTableCode())){
                return "mysql表名只能由英文字母,数字,_组成!!!";
            }
            if (fullHiveTable.getMysqlTable().getDbId() == -1){
                return validateMessage("mysql数据库");
            }
        }
        return result;
    }
}
