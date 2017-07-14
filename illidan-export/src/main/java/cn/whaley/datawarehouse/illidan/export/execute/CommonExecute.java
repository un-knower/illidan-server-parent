package cn.whaley.datawarehouse.illidan.export.execute;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.export.driver.MysqlDriver;
import cn.whaley.datawarehouse.illidan.export.service.HiveService;
import cn.whaley.datawarehouse.illidan.export.util.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by guohao on 2017/7/14.
 */
@Service
 public abstract class CommonExecute {

    @Autowired
    private HiveService hiveService;
    @Autowired
    private DbInfoService dbInfoService;
    public static Queue<List<Object[]>> dataQueue = new ConcurrentLinkedQueue<List<Object[]>>();
    public void start(List<Map<String, Object>> hiveInfo,Map<String,String> map){
        addToQueue(hiveInfo);
        System.out.println("data to queue is success ...");
        execute(hiveInfo,map);
    }
    public abstract void execute(List<Map<String, Object>> hiveInfo,Map<String,String> map);
    /**
     * 数据分片放入队列中
     * @param hiveInfo
     */
    public static void addToQueue(List<Map<String, Object>> hiveInfo){
        int batchNum = ConfigurationManager.getInteger("batchNum");
        int slice = hiveInfo.size()/batchNum;
        System.out.println("slice is "+slice);
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
        return getDriveInfo("hiveDb",map);
    }

    public  Map<String,String> getMysqlDriveInfo(Map<String, String> map){
        return getDriveInfo("mysqlDb",map);
    }

    public Map<String,String> getDriveInfo(String db,Map<String, String> map){
        DbInfoWithStorage dbWithStorageByCode = dbInfoService.getDbWithStorageByCode(map.get(db));
        String url = dbWithStorageByCode.getAddress();
        String driver = dbWithStorageByCode.getDriver();
        String userName = dbWithStorageByCode.getUser();
        String passWord = dbWithStorageByCode.getPassword();
        map.put("url",url);
        map.put("driver",driver);
        map.put("userName",userName);
        map.put("passWord",passWord);
        return map;
    }
}
