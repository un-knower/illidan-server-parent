package cn.whaley.datawarehouse.illidan.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
     * job文件中写入内容
     * @param taskName
     */
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
            buffer.append("command=sh submit.sh --taskCode " +taskName +" --startDate ${startDate} --endDate ${endDate}");
            File file = new File(filepath);//文件路径(包括文件名称)
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
        }catch (Exception e){
            log.error("writeFile is err : "+e.getMessage());
            throw new RuntimeException("writeFile is err ...");
        }finally {
            try {
                fos.close();
                pw.close();
            }catch (Exception e){
                log.error("writeFile is err : "+e.getMessage());
                throw new RuntimeException("writeFile is err ...");
            }
        }
    }
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
            log.error("writeFile is err : "+e.getMessage());
            throw new RuntimeException("writeFile is err ...");
        }finally {
            try {
                fos.close();
                pw.close();
            }catch (Exception e){
                log.error("writeFile is err : "+e.getMessage());
                throw new RuntimeException("writeFile is err ...");
            }
        }
    }
    public static void main(String[] args) {
//        FileUtil.createFile("project","flow","task");
        File file = new File("G:\\zip\\project");
        FileUtil.deleteDFile(file);
//        FileUtil.copyFile("G:\\zip","G:\\zip\\project","pro.properties");

/*       List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        FileUtil.writeEndJob("project","flow",list);*/
//        FileUtil.writeJob("project","flow","task","guo.hao@whaley.cn,xiaoming@whaley.cn");
    }

}
