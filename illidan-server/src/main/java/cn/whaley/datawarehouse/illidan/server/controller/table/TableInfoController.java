package cn.whaley.datawarehouse.illidan.server.controller.table;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.FullHiveTable;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.server.util.Common;
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
import java.util.Date;
import java.util.List;

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

    @RequestMapping("list")
    public String list(){
        return "table/list";
    }

    @RequestMapping("tableList")
    public void tableList(Integer start, Integer length, @ModelAttribute("tableInfo") TableInfoQuery tableInfo) {
        try {
            if(tableInfo == null){
                tableInfo = new TableInfoQuery();
            }
            tableInfo.setLimitStart(start);
            tableInfo.setPageSize(length);
            Long count = tableInfoService.countByTableInfo(tableInfo);
            List<TableInfo> tableInfos = tableInfoService.findByTableInfo(tableInfo);
            outputTemplateJson(tableInfos, count);
        } catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

    @RequestMapping("toAdd")
    public ModelAndView toAdd(ModelAndView mav,Long id) {
        FullHiveTable fullHiveTable;
        if(!id.equals(-1L)){
            fullHiveTable = tableInfoService.getFullHiveTable(id);
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
        }
        List<DbInfo> hiveDbInfoList = dbInfoService.getDbInfo(1L);//hive
        List<DbInfo> mysqlDbInfoList = dbInfoService.getDbInfo(2L);//mysql
        mav.addObject("hiveDbInfoList",hiveDbInfoList);
        mav.addObject("mysqlDbInfoList",mysqlDbInfoList);
        mav.addObject("isCopy",id);
        mav.setViewName("table/add");
        return mav;
    }

    @RequestMapping("add")
    @ResponseBody
    public String add(@RequestBody FullHiveTable fullHiveTable) {
        try {
            if(validateTable(fullHiveTable).equals("ok")) {
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
                if (getCookieValue("tableId")!=null && !getCookieValue("tableId").equals("")){
                    clearCookie("tableId");
                    logger.info("清除cookie");
                }
                logger.info("新增输出表成功!!!");
                return returnResult(true, "新增输出表成功!!!");
            }else {
                return returnResult(false, "新增输出表失败!!!");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            return returnResult(false, "新增输出表失败: " + e.getMessage());
        }
    }

    @RequestMapping("toEdit")
    public ModelAndView toEdit(Long id, ModelAndView mav) {
        mav.setViewName("table/edit");
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
    public String edit(@RequestBody FullHiveTable fullHiveTable) {
        try {
            if(validateTable(fullHiveTable).equals("ok")) {
                tableInfoService.updateFullHiveTable(fullHiveTable);
                logger.info("修改任务成功!!!");
                return returnResult(true, "修改输出表成功!!!");
            }else {
                logger.info("修改输出表失败!!!");
                return returnResult(true, "修改输出表成功!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return returnResult(false, "修改输出表失败," + e.getMessage());
        }
    }

    public String validateTable(FullHiveTable fullHiveTable){
        List<String> partitionColumns = new ArrayList<String>();
        partitionColumns.add("day_p");
        partitionColumns.add("month_p");
        partitionColumns.add("date_type");
        partitionColumns.add("product_line");
        String result = "ok";
        if(StringUtils.isEmpty(fullHiveTable)){
            return returnResult(false, "失败!!!");
        }
        if(!validateColumnNull(fullHiveTable.getHiveTable().getTableCode())){
            return validateMessage("hive表名");
        }
        if (!tableReg(fullHiveTable.getHiveTable().getTableCode())){
            return returnResult(false, "hive表名只能由英文字母,数字,_组成!!!");
        }
        if (fullHiveTable.getHiveTable().getDbId() == -1){
            return validateMessage("hive数据库");
        }
        List<FieldInfo> fieldInfos = fullHiveTable.getHiveTable().getFieldList();
        if(StringUtils.isEmpty(fieldInfos)){
            return returnResult(false, "字段不能为空");
        }
        for(FieldInfo fieldInfo:fieldInfos){
            if (!validateColumnNull(fieldInfo.getColName())){
                return validateMessage("字段名称");
            }
            if (!tableReg(fieldInfo.getColName())){
                return returnResult(false, "字段名称只能由英文字母,数字,_组成!!!");
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
                    return returnResult(false, "分区字段目前只能为day_p,month_p,date_type,product_line四个值");
                }
            }
        }
        if (fullHiveTable.getMysqlTable()!=null){
            if(!validateColumnNull(fullHiveTable.getMysqlTable().getTableCode())){
                return validateMessage("mysql表名");
            }
            if (!tableReg(fullHiveTable.getMysqlTable().getTableCode())){
                return returnResult(false, "mysql表名只能由英文字母,数字,_组成!!!");
            }
            if (fullHiveTable.getMysqlTable().getDbId() == -1){
                return validateMessage("mysql数据库");
            }
        }
        return result;
    }
}
