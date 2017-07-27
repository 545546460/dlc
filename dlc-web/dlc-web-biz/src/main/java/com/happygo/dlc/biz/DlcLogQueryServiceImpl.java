/**
 * Copyright  2017
 * <p>
 * All  right  reserved.
 * <p>
 * Created  on  2017年6月4日 上午9:25:42
 *
 * @Package com.happygo.dlc.biz
 * @Title: DlcLogQueryServiceImpl.java
 * @Description: DlcLogQueryServiceImpl.java
 * @author sxp (1378127237@qq.com)
 * @version 1.0.0
 */
package com.happygo.dlc.biz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.happgo.dlc.base.DlcConstants;
import com.happgo.dlc.base.bean.DlcLog;
import com.happgo.dlc.base.bean.PageParam;
import com.happgo.dlc.base.util.CollectionUtils;
import com.happygo.dlc.biz.service.DlcLogQueryService;
import com.happygo.dlc.dal.callback.DlcLogQueryCallback;

/**
 * The type Dlc log query service.
 */
@Service
public class DlcLogQueryServiceImpl implements DlcLogQueryService {

    /**
     * Logger the LOGGER
     */
    private static final Logger LOGGER = LogManager.getLogger(DlcLogQueryServiceImpl.class);

    /**
     * Ignite the ignite
     */
    @Autowired
    private Ignite ignite;

    /**
     * IgniteCache<String,List<List<DlcLog>>> the igniteCache
     */
    private IgniteCache<String, List<List<DlcLog>>> igniteCache;

    /**
     * Init ignite cache.
     *
     * @return void
     * @MethodName: initIgniteCache
     * @Description: the initIgniteCache
     */
    @PostConstruct
    public void initIgniteCache() {
        String cacheName = "dlcLogCache";
        igniteCache = ignite.getOrCreateCache(cacheName);
    }

    /* (non-Javadoc)
     * @see com.happygo.dlc.biz.service.DlcLogQueryService
     * @see #logQuery(java.lang.String, com.happgo.dlc.base.bean.PageParam)
     */
    public List<List<DlcLog>> logQuery(String keyWord, String appName, PageParam pageParam) {
        //1.根据key在IgniteCache查询是否有缓存，如果有直接返回，否则继续第二步
        List<List<DlcLog>> splitLogQueryDlcLogs = igniteCache.get(keyWord);
        if (splitLogQueryDlcLogs != null && !splitLogQueryDlcLogs.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("get keyword[" + keyWord + "] dlc log from ignite cache");
            }
            return splitLogQueryDlcLogs;
        }

        int partitionSize = pageParam.getNumPerPage();

        //2.根据keyword关键字匹配，如果没有匹配到，继续第三步
        List<DlcLog> logQueryDlcLogs = broadcastLogQuery(keyWord, appName, null);
        splitLogQueryDlcLogs = splitLogAndPutInCache(keyWord, partitionSize, logQueryDlcLogs);
        if (!org.springframework.util.CollectionUtils.isEmpty(splitLogQueryDlcLogs)) {
            return splitLogQueryDlcLogs;
        }

        //3.根据keyword进行相似度查询
        logQueryDlcLogs = broadcastLogQuery(keyWord, appName, DlcConstants.DLC_MORE_LIKE_THIS_QUERY_MODE);
        splitLogQueryDlcLogs = splitLogAndPutInCache(keyWord, partitionSize, logQueryDlcLogs);
        return splitLogQueryDlcLogs;
    }

    /**
     * @MethodName: broadcastLogQuery
     * @Description: the broadcastLogQuery
     * @param keyWord
     * @param queryMode
     * @return List<DlcLog>
     */
    private List<DlcLog> broadcastLogQuery(String keyWord, String appName, String queryMode) {
        Collection<List<DlcLog>> logQueryResults = ignite.compute().broadcast(
                new DlcLogQueryCallback(keyWord, appName, queryMode));
        if (logQueryResults == null) {
            return null;
        }
        List<DlcLog> logQueryDlcLogs = new ArrayList<DlcLog>();
        for (Iterator<List<DlcLog>> it = logQueryResults.iterator(); it
                .hasNext(); ) {
            logQueryDlcLogs.addAll(it.next());
        }
        return logQueryDlcLogs;
    }

    /**
     * @MethodName: splitLogAndPutInCache
     * @Description: the splitLogAndPutInCache
     * @param keyWord
     * @param partitionSize
     * @param logQueryDlcLogs
     * @return List<List<DlcLog>>
     */
    private List<List<DlcLog>> splitLogAndPutInCache(String keyWord, int partitionSize,
                                                     List<DlcLog> logQueryDlcLogs) {
        if (logQueryDlcLogs.isEmpty()) {
            return null;
        }
        List<List<DlcLog>> splitLogQueryDlcLogs = CollectionUtils.split(logQueryDlcLogs, partitionSize);
        boolean isSuccess = igniteCache.replace(keyWord, splitLogQueryDlcLogs);
        if (!isSuccess) {
            igniteCache.put(keyWord, splitLogQueryDlcLogs);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("keyword[" + keyWord + "] dlc log put ignite cache");
        }
        return splitLogQueryDlcLogs;
    }
}

