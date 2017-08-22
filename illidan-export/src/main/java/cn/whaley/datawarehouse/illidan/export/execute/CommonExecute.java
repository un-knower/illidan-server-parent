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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guohao on 2017/7/14.
 */

public abstract class CommonExecute {
    private static Logger logger = LoggerFactory.getLogger(CommonExecute.class);
    protected Queue<List<Map<String, Object>>> dataQueue = new ConcurrentLinkedQueue<>();
    protected List<String> hiveColumnNames;

    @Autowired
    private HiveExportService hiveService;

    public void start(Map<String, String> paramMap) {
        List<Map<String, Object>> hiveInfo = hiveService.getHiveInfo(paramMap);
        if (hiveInfo == null || hiveInfo.isEmpty()) {
            System.out.println("没有数据 ... ");
            return;
        }
        hiveColumnNames = new ArrayList<>(hiveInfo.get(0).keySet());

        addToQueue(hiveInfo);

        logger.info("export start ...");
        if(!delete(paramMap)) {
            logger.error("清理数据失败...");
        }

        Map<String, Object> processMap = prepareExecute(paramMap);

        ExecutorService exec = Executors.newFixedThreadPool(ConfigurationManager.getInteger("threadNum"));

        int batchIndex = 0;
        while (!isBreak()) {
            List<Map<String, Object>> data = dataQueue.poll();
            batchIndex ++;
            processMap.put("batchIndex", batchIndex);
            Runnable process = process(data, paramMap, processMap);
            exec.execute(process);
        }
        exec.shutdown();
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
        logger.info("slice num is " + slice);
        for (int i = 1; i <= slice; i++) {
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (int j = batchNum * (i - 1); j < batchNum * i; j++) {
                Map<String, Object> data = hiveInfo.get(j);
                dataList.add(data);
            }
            dataQueue.add(dataList);
        }
        if(hiveInfo.size() % batchNum > 0) {
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (int i = batchNum * slice; i < hiveInfo.size(); i++) {
                Map<String, Object> data = hiveInfo.get(i);
                dataList.add(data);
            }
            dataQueue.add(dataList);
        }
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

    protected abstract boolean delete(Map<String, String> paramMap);

    protected abstract Map<String, Object> prepareExecute(Map<String, String> paramMap);

    protected abstract Runnable process(List<Map<String, Object>> data, Map<String, String> paramMap, Map<String, Object> processMap);

}
