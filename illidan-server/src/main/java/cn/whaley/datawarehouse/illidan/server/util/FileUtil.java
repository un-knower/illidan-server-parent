package cn.whaley.datawarehouse.illidan.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 创建人：郭浩
 * 创建时间：2017/6/27
 * 程序作用：
 * 数据输入：
 * 数据输出：
 */

public class FileUtil {
    private static Logger log = LoggerFactory.getLogger(FileUtil.class);
    /**
     * 文件复制
     * @param newPath
     */
    public static void copyFile(String oldPath ,String newPath,String fileName){
        oldPath= oldPath+File.separator+fileName;
        log.info("copy file oldFilePath is : "+oldPath);
        newPath= newPath+File.separator+fileName;
        log.info("copy file newFilePath is : "+newPath);
        try {
            int bytesum = 0;
            int byteread = 0;
            InputStream inStream = null;
            FileOutputStream fs = null;
            File oldfile = new File(oldPath);
            if (oldfile.exists()){ //文件存在时
                inStream = new FileInputStream(oldPath); //读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
        }catch (Exception e){
            log.error("copyFile is err : "+e.getMessage());
            throw new RuntimeException("copyFile is error ...");
        }
    }

    /**
     * 递归删除目录以及下面子目录和子文件 或者 文件
     * @param file
     */
    public static void deleteDFile(File file){
        if(file.exists()){
            if (file.isDirectory()){
                File[] fileList = file.listFiles();
                for(File f: fileList){
                    if(!f.isDirectory()){
                        f.delete();
                    }else{
                        deleteDFile(f);
                    }
                }
                file.delete();
            }else{
                file.delete();
            }
        }
    }

    /**
     * 创建目录
     * @param destDir
     */
    public static void createDir(String destDir){
        File dir = new File(destDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }
    /**
     * 创建job文件
     * @param projectName
     * @param groupName
     * @param taskName
     */
    public static void createFile(String dirPath,String projectName,String groupName,String taskName){
        dirPath = dirPath + File.separator +projectName + File.separator+groupName;
        //创建目录,project+group
        createDir(dirPath);
        String filePath = dirPath +File.separator+taskName+".job";
        File file = new File(filePath);
        try {
            if(!file.exists()){
                //如果文件不存在，则创建新的文件
                file.createNewFile();
            }
        }catch (Exception e){
            log.error("create file error : "+e.getMessage());
            throw new RuntimeException("create file error ...");
        }
    }


    /**
     * 创建执行job
     * @param map
     * @return
     */
    public static String createEngineJob(Map<String,String> map){
        String taskName = map.get("taskCode");
        StringBuffer buffer = new StringBuffer();
        buffer.append("type=command");
        buffer.append(System.getProperty("line.separator"));
        String command = "command=sh  ../submit_engine.sh --taskCode " +taskName
                +" --startDate ${startDate} --endDate ${endDate} --startHour ${startHour} --endHour ${endHour} " +
                "--dateType " + (map.get("executeType").contains("hour") ? "hour": "day");
        buffer.append(command);
        return buffer.toString();
    }

    /**
     * 创建导出job
     * @param map
     * @return
     */
    public static String createExportJob(Map<String,String> map){
        String taskName = map.get("taskCode");
        taskName = taskName.substring(0,taskName.lastIndexOf("_export"));
        String hiveDb = map.get("hiveDb");
        String hiveTable = map.get("hiveTable");
        String mysqlDb = map.get("mysqlDb");
        String mysqlTable = map.get("mysqlTable");
        StringBuffer buffer = new StringBuffer();
        buffer.append("type=command");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("dependencies="+taskName);
        buffer.append(System.getProperty("line.separator"));
        buffer.append("command=sh  ../submit_export.sh --hiveDb " + hiveDb + " --hiveTable " + hiveTable + " --mysqlDb " + mysqlDb + " --mysqlTable " + mysqlTable + " --platForm 'illidan' "
                + "--startDate ${startDate} --endDate ${endDate} --startHour ${startHour} --endHour ${endHour} "
                + "--dateType " + (map.get("executeType").contains("hour") ? "hour" : "day"));
        return buffer.toString();
    }

    /**
     * 创建错误job文件
     * @param map
     * @return
     */
    public static String createErrorJob(Map<String,String> map){
        String taskName = map.get("taskCode");
        StringBuffer buffer = new StringBuffer();
        buffer.append("type=command");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("command=echo "+taskName+" has some error ...");
        return buffer.toString();
    }

    /**
     * job文件中写入内容
     * @param map
     */
    public static void writeJob(Map<String,String> map){
        String dirPath = map.get("path");
        String projecName = map.get("projectCode");
        String groupName = map.get("groupCode");
        String taskName = map.get("taskCode");
        String jobType = map.get("jobType");
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        String filepath=dirPath+File.separator+projecName+File.separator+groupName+File.separator+taskName+".job";
        String jobContent = "";
        if("engine".equals(jobType)){
            jobContent = createEngineJob(map);
        }else if("export".equals(jobType)){
            jobContent = createExportJob(map);
        }else{
            jobContent = createErrorJob(map);
        }
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(jobContent.toCharArray());
            pw.flush();
        }catch (Exception e){
            log.error("writeFile is err : "+e.getMessage());
            throw new RuntimeException("writeFile is err ...");
        }finally {
           close(fos,pw);
        }
    }

    /**
     * job文件中写入内容
     * @param taskName
     */
    @Deprecated
    public static void writeJob(String dirPath ,String projecName,String groupName,String taskName,String emails){
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        String filepath=dirPath+File.separator+projecName+File.separator+groupName+File.separator+taskName+".job";
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("type=command");
            buffer.append(System.getProperty("line.separator"));
            buffer.append("failure.emails="+emails);
            buffer.append(System.getProperty("line.separator"));
            buffer.append("command=sh  ../submit_engine.sh --taskCode " +taskName +" --startDate ${startDate} --endDate ${endDate}");
            File file = new File(filepath);//文件路径(包括文件名称)
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
        }catch (Exception e){
            log.error("writeFile is err : "+e.getMessage());
            throw new RuntimeException("writeFile is err ...");
        }finally {
            close(fos,pw);
        }
    }

