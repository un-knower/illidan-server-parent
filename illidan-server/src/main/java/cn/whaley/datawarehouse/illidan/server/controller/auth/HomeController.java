package cn.whaley.datawarehouse.illidan.server.controller.auth;

import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import cn.whaley.datawarehouse.illidan.server.auth.AuthService;
import cn.whaley.datawarehouse.illidan.server.controller.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by lituo on 2018/1/12.
 */

@Controller
@RequestMapping("/")
public class HomeController extends Common {
    private Logger logger = LoggerFactory.getLogger(HomeController.class);


    @RequestMapping(value = "login", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView login(String sso_tn, String url, HttpSession httpSession, ModelAndView mav) {
        Map<String, String> loginUser = getUserFromSession(httpSession);
        Map<String, String> user = AuthService.getUserInfo(sso_tn);
        if (url == null || url.trim().length() == 0) {
            url = ConfigUtils.get("newillidan.entranceUrl");
        }
        if (user == null) {
            if (loginUser != null || AuthService.skipLogin()) {
                return new ModelAndView(new RedirectView(url));
            }
            mav.addObject("msg", "登陆失败，token无效");
            mav.setViewName("error");
            return mav;
        }
        httpSession.setAttribute("user", user);
        return new ModelAndView(new RedirectView(url));

    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView logout(HttpSession httpSession) {
        httpSession.removeAttribute("user");

        String url = ConfigUtils.get("newillidan.sso.server.url");
        String logoutCallbackUrl = ConfigUtils.get("newillidan.sso.callback.logout");
        url = url + "/user/logout?appkey=bigdata-illidan&cb=" + logoutCallbackUrl;

        return new ModelAndView(new RedirectView(url));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView main(HttpSession httpSession, String url) {
        return new ModelAndView(new RedirectView("project/list"));
    }


}
