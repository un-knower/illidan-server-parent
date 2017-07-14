package cn.whaley.datawarehouse.illidan.common.domain.task;

import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.mapper.db.DbInfoMapper;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.storage.StorageInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/27.
 */
public class TaskFull extends Task{

    /**
     * 目标表
     */
    private TableWithField table;
    private List<TableWithField> tableList;

    /**
     * 执行方式（自然日：day,自然周：week,自然月：month,季度：quarter,年初到执行日：ytd）
     */
    private List<String> executeTypeList;

    /**
     * 0:hive,1:mysql
     * 默认为0
     */
    private int flag = 0;

    public TableWithField getTable() {
        List<TableWithField> tableList = getTableList();
        for (int i=0; i<=tableList.size()-1; ++i) {
            Long storageType = tableList.get(i).getDbInfo().getStorageType();
            if (storageType == 1L){//hive
                table = tableList.get(i);
            }
        }
        return table;
    }

    public void setTable(TableWithField table) {
        this.table = table;
    }

    public List<String> getExecuteTypeList() {
        return executeTypeList;
    }

    public void setExecuteTypeList(List<String> executeTypeList) {
        this.executeTypeList = executeTypeList;
    }

    public List<TableWithField> getTableList() {
        return tableList;
    }

    public void setTableList(List<TableWithField> tableList) {
        this.tableList = tableList;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
