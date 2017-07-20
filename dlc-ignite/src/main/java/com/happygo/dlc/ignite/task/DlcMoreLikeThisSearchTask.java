/**
 * Copyright  2017
 * <p>
 * All  right  reserved.
 * <p>
 * Created  on  2017年6月13日 下午7:35:14
 *
 * @Package com.happygo.dlc.lucene
 * @Title: DlcMoreLikeThisSearchTask.java
 * @Description: DlcMoreLikeThisSearchTask.java
 * @author sxp (1378127237@qq.com)
 * @version 1.0.0
 */
package com.happygo.dlc.ignite.task;

import com.happgo.dlc.base.DlcConstants;
import com.happgo.dlc.base.bean.DlcLog;
import com.happgo.dlc.base.util.CollectionUtils;
import com.happgo.dlc.base.util.Strings;
import com.happygo.dlc.log.LuceneAppender;
import com.happygo.dlc.lucene.LuceneIndexSearcher;
import com.happygo.dlc.lucene.LuceneIndexWriter;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskAdapter;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import java.util.*;

/**
 * ClassName:DlcMoreLikeThisSearchTask
 *
 * @author sxp (1378127237@qq.com)
 * @Description: MoreLikeThisSearchTask.java
 * @date:2017年6月13日 下午7 :35:14
 */
public class DlcMoreLikeThisSearchTask extends
        ComputeTaskAdapter<String, List<DlcLog>> {

    /**
     * long the serialVersionUID
     */
    private static final long serialVersionUID = 6134653436779567038L;

    /**
     * The field LOGEER
     */
    private static final Logger LOGEER = LogManager.getLogger(DlcMoreLikeThisSearchTask.class);

    /**
     * Ignite the ignite
     */
    @IgniteInstanceResource
    private Ignite ignite;

    /**
     * Map map.
     *
     * @param subgrid the subgrid
     * @param keyWord the key word
     * @return the map
     * @throws IgniteException the ignite exception
     */
    @Override
    public Map<? extends ComputeJob, ClusterNode> map(
            List<ClusterNode> subgrid, final String keyWord)
            throws IgniteException {
        Map<ComputeJob, ClusterNode> map = new HashMap<>();
        Map<String, LuceneIndexWriter> writeMap = LuceneAppender.writerMap;
        Map.Entry<String, LuceneIndexWriter> entry = CollectionUtils.getFirstEntry(writeMap);
        final String targetPath = entry.getKey();
        if (LOGEER.isDebugEnabled()) {
            LOGEER.debug(">>> Search keyWord '" + keyWord
                    + "' on this node from target path '" + targetPath
                    + "'");
        }

        KeywordAnalyzer analyzer = new KeywordAnalyzer();
        LuceneIndexSearcher indexSearcher = LuceneIndexSearcher
                .indexSearcher(targetPath, analyzer);
        ScoreDoc[] scoreDocs = indexSearcher.matchAllDocsSearch();
        if ((scoreDocs == null) || (scoreDocs.length == 0)) {
            return null;
        }
        List<Document> documents = new ArrayList<Document>(scoreDocs.length);
        Document document = null;
        for (final ScoreDoc scoreDoc : scoreDocs) {
            document = indexSearcher.hitDocument(scoreDoc);
            documents.add(document);
        }

        int docPageSize = 10000;
        List<List<Document>> splitDocList = CollectionUtils.split(documents, docPageSize);
        Iterator<ClusterNode> it = subgrid.iterator();
        for (final List<Document> subDocList : splitDocList) {
            if (!it.hasNext()) {
                it = subgrid.iterator();
            }
            ClusterNode node = it.next();
            map.put(new DlcMoreLikeThisComputeJobAdapter(keyWord, targetPath, subDocList), node);
        }
        return map;
    }

    /**
     * Reduce list.
     *
     * @param results the results
     * @return the list
     * @throws IgniteException the ignite exception
     */
    @Override
    public List<DlcLog> reduce(List<ComputeJobResult> results)
            throws IgniteException {
        if (results == null || results.isEmpty()) {
            return null;
        }
        List<DlcLog> dlcLogs = new ArrayList<DlcLog>();
        List<DlcLog> dataList = null;
        for (final ComputeJobResult res : results) {
            dataList = res.<List<DlcLog>>getData();
            dlcLogs.addAll(dataList);
        }
        return dlcLogs;
    }

    /**
     * The type Dlc more like this compute job adapter.
     */
    static class DlcMoreLikeThisComputeJobAdapter extends ComputeJobAdapter {
        /**
         * The Key word.
         */
        private String keyWord;

        /**
         * The Target path.
         */
        private String targetPath;

        /**
         * The Sub doc list.
         */
        private List<Document> subDocList;

        /**
         * Instantiates a new Dlc more like this compute job adapter.
         *
         * @param keyWord    the key word
         * @param targetPath the target path
         * @param subDocList the sub doc list
         */
        public DlcMoreLikeThisComputeJobAdapter(String keyWord, String targetPath, List<Document> subDocList) {
            this.keyWord = keyWord;
            this.targetPath = targetPath;
            this.subDocList = subDocList;
        }

        /**
         * Execute object.
         *
         * @return the object
         * @throws IgniteException the ignite exception
         */
        @Override
        public Object execute() throws IgniteException {
            if (LOGEER.isDebugEnabled()) {
                LOGEER.debug(">>> Filter keyWord '" + keyWord
                        + "' on this node from target path '" + targetPath
                        + "'");
            }
            return filterDocsAndBuildDlcLogs(subDocList, keyWord);
        }

        /**
         * Filter docs and build dlc logs list.
         *
         * @param subDocList the sub doc list
         * @param keyWord    the key word
         * @return the list
         */
        private List<DlcLog> filterDocsAndBuildDlcLogs(
                List<Document> subDocList, String keyWord) {
            if ((subDocList == null) || (subDocList.size() == 0)) {
                return null;
            }
            List<DlcLog> dlcLogs = new ArrayList<>();
            DlcLog dlcLog = null;
            for (final Document doc : subDocList) {
                String content = doc.get(DlcConstants.DLC_CONTENT);
                if (content.contains(keyWord)) {
                    content = Strings.fillPreAndPostTagOnTargetString(DlcConstants.DLC_HIGHLIGHT_PRE_TAG,
                            DlcConstants.DLC_HIGHLIGHT_POST_TAG, keyWord, content);
                    String level = doc.get(DlcConstants.DLC_LEVEL);
                    long time = (doc.getField(DlcConstants.DLC_TIME)) == null ? 0
                            : (Long) doc.getField(DlcConstants.DLC_TIME)
                            .numericValue();
                    String hostIp = doc.get(DlcConstants.DLC_HOST_IP);
                    String systemName = doc.get(DlcConstants.SYSTEM_NAME);
                    dlcLog = new DlcLog(content, level, time, hostIp,
                            systemName);
                    dlcLogs.add(dlcLog);
                }
            }
            return dlcLogs;
        }
    }
}
