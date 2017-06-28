package cn.whaley.datawarehouse.illidan.common.service.field;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */
public interface FieldInfoService {
    FieldInfo get(final Long id);
    List<FieldInfo> getByTableId(final Long tableId);
}
