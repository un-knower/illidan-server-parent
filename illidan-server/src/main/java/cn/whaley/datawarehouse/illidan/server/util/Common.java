package cn.whaley.datawarehouse.illidan.server.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 共同类
 */
public class Common {
    public static final String PWD_REG = "((?=.*\\d)(?=.*\\D)|(?=.*[a-zA-Z])(?=.*[^a-zA-Z]))^.{8,16}$";

    public static final String DEPT_ICON = "glyphicon glyphicon-briefcase";
    public static final String POSITION_ICON = "glyphicon glyphicon-user";

    public static final String USER_KEY = "CRM_USER_";
    public static final Long KEY_EXPIRE = 3*24*60*60L;
    public static final String VALIDATE_CODE = "CRM_VALIDATE_CODE_";

    public static final String AUTH_RES_ANY = "CRM_AUTH_RES_ANY";
    public static final String AUTH_RES = "CRM_AUTH_RES_";

    protected HttpServletRequest request = null;
    protected HttpServletResponse response = null;
    protected HttpSession session = null;

//    @Autowired
//    private RedisUtil redisUtil;
//
//    @Autowired
//    private SysuserManager sysuserManager;
//    @Autowired
//    private ExtendAttributeService extendAttributeService;
//    @Autowired
//    private SysDicConvert sysDicConvert;
//    @Autowired
//    private SysDicManager sysDicManager;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
//        ThreadLocalHelper.setLoginUser(getSysuserFromSession());
//        ThreadLocalHelper.setClient(getClientFromSession());
    }
    //
