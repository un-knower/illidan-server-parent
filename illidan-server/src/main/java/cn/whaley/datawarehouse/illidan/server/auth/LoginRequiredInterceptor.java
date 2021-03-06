package cn.whaley.datawarehouse.illidan.server.auth;

import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import cn.whaley.datawarehouse.illidan.server.response.ServerResponse;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lituo on 2018/1/12.
 */
public class LoginRequiredInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // 判断该方法是否加了@LoginRequired 注解
        if (!methodInvocation.getMethod().isAnnotationPresent(LoginRequired.class)
                ||!methodInvocation.getMethod().isAnnotationPresent(RequestMapping.class)
                || UserService.skipLogin()) {
            return methodInvocation.proceed();
        }

        Object[] ars = methodInvocation.getArguments();
        HttpSession httpSession = null;
        for (Object ar : ars) {
            if (ar instanceof HttpSession) {
                httpSession = (HttpSession) ar;
            }
        }
        if (httpSession == null) {
            throw new RuntimeException("内部错误，获取session失败");
        }

        Object user = httpSession.getAttribute("user");

        if (user == null) {
            Class clazz = methodInvocation.getMethod().getReturnType();
            String url = ConfigUtils.get("newillidan.sso.server.url");
            String loginCallbackUrl = ConfigUtils.get("newillidan.sso.callback.login");
            url = url + "/user/login?appkey=bigdata-illidan&cb=" + loginCallbackUrl;
            if (clazz.equals(ModelAndView.class)) {
                return new ModelAndView(new RedirectView(url));
            }
            if (clazz.equals(String.class)) {
                return "redirect:" + url;
            }
            if (clazz.equals(ServerResponse.class)) {
                ServerResponse sr = ServerResponse.responseByError(401, "请登录");
                Map<String, String> data = new HashMap<>();
                data.put("url", url);
                sr.setData(data);
                return sr;
            }
        }

        //执行被拦截的方法，切记，如果此方法不调用，则被拦截的方法不会被执行。
        return methodInvocation.proceed();
    }
}
