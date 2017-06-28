package cn.whaley.datawarehouse.illidan.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 创建人：郭浩
 * 创建时间：2017/6/27
 * 程序作用：
 * 数据输入：
 * 数据输出：
 */
public class ZipUtils {
    private static Logger log = LoggerFactory.getLogger(ZipUtils.class);
    public static void doCompress(String srcFile,String zipFile) {
        try {
            doCompress(new File(srcFile), new File(zipFile));
            log.info("do compress is success ... ");
        }catch (Exception e){
            log.error("do compress is error : "+e.getMessage());
        }
    }

    public static void doCompress(String pathname, ZipOutputStream out) throws IOException{
        doCompress(new File(pathname), out);
    }

    private static void doCompress(File srcFile, File destFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destFile));
        if(srcFile.isDirectory()){
            File[] files = srcFile.listFiles();
            for(File file : files){
                doCompress(file, out);
            }
        }else {
            doCompress(srcFile, out);
        }
    }

    /**
     * 文件压缩
     * @param file 目录或者单个文件
     * @param out  压缩后的ZIP文件
     * @throws IOException
     */
    private static void doCompress(File file, ZipOutputStream out) throws IOException {
        if( file.exists() ){
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(file.getName()));
            int len = 0 ;
            // 读取文件的内容,打包到zip文件
            while ((len = fis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.closeEntry();
            fis.close();
        }
    }

    public static void main(String[] args) throws Exception {
        doCompress("G:\\zip\\file","G:\\zip\\zip\\tmp.zip");
    }
}