//    public Sysuser getUser(String token) {
//        try {
//            String json = redisUtil.get(USER_KEY + token).toString();
//            return JSON.parseObject(json, Sysuser.class);
//        } catch (Exception e) {
//            return null;
//        }
//    }
    @ModelAttribute("resourceDomain")
    public String getResourceDomain() {
        return "360img.cn";
    }

    @ModelAttribute("webSiteDomain")
    public String getWebsiteDomain() {
        return "360haoyao.com";
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public HttpSession getSession() {
        return this.session;
    }

    protected static final String PROVINCE_KEY = "provinceId";

//    public Long getTenantIdFromSession() {
////        return 1L;
//        Sysuser sysuser = getSysuserFromSession();
//        if(sysuser==null){
//            return -1L;
//        }else{
//            return sysuser.getTenantId();
//        }
////        throw new RuntimeException("login timeout, login again !");
//    }

//    public Client getClientFromSession() {
////        Sysuser nowSysuser = getSysuserFromSession();
//        return Client.buildByTerminalSite(TerminalEnum.TERMINAL_PC, SiteEnum.SITE_MALL, "crm-admin", "1.0", getTenantIdFromSession());
//    }
//
//    public Client getClientFromTenantId(Long tenantId ) {
////        Sysuser nowSysuser = getSysuserFromSession();
//        return Client.buildByTerminalSite(TerminalEnum.TERMINAL_PC, SiteEnum.SITE_MALL, "crm-admin", "1.0", tenantId);
//    }

    private String getUserFromCookie(Cookie[] cookieAry) {
//        if(null == cookieAry || cookieAry.length <= 0){
//            return null;
//        }
//        for(Cookie cookie : cookieAry){
//            if(AdminConstant.USER_LOGIN_NAME.equals(cookie.getName())){
//                return cookie.getValue();
//            }
//        }
        return null;
    }

//    public Sysuser getSysuserFromSession(){
////        if(getRequest()==null){
////            return null;
////        }
////        HttpSession session = getRequest().getSession();
////        return (Sysuser) session.getAttribute("NOW_SYSUSER");
//
//
////        String token = getToken(getRequest());
//        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
//        Object inRedis = redisUtil.get(USER_KEY + sessionId);
//        if(inRedis==null){
//            return null;
//        }else{
//            return JSON.parseObject(inRedis.toString(),Sysuser.class);
//        }
//    }

//    public String getUsernameFromSession() {
//        return getSysuserFromSession().getUsername();
////        return LoginThreadLocalHelper.getUserName();
//    }
//    public Long getUserIdFromSession() {
//        return getSysuserFromSession().getId();
////        return LoginThreadLocalHelper.getUserName();
//    }
//
//    private Long getTenantFromCookie(Cookie[] cookieAry) {
////        if(null == cookieAry || cookieAry.length <= 0){
////            return null;
////        }
////        for(Cookie cookie : cookieAry){
////            if(AdminConstant.USER_LOGIN_TENANT_ID.equals(cookie.getName())){
////                return Long.valueOf(cookie.getValue());
////            }
////        }
//        return null;
//    }


    /**
     * 获取省id
     *
     * @return 省id
     */
    public int getProvinceId() {
        String provinceId = getCookieValue(PROVINCE_KEY);
        if (null == provinceId || provinceId.trim().length() <= 0) {
            return 1;
        }
        return Integer.valueOf(provinceId.trim());
    }

    /**
     * 获得cookie对应的值
     *
     * @param key 对应的键
     * @return 返回值
     */
    public String getCookieValue(String key) {
        Cookie cookie = this.getCookie(key);
        if (cookie != null) {
            try {
                return URLDecoder.decode(cookie.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据键获取对应的cookie对象
     *
     * @param key 对应的key
     * @return key对应的cookie值
     */
    public Cookie getCookie(String key) {
        Cookie[] cookies = this.getRequest().getCookies();
        if (cookies == null || cookies.length < 1) {
            return null;
        }

        for (Cookie temp : cookies) {
            if (temp.getName().equals(key)) {
                return temp;
            }
        }
        return null;
    }

    protected void writeJson(Object obj) {
        PrintWriter out = null;
        this.getResponse().setContentType("application/json; charset=utf-8");
        try {
            out = this.getResponse().getWriter();
            out.write(new ObjectMapper().writeValueAsString(obj));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    protected void outputTemplateJson(List<? extends Object> templates, Long count) {
        PrintWriter out = null;
        try {
            response.setContentType("text/json; charset=utf-8");
            out = response.getWriter();
            out.print("{\"data\" :" + new ObjectMapper().writeValueAsString(templates) + ", \"iTotalRecords\" : " + count + ",\"iTotalDisplayedRecords\" :" + count + "}");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected void returnResult(boolean flag, String msg) {
        PrintWriter out = null;
        try {
            response.setContentType("text/json; charset=utf-8");
            out = response.getWriter();
            String result = "{\"result\" :" + flag + ", \"msg\" : '" + msg + "'}";
            out.print(JSONObject.parseObject(result));
//            out.print(result);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            String result = "{\"result\" :" + false + ", \"msg\" : '" + e.getMessage() + "'}";
            System.out.println("----"+result);
            if (out != null){
                out.print(JSONObject.parseObject(result));
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


//    public String validate(Object object){
//        try{
//            Field[] fields = object.getClass().getDeclaredFields();
//            for(Field field : fields){
//                field.setAccessible(true); //设置些属性是可以访问的
//                FieldAttr fieldAttr = field.getAnnotation(FieldAttr.class);
//                if(fieldAttr == null || !fieldAttr.required()){
//                    continue;
//                }
//                if(field.get(object) == null){
//                    return StringUtils.isEmpty(fieldAttr.desc())? field.getName() : fieldAttr.desc() + "必填";
//                }else if(field.getType() == String.class && StringUtils.isEmpty(String.valueOf(field.get(object)))){
//                    return StringUtils.isEmpty(fieldAttr.desc())? field.getName() : fieldAttr.desc() + "必填";
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return "";
//    }

    //Dept转TreeNode
//    public TreeNode transNode(Dept dept, int level){
//        TreeNode node = new TreeNode();
//        node.setId(dept.getId());
//        node.setText(dept.getName());
//        node.setLevel(level);
//        node.setIcon(DEPT_ICON);
//        node.setType("D");
//
//        return node;
//    }
//    //Position转TreeNode
//    public TreeNode transNode(Position position, int level){
//        TreeNode node = new TreeNode();
//        node.setId(position.getId());
//        node.setText(position.getName());
//        node.setLevel(level);
//        node.setIcon(POSITION_ICON);
//        node.setType("P");
//
//        return node;
//    }

    public String formatDate(Date date){
        if(date == null){
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    public void printException(Logger logger, Exception e){
        logger.error("message:{}\r\n{}\r\n{}\r\n{}\r\n",
                e.toString(), e.getStackTrace(), e.getSuppressed(), e.getCause());
    }

    //spring security
//    public String passwordEncode(String password){
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        return encoder.encode(password);
//    }

//    public Boolean passwordValidate(String password,String passwordInDB){
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        return encoder.matches(password,passwordInDB);
//    }

    //pc onley
//    public String getToken(HttpServletRequest request){
//        String token = "";
//        Cookie[] cookies = request.getCookies();
//        for(Cookie c:cookies){
//            if(c.getName().equals("SESSION")){
//                token = c.getValue();
//                break;
//            }
//        }
//        return token;
//    }

//    public Long getTenantIdbyCode(String Code){
//        return getTenantMap().get(Code.toUpperCase());
//    }
//
//    public Set<String> getTenantCodeSet(){
//        return getTenantMap().keySet();
//    }
//
//    private HashMap<String,Long> getTenantMap(){
//        HashMap<String,Long> tenantMap = new HashMap<>();
//        tenantMap.put("QQ",1L);
//
//        return tenantMap;
//    }
//
//    public static List<File> toFileList(MultipartFile[] list)throws IOException{
//        if(null == list || list.length <= 0){
//            return null;
//        }
//        List<File> fileList = new ArrayList<>(list.length);
//        for(MultipartFile multipartFile:list){
//            File file = toFile(multipartFile);
//            if(null != file){
//                fileList.add(file);
//            }
//        }
//        return fileList;
//    }
//
//    public static File toFile(MultipartFile multipartFile)throws IOException{
//        if(null == multipartFile){
//            return null;
//        }
//        return new File(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
//    }
//
//    private Boolean spAuth(Sysuser su){
//        Boolean sp = false;
//
//        SysDic sysDic = sysDicManager.getByName("audit_deptId",getClientFromSession());
//        if(su.getPositionId()==Long.parseLong(SysDic.getValueByName(sysDic.getValue(),"1"))){
//            sp = true;
//        }
//        if(su.getPositionId()==Long.parseLong(SysDic.getValueByName(sysDic.getValue(),"2"))){
//            sp = true;
//        }
//
//        return sp;
//    }
//
//    public List<Long> getAuthIds(){
//        return getAuthIds(null);
//    }
//    public List<Long> getAuthIds(Sysuser sysuser){
//        Client client;
//        if(sysuser==null){//PC
//            sysuser = getSysuserFromSession();
//            client = getClientFromSession();
//        }else {//APP
//            client = getClientFromTenantId(sysuser.getTenantId());
//        }
//        if(spAuth(sysuser)){
//            return null;
//        }else{
//            List<Long> list = sysuserManager.getAuthIds(sysuser,client);
//            return list;
//        }
//    }
//
//    //特殊方法 只提供PC用
//    public List<String> getAuthNames(){
//        Sysuser su = getSysuserFromSession();
//        if(spAuth(su)){
//            return null;
//        }else{
//            return sysuserManager.getAuthNames(su,getClientFromSession());
//        }
//    }
//
//    public ReturnResult checkFileImg(File file){
//        String name = file.getName();
//        if(StringUtils.isEmpty(name)){
//            return ReturnResult.fail("文件名称为空");
//        }
//        int index = name.lastIndexOf(".");
//        if(index < 0 || index >= name.length()){
//            return ReturnResult.fail("文件名称没有扩展名");
//        }
//        String suffix = name.substring(index+1);
//        switch (suffix.toUpperCase()){
//            case "JPG":
//            case "JPEG":
//            case "PNG":
//                return ReturnResult.succeed("OK");
//            default:
//                return ReturnResult.fail("不是图片类型");
//        }
//    }
//
//
//
//    public void dealExtendAttribute(Client client, List<User> users) {
//        if(users != null && users.size() > 0){
//            List<Long> ids = users.stream().map(x -> x.getId()).collect(Collectors.toList());
//            Map<Long, List<ExtendAttributeVo>> map = extendAttributeService.findResourceAttrval(ExtendAttributeService.ExtendAttributeType.CUSTOMER, ids, client);
//            filterShowAttribute(map, users);
//        }
//    }
//
//    /**
//     * 过滤只剩下列表页展示的属性
//     *
//     * @param map
//     * @return
//     */
//    private void filterShowAttribute(Map<Long, List<ExtendAttributeVo>> map, List<User> users) {
//        for (User user : users) {
//            List<ExtendAttributeVo> extendAttributeVos = map.get(user.getId());
//            if (extendAttributeVos != null) {
//                extendAttributeVos = extendAttributeVos.stream().filter(x -> x.getListShow() == ExtendAttributeVo.LISTSHOW_YES).collect(Collectors.toList());
//                Map<String,String> attributeMap = extendAttributeVos.stream().collect(Collectors.toMap(x->x.getName(),x->x.getValuesStr()));
//                user.setMap(attributeMap);
//            }
//        }
//    }
//
//    public void injectUserName(Visit visit, Client client){
//        try{
//            if(visit == null || visit.getType() == null || visit.getUserId() == null){
//                return;
//            }
//            String convertName = sysDicConvert.getName("visit_type_convert",String.valueOf(visit.getType()),client);
//            if(!org.springframework.util.StringUtils.isEmpty(convertName)){
//                InjectConvert convert = (InjectConvert) ApplicationContextHelper.getBean(convertName);
//                if(convert == null){
//                    return;
//                }
//                visit.setUserName(convert.getName(visit.getUserId(),client));
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    public void dealTask(Task task, Client client) {
//        injectResourceName(task, client);
//        injectRelation(task, client);
//        task.setBeginTimeStr(formatDate(task.getBeginTime()));
//        task.setEndTimeStr(formatDate(task.getEndTime()));
//    }
//
//
//    public void injectResourceName(Task task, Client client) {
//        try {
//            if (task == null || task.getResourceType() == null || task.getResourceId() == null) {
//                return;
//            }
//            String convertName = sysDicConvert.getName("resource_type_convert", String.valueOf(task.getResourceType()), client);
//            if (!org.springframework.util.StringUtils.isEmpty(convertName)) {
//                InjectConvert convert = (InjectConvert) ApplicationContextHelper.getBean(convertName);
//                if (convert == null) {
//                    return;
//                }
//                task.setResourceName(convert.getName(task.getResourceId(), client));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void injectRelation(Task task, Client client) {
//        if (task == null || org.springframework.util.StringUtils.isEmpty(task.getRelationId())) {
//            return;
//        }
//        Sysuser sysuser = new Sysuser();
//        sysuser.setIds(task.getRelationId());
//        List<Sysuser> sysusers = sysuserManager.findBySysuser(sysuser, client);
//        String relationId = sysusers.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.joining(","));
//        String relationName = sysusers.stream().map(Sysuser::getUsername).collect(Collectors.joining(","));
//        task.setRelationId(relationId);
//        task.setRelationName(relationName);
//    }
//
//    public boolean pwdReg(String pwd){
//        Pattern pattern = Pattern.compile(PWD_REG);
//        Matcher matcher = pattern.matcher(pwd);
//
//        return matcher.matches();
//    }
}
