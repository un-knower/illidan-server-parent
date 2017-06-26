package cn.whaley.datawarehouse.illidan.test;

import cn.whaley.datawarehouse.illidan.common.domain.user.UserInfo;
import cn.whaley.datawarehouse.illidan.common.service.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * Created by lituo on 2017/6/22.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:spring/application-illidan-*.xml"})

public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void findAllTest() {
        List<UserInfo> userInfoList = userService.findAll();
        System.out.println(userInfoList.size());
    }
}
