package cn.whaley.datawarehouse.illidan.util;



/**
 * 线程工具类
 * 用于共享数据
 * Created by bk on 2017/2/8.
 */
public class ThreadLocalHelper {
    private static ThreadLocal loginUserThreadLocal = new InheritableThreadLocal();

    public static ThreadLocal clientThreadLocal = new InheritableThreadLocal();

    /**
     * 获取登陆用户id
     * @return
     */
//    public static Long getUserId( ) {
//        return loginUserThreadLocal.get()==null?0l:loginUserThreadLocal.get().getId();
//    }

    /**
     * 获取登录用户名称
     * @return
     */
//    public static String getUserName() {
//        return loginUserThreadLocal.get()==null?"":loginUserThreadLocal.get().getUsername();
//    }

    /**
     * 获取租户ID
     * @return
     */
//    public static Long getTenantId(){
//        return clientThreadLocal.get()==null?0l:clientThreadLocal.get().getTenantId();
//    }

    /**
     * 获取登录用户对象
     * @param user
     */
//    public static void setLoginUser(Sysuser user) {
//        loginUserThreadLocal.set(user);
//    }
    /**
     * 获取 Client
     * @param client
     */
//    public static void setClient(Client client) {
//        clientThreadLocal.set(client);
//    }
}
