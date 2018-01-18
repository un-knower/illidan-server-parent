package cn.whaley.datawarehouse.illidan.server.controller;

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
import java.util.Map;

/**
 * 共同类
 */
public class Common {
    public static final String PWD_REG = "((?=.*\\d)(?=.*\\D)|(?=.*[a-zA-Z])(?=.*[^a-zA-Z]))^.{8,16}$";

    public static final String DEPT_ICON = "glyphicon glyphicon-briefcase";
    public static final String POSITION_ICON = "glyphicon glyphicon-user";

    public static final String USER_KEY = "CRM_USER_";
    public static final Long KEY_EXPIRE = 3 * 24 * 60 * 60L;
    public static final String VALIDATE_CODE = "CRM_VALIDATE_CODE_";

    public static final String AUTH_RES_ANY = "CRM_AUTH_RES_ANY";
    public static final String AUTH_RES = "CRM_AUTH_RES_";
    protected static final String PROVINCE_KEY = "provinceId";
    protected HttpServletRequest request = null;
    protected HttpServletResponse response = null;
    protected HttpSession session = null;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
//        ThreadLocalHelper.setLoginUser(getSysuserFromSession());
//        ThreadLocalHelper.setClient(getClientFromSession());
    }

    protected Map<String, String> getUserFromSession(HttpSession httpSession) {
        Object user = httpSession.getAttribute("user");
        if (user instanceof Map) {
            return (Map<String, String>) user;
        } else {
            return null;
        }
    }

    protected String getUserNameFromSession(HttpSession httpSession) {
        Map<String, String> user = getUserFromSession(httpSession);
        if (user != null) {
            return user.get("nickname");
        }
        return null;
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

    public void clearCookie(String key) {
        Cookie newCookie = new Cookie(key, null);
        newCookie.setMaxAge(0);
//        newCookie.setPath("/");
        this.getResponse().addCookie(newCookie);
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

    protected String returnResult(boolean flag, String msg) {
        PrintWriter out = null;
        String result;
        try {
            response.setContentType("text/json; charset=utf-8");
            out = response.getWriter();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", flag);
            jsonObject.put("msg", msg);
            result = jsonObject.toString();
            out.print(jsonObject);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", false);
            jsonObject.put("msg", "系统错误");
            result = jsonObject.toString();
            if (out != null) {
                out.print(jsonObject);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return result;
    }

    public boolean codeReg(String str) {
        String regex = "^[a-z0-9A-Z_\\-]+$";
        return str.matches(regex);
    }

    public boolean tableReg(String str) {
        String regex = "^[a-z0-9A-Z_]+$";
        return str.matches(regex);
    }

    public Boolean validateColumnNull(Object columnName) {
        return !(columnName == null || columnName.equals(""));
    }

    public String validateMessage(String columnName) {
        return returnResult(false, columnName + "不能为空!!!");
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

    public String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    public void printException(Logger logger, Exception e) {
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
