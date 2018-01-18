package cn.whaley.datawarehouse.illidan.server.auth;

import cn.whaley.datawarehouse.illidan.common.util.ConfigUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

/**
 * Created by lituo on 2018/1/12.
 */
public class LoginRequiredInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // 判断该方法是否加了@LoginRequired 注解
        if (!methodInvocation.getMethod().isAnnotationPresent(LoginRequired.class) || AuthService.skipLogin()) {
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
            if (clazz.equals(ModelAndView.class)) {
                String url = ConfigUtils.get("newillidan.sso.server.url");
                String loginCallbackUrl = ConfigUtils.get("newillidan.sso.callback.login");
                url = url + "/user/login?appkey=bigdata-illidan&cb=" + loginCallbackUrl;
                return new ModelAndView(new RedirectView(url));
            }
        }

        //执行被拦截的方法，切记，如果此方法不调用，则被拦截的方法不会被执行。
        return methodInvocation.proceed();
    }
}
