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
    public ModelAndView toAdd(ModelAndView mav) {
        List<DbInfo> hiveDbInfoList = dbInfoService.getDbInfo(1L);//hive
        List<DbInfo> mysqlDbInfoList = dbInfoService.getDbInfo(2L);//mysql
        mav.addObject("hiveDbInfoList",hiveDbInfoList);
        mav.addObject("mysqlDbInfoList",mysqlDbInfoList);
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
        String mysqlTableDbCode = dbInfoService.get(fullHiveTable.getMysqlTable().getDbId()).getDbCode();
        Boolean flag = tableInfoService.isExport2Mysql(fullHiveTable.getHiveTable().getId());
        mav.addObject("flag" , flag);
        mav.addObject("hiveTable",fullHiveTable.getHiveTable());
        mav.addObject("mysqlTable",fullHiveTable.getMysqlTable());
        mav.addObject("mysqlTableDbCode",mysqlTableDbCode);
        return mav;
    }

    public String validateTable(FullHiveTable fullHiveTable){
        String result = "ok";
        if(StringUtils.isEmpty(fullHiveTable)){
            return returnResult(false, "失败!!!");
        }
        if(!validateColumnNull(fullHiveTable.getHiveTable().getTableCode())){
            return validateMessage("hive表名");
        }
        if (!codeReg(fullHiveTable.getHiveTable().getTableCode())){
            return returnResult(false, "hive表名只能由英文字母,数字,-,_组成!!!");
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
            if (!codeReg(fieldInfo.getColName())){
                return returnResult(false, "字段名称只能由英文字母,数字,-,_组成!!!");
            }
            if (fieldInfo.getColType().equals("-1")){
                return validateMessage("字段类型");
            }
            if (fieldInfo.getIsPartitionCol().equals("-1")){
                return validateMessage("分区字段");
            }
        }
        if (fullHiveTable.getMysqlTable()!=null){
            if(!validateColumnNull(fullHiveTable.getMysqlTable().getTableCode())){
                return validateMessage("mysql表名");
            }
            if (!codeReg(fullHiveTable.getMysqlTable().getTableCode())){
                return returnResult(false, "mysql表名只能由英文字母,数字,-,_组成!!!");
            }
            if (fullHiveTable.getMysqlTable().getDbId() == -1){
                return validateMessage("mysql数据库");
            }
        }
        return result;
    }
}