    /**
     * 写入
     * @param dirPath
     * @param projecName
     * @param groupName
     * @param taskNames
     */
    public static void writeEndJob(String dirPath,String projecName,String groupName,List<String> taskNames){
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        String filepath=dirPath+File.separator+projecName+File.separator+groupName+File.separator+groupName+"_end.job";
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("type=command");
            buffer.append(System.getProperty("line.separator"));
            buffer.append("dependencies=");
            for(int i=0;i<taskNames.size()-1;i++){
                buffer.append(taskNames.get(i)).append(",");
            }
            buffer.append(taskNames.get(taskNames.size()-1));
            buffer.append(System.getProperty("line.separator"));
            buffer.append("command=echo ' "+groupName+" is  success '");
            File file = new File(filepath);//文件路径(包括文件名称)
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
        }catch (Exception e){
            log.error("writeEndFile is err : "+e.getMessage());
            throw new RuntimeException("writeEndFile is err ...");
        }finally {
            close(fos,pw);
        }
    }
    public static void write2AzkabanProperties(Map<String,String> map){
        String dirPath = map.get("path");
        String projecName = map.get("projectCode");
        String groupName = map.get("groupCode");
        String emails = map.get("emails");
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        String filepath=dirPath+File.separator+projecName+File.separator+groupName+File.separator+"azkaban.properties";
        StringBuffer buffer = new StringBuffer();
        buffer.append("yyyy=${azkaban.flow.start.year}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("MM=${azkaban.flow.start.month}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("dd=${azkaban.flow.start.day}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("hh=${azkaban.flow.start.hour}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("failure.emails="+emails);
        buffer.append(System.getProperty("line.separator"));
        buffer.append("hh=${azkaban.flow.start.hour}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("backoff=30000");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("retries=1");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("startDate=${yyyy}${MM}${dd}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("endDate=${yyyy}${MM}${dd}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("startHour=${hh}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("endHour=${hh}");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("user.to.proxy=spark");
        buffer.append(System.getProperty("line.separator"));
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
        }catch (Exception e){
            log.error("write2Properties is err : "+e.getMessage());
            throw new RuntimeException("write2Properties is err ...");
        }finally {
            close(fos,pw);
        }
    }

    public static void close(FileOutputStream fos,PrintWriter pw){
        try {
            fos.close();
            pw.close();
        }catch (Exception e){
            log.error("writeFile is err : "+e.getMessage());
            throw new RuntimeException("writeFile is err ...");
        }
    }
    public static void main(String[] args) {

    }

}
