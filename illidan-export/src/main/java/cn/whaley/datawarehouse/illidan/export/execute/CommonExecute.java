package cn.whaley.datawarehouse.illidan.export.execute;

import cn.whaley.datawarehouse.illidan.export.service.HiveExportService;
import cn.whaley.datawarehouse.illidan.export.util.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by guohao on 2017/7/14.
 */

public abstract class CommonExecute {
    private static Logger logger = LoggerFactory.getLogger(CommonExecute.class);
    public Queue<List<Object[]>> dataQueue = new ConcurrentLinkedQueue<List<Object[]>>();

    @Autowired
    private HiveExportService hiveService;

    public void start(Map<String, String> map) {
        List<Map<String, Object>> hiveInfo = hiveService.getHiveInfo(map);
        if (hiveInfo == null || hiveInfo.isEmpty()) {
            System.out.println("没有数据 ... ");
            return;
        }
        addToQueue(hiveInfo);

        execute(hiveInfo, map);
    }

    /**
     * 数据分片放入队列中
     *
     * @param hiveInfo
     */
    private void addToQueue(List<Map<String, Object>> hiveInfo) {
        int batchNum = ConfigurationManager.getInteger("batchNum");
        logger.info("batch size is " + batchNum);
        int slice = hiveInfo.size() / batchNum;
        logger.info("slice num is " + batchNum);
        for (int i = 1; i <= slice; i++) {
            List<Object[]> dataList = new ArrayList<Object[]>();
            for (int j = batchNum * (i - 1); j < batchNum * i; j++) {
                Object[] data = hiveInfo.get(j).values().toArray();
                dataList.add(data);
            }
            dataQueue.add(dataList);
        }
        List<Object[]> dataList = new ArrayList<Object[]>();
        for (int i = batchNum * slice; i < hiveInfo.size(); i++) {
            Object[] data = hiveInfo.get(i).values().toArray();
            dataList.add(data);
        }
        dataQueue.add(dataList);
        logger.info("add queue is success ...");
    }

    /**
     * 判断队列中是否还有数据
     *
     * @return
     */
    public Boolean isBreak() {
        return dataQueue.size() == 0;
    }

    public abstract void execute(List<Map<String, Object>> hiveInfo, Map<String, String> map);

}
