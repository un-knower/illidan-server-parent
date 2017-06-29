package cn.whaley.datawarehouse.illidan.common.mapper.table;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Mapper
@Component
public interface TableInfoMapper {
    TableInfo get(@Param("id") final Long id);
    Long insert(@Param("tableInfo") final TableInfo tableInfo);
    Long isExistTableInfo(@Param("tableCode") String tableCode);
}
