package cn.whaley.datawarehouse.illidan.common.domain.task;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;

/**
 * Created by wujiulin on 2017/6/27.
 */
public class TaskFull extends Task{

    /**
     * 目标表
     */
    private TableWithField table;

    public TableWithField getTable() {
        return table;
    }

    public void setTable(TableWithField table) {
        this.table = table;
    }
}
