package cn.whaley.datawarehouse.illidan.server.controller.owner;

import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.owner.OwnerQuery;
import cn.whaley.datawarehouse.illidan.common.service.owner.OwnerService;
import cn.whaley.datawarehouse.illidan.server.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/29.
 */
public class OwnerController {
    @Autowired
    private OwnerService ownerService;

    @RequestMapping("getOwner")
    @ResponseBody
    public ServerResponse getOwner(OwnerQuery owner){
        return ServerResponse.responseBySuccess(ownerService.findByOwner(owner));
    }
}
