/**
 * Copyright  2017
 * <p>
 * All  right  reserved.
 * <p>
 * Created  on  2017年6月1日 下午4:12:19
 *
 * @Package com.happygo.dlc.ignite.task
 * @Title: DlcKeywordSearchTask.java
 * @Description: DlcKeywordSearchTask.java
 * @author sxp (1378127237@qq.com)
 * @version 1.0.0
 */
package com.happygo.dlc.ignite.task;

import com.happgo.dlc.base.DlcConstants;
import com.happgo.dlc.base.bean.DlcLog;
import com.happgo.dlc.base.util.CollectionUtils;
import com.happygo.dlc.logging.LuceneAppender;
import com.happygo.dlc.lucene.LuceneIndexSearcher;
import com.happygo.dlc.lucene.LuceneIndexWriter;

import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import java.io.File;
import java.util.*;

/**
 * ClassName:DlcKeywordSearchTask
 *
 * @author sxp (1378127237@qq.com)
 * @Description: DlcIgniteTask.java
 * @date:2017年6月1日 下午4:12:19
 */
public class DlcKeywordSearchTask extends ComputeTaskAdapter<String, List<DlcLog>> {

    /**
     * The field serialVersionUID
     */
    private static final long serialVersionUID = 2812385577387815111L;

    /**
     * The field LOGEER
     */
    private static final Logger LOGEER = LogManager.getLogger(DlcKeywordSearchTask.class);

    /**
     * map
     *
     * @param subgrid
     * @param keyWord
     * @throws IgniteException
     * @see org.apache.ignite.compute.ComputeTask#map(java.util.List, java.lang.Object)
     */
    public Map<? extends ComputeJob, ClusterNode> map(
            List<ClusterNode> subgrid, final String keyWord)
            throws IgniteException {
        Map<ComputeJob, ClusterNode> map = new HashMap<>();
        Map<String, LuceneIndexWriter> writeMap = LuceneAppender.writerMap;
        Map.Entry<String, LuceneIndexWriter> entry = CollectionUtils.getFirstEntry(writeMap);
        final String targetPath = entry.getKey();
        //如果索引文件夹不存在，直接返回
        if (!new File(targetPath).exists()) {
            return map;
        }
        Iterator<ClusterNode> it = subgrid.iterator();
        ClusterNode node = it.next();
        map.put(new ComputeJobAdapter() {
            /**
             * The field serialVersionUID
             */
            private static final long serialVersionUID = 8846404467071310921L;

            @Override
            public Object execute() {
                if (LOGEER.isDebugEnabled()) {
                    LOGEER.info(">>> Search keyWord '" + keyWord
                            + "' on this node from target path '" + targetPath
                            + "'");
                }
                CJKAnalyzer analyzer = new CJKAnalyzer();
                LuceneIndexSearcher indexSearcher = LuceneIndexSearcher
                        .indexSearcher(targetPath, analyzer);
                ScoreDoc[] scoreDocs = indexSearcher.search(keyWord, 1, 1,
					                		DlcConstants.DLC_HIGHLIGHT_PRE_TAG,
					                        DlcConstants.DLC_HIGHLIGHT_POST_TAG,
					                        DlcConstants.DLC_FRAGMENT_SIZE);
                return buildDlcLogs(scoreDocs, indexSearcher, analyzer);
            }
        }, node);
        return map;
    }

    /**
     * reduce
     *
     * @param results
     * @throws IgniteException
     * @see org.apache.ignite.compute.ComputeTask#reduce(java.util.List)
     */
    public List<DlcLog> reduce(List<ComputeJobResult> results)
            throws IgniteException {
        List<DlcLog> dlcLogs = new ArrayList<DlcLog>();
        if (results == null || results.isEmpty()) {
            return null;
        }
        for (final ComputeJobResult res : results) {
            List<DlcLog> dataList = res.<List<DlcLog>>getData();
            if (dataList == null || dataList.isEmpty()) {
                continue;
            }
            dlcLogs.addAll(dataList);
        }
        return dlcLogs;
    }

    /**
     * @param scoreDocs
     * @param iSearcher
     * @param analyzer
     * @return List<DlcLog>
     * @MethodName: buildDlcLogs
     * @Description: the buildDlcLogs
     */
    public static List<DlcLog> buildDlcLogs(ScoreDoc[] scoreDocs,
                                            LuceneIndexSearcher iSearcher, Analyzer analyzer) {
        if (scoreDocs == null || scoreDocs.length == 0) {
            return null;
        }
        List<DlcLog> dlcLogs = new ArrayList<>();
        DlcLog dlcLog = null;
        Document doc = null;
        for (final ScoreDoc scoreDoc : scoreDocs) {
            doc = iSearcher.hitDocument(scoreDoc);
            String content = doc.get(DlcConstants.DLC_CONTENT);
            String level = doc.get(DlcConstants.DLC_LEVEL);
            long time = (doc.getField(DlcConstants.DLC_TIME)) == null ? 0
                    : (Long) doc.getField(DlcConstants.DLC_TIME).numericValue();
            String hostIp = doc.get(DlcConstants.DLC_HOST_IP);
            String systemName = doc.get(DlcConstants.SYSTEM_NAME);
            dlcLog = new DlcLog(content, level, time, hostIp, systemName);
            dlcLogs.add(dlcLog);
        }
        return dlcLogs;
    }
}
