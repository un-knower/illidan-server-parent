package cn.whaley.datawarehouse.illidan.common.service.field;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.project.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */
public interface FieldInfoService {

    FieldInfo get(final Long id);

    List<FieldInfo> getByTableId(final Long tableId);

    void insertBatch(final List<FieldInfo> list);

    List<Long> insertMulti(final List<FieldInfo> list);

    Long insert(final FieldInfo fieldInfo);

    void removeByTableId(final Long tableId);

    void removeById(final Long id);

    List<String> findPartitionFields(final Long tableId);

    Long updateById(final FieldInfo fieldInfo);

    void setFiledValue(List<FieldInfo> fieldInfos);
}
