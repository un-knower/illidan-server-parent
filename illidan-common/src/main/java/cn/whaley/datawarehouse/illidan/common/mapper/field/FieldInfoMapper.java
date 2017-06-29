package cn.whaley.datawarehouse.illidan.common.mapper.field;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Mapper
@Component
public interface FieldInfoMapper {
    FieldInfo get(@Param("id") final Long id);
    List<FieldInfo> getByTableId(@Param("tableId") final Long tableId);
    void insertBatch(@Param("list") final List<FieldInfo> fieldInfo);
    void removeByTableId(@Param("tableId") final Long tableId);
}
