package cn.whaley.datawarehouse.illidan.common.domain.task;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/27.
 */
public class TaskFull extends Task{

    /**
     * 目标表
     */
    private TableWithField table;

    /**
     * 执行方式（自然日：day,自然周：week,自然月：month,季度：quarter,年初到执行日：ytd）
     */
    private List<String> executeTypeList;


    public TableWithField getTable() {
        return table;
    }

    public void setTable(TableWithField table) {
        this.table = table;
    }
}
