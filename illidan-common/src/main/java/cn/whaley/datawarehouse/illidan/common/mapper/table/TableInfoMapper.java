package cn.whaley.datawarehouse.illidan.common.mapper.table;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Mapper
@Component
public interface TableInfoMapper {
    TableInfo get(@Param("id") final Long id);

    Long insert(@Param("tableInfo") final TableInfo tableInfo);

    Long isExistTableInfo(@Param("tableCode") String tableCode,@Param("dbId") Long dbId);

    List<TableInfo> findByTableInfo(@Param("tableInfo") final TableInfoQuery tableInfo);

    Long updateById(@Param("tableInfo") final TableInfo tableInfo);

    Long removeByIds(@Param("ids") final List<Long> ids);

    Long countByTableInfo(@Param("tableInfo") final TableInfoQuery tableInfo);
}
