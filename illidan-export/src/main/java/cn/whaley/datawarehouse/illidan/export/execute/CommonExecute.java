package cn.whaley.datawarehouse.illidan.export.execute;

import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import cn.whaley.datawarehouse.illidan.export.service.HiveService;
import cn.whaley.datawarehouse.illidan.export.util.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by guohao on 2017/7/14.
 */
@Service
 public abstract class CommonExecute {
    private static Logger logger = LoggerFactory.getLogger(CommonExecute.class);
    @Autowired
    private HiveService hiveService;

    public static Queue<List<Object[]>> dataQueue = new ConcurrentLinkedQueue<List<Object[]>>();
    public void start(List<Map<String, Object>> hiveInfo,Map<String,String> map){
        execute(hiveInfo,map);
    }
    public abstract void execute(List<Map<String, Object>> hiveInfo,Map<String,String> map);
    /**
     * 数据分片放入队列中
     * @param hiveInfo
     */
    public static void addToQueue(List<Map<String, Object>> hiveInfo){
        int batchNum = ConfigurationManager.getInteger("batchNum");
        logger.info("batch size is "+batchNum);
        int slice = hiveInfo.size()/batchNum;
        logger.info("slice num is "+batchNum);
        for(int i=1;i<=slice;i++){
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(int j=batchNum*(i - 1);j<batchNum*i;j++){
                Object[] data = hiveInfo.get(j).values().toArray();
                dataList.add(data);
            }
            dataQueue.add(dataList);
        }
        List<Object[]> dataList = new ArrayList<Object[]>();
        for(int i=batchNum*slice;i<hiveInfo.size();i++){
            Object[] data = hiveInfo.get(i).values().toArray();
            dataList.add(data);
        }
        dataQueue.add(dataList);
        logger.info("add queue is success ...");
    }

    /**
     * 判断队列中是否还有数据
     * @return
     */
    public static Boolean isBreak(){
        return dataQueue.size()==0 ?true:false ;
    }

    /**
     * 获取hive数据
     * @param map
     * @param mysqlDriver
     * @return
     */
    public List<Map<String, Object>> getHiveInfo(Map<String,String> map, MysqlDriver mysqlDriver){
        return hiveService.getHiveInfo(map,mysqlDriver);
    }
    /**
     * 获取驱动的相关信息,同时把已有的数据向下传递
     * @param map
     * @return
     */
    public  Map<String,String> getHiveDriveInfo(Map<String, String> map){
        return hiveService.getHiveDriveInfo(map);
    }
}
