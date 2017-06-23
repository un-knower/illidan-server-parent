package cn.whaley.datawarehouse.illidan.controller;

import cn.whaley.datawarehouse.illidan.domain.UserInfo;
import cn.whaley.datawarehouse.illidan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {
	@Autowired
	private UserService userService;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

	/**
	 * 查询用户
	 * @return
	 */
	@RequestMapping("/findUser")
	public String findUser(Model model) {
        List<UserInfo> userInfoList = userService.findAll();
        model.addAttribute("userInfos",userInfoList);
        logger.info("获取成功===============");
        return "findUser";
	}
	
	/**
	 * findById
	 * @param id
	 * @return
	 */
	@RequestMapping("user/{id}")
	public String findById(@PathVariable int id, Model model) {
	    UserInfo userInfo = userService.findOne(id);
	    model.addAttribute("userInfo", userInfo);
	    return "userDetail";
//		return JSON.toJSONString(userService.findOne(id));
	}

    /**
     * findByName
     * @param userInfo
     * @return
     */
    @RequestMapping("findByName")
    public String findByName(UserInfo userInfo, Model model) {
        UserInfo user = userService.findByName(userInfo.getName());
        model.addAttribute("userInfo", user);
        return "userDetail";
    }

    /**
     * 添加用户
     */
    @RequestMapping("/addUser")
    public String addUser(){
        return "addUser";
    }

    @RequestMapping("/addUserCommit")
    public String addUserCommit(UserInfo userInfo){
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createTime = sdf.format(date);
            UserInfo user = new UserInfo();
            user.setName(userInfo.getName());
            user.setTel(userInfo.getTel());
            user.setCreateTime(createTime);
            userService.addUser(user);
            logger.info("新增用户ID: " + user.getId());
        } catch (Exception e){
            logger.error("新增用户失败" + e);
        }
        return "redirect:/findUser";
    }
}
