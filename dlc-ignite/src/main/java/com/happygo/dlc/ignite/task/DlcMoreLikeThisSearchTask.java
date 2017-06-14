/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月13日 下午7:35:14
 *
 * @Package com.happygo.dlc.lucene  
 * @Title: DlcMoreLikeThisSearchTask.java
 * @Description: DlcMoreLikeThisSearchTask.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happygo.dlc.ignite.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import com.happgo.dlc.base.DlcConstants;
import com.happgo.dlc.base.bean.DlcLog;
import com.happgo.dlc.base.util.CollectionUtils;
import com.happgo.dlc.base.util.Strings;
import com.happygo.dlc.ignite.callback.DlcMoreLikeThisCallback;
import com.happygo.dlc.log.LuceneAppender;
import com.happygo.dlc.lucene.LuceneIndexSearcher;
import com.happygo.dlc.lucene.LuceneIndexWriter;

/**
 * ClassName:DlcMoreLikeThisSearchTask
 * 
 * @Description: MoreLikeThisSearchTask.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月13日 下午7:35:14
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ignite.compute.ComputeTask#map(java.util.List,
	 * java.lang.Object)
	 */
	@Override
	public Map<? extends ComputeJob, ClusterNode> map(
			List<ClusterNode> subgrid, final String keyWord)
			throws IgniteException {
		Map<ComputeJob, ClusterNode> map = new HashMap<>();
		Iterator<ClusterNode> it = subgrid.iterator();
		Map<String, LuceneIndexWriter> writeMap = LuceneAppender.writerMap;
		for (Map.Entry<String, LuceneIndexWriter> entry : writeMap.entrySet()) {
			if (!it.hasNext()) {
				it = subgrid.iterator();
			}
			final String targetPath = entry.getKey();
			ClusterNode node = it.next();
			map.put(new ComputeJobAdapter() {
				/**
				 * The field serialVersionUID
				 */
				private static final long serialVersionUID = 8846404467071310921L;

				@Override
				public Object execute() {
					if (LOGEER.isDebugEnabled()) {
						LOGEER.debug(">>> Search keyWord '" + keyWord
								+ "' on this node from target path '" + targetPath
								+ "'");
					}
					KeywordAnalyzer analyzer = new KeywordAnalyzer();
					LuceneIndexSearcher indexSearcher = LuceneIndexSearcher
							.indexSearcher(targetPath, analyzer);
					ScoreDoc[] scoreDocs = indexSearcher.matchAllDocsSearch();
					Collection<List<DlcLog>> logQueryResults = ignite.compute()
							.broadcast(
									new DlcMoreLikeThisCallback(scoreDocs,
											targetPath + DlcConstants.SYMBOL_AT + keyWord));
					List<DlcLog> logQueryDlcLogs = new ArrayList<DlcLog>();
					for (Iterator<List<DlcLog>> it = logQueryResults.iterator(); it
							.hasNext();) {
						logQueryDlcLogs.addAll(it.next());
					}
					return logQueryDlcLogs;
				}
			}, node);
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ignite.compute.ComputeTask#reduce(java.util.List)
	 */
	@Override
	public List<DlcLog> reduce(List<ComputeJobResult> results)
			throws IgniteException {
		List<DlcLog> dlcLogs = new ArrayList<DlcLog>();
		if (results == null || results.isEmpty()) {
			return null;
		}
		for (final ComputeJobResult res : results) {
			List<DlcLog> dataList = res.<List<DlcLog>> getData();
			if (dataList == null || dataList.isEmpty()) {
				continue;
			}
			dlcLogs.addAll(dataList);
		}
		return dlcLogs;
	}

	/**
	 * ClassName:DlcMoreLikeThisComputeJob
	 * 
	 * @Description: DlcMoreLikeThisComputeJob.java
	 * @author sxp (1378127237@qq.com)
	 * @date:2017年6月13日 下午9:04:41
	 */
	public static class DlcMoreLikeThisComputeJob extends
			ComputeTaskAdapter<Map<String, ScoreDoc[]>, List<DlcLog>> {

		/**
		 * long the serialVersionUID
		 */
		private static final long serialVersionUID = 5081026869134294235L;
		
		/**
		 * The field LOGEER
		 */
		private static final Logger LOGEER = LogManager.getLogger(DlcMoreLikeThisComputeJob.class);

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.ignite.compute.ComputeTask#map(java.util.List,
		 * java.lang.Object)
		 */
		@Override
		public Map<? extends ComputeJob, ClusterNode> map(
				List<ClusterNode> subgrid,
				Map<String, ScoreDoc[]> targetPathAndScoreDocsMap)
				throws IgniteException {
			Map<ComputeJob, ClusterNode> map = new HashMap<>();
			Iterator<ClusterNode> it = subgrid.iterator();
			final Entry<String, ScoreDoc[]> firstEntry = CollectionUtils
					.getFirstEntry(targetPathAndScoreDocsMap);
			List<ScoreDoc> scoreDocsList = Arrays.asList(firstEntry.getValue());
			int scoreDocPageSize = 10000;
			int modScoreDoc = scoreDocsList.size() % scoreDocPageSize;
			int scoreDocPage = (modScoreDoc == 0) ? scoreDocsList.size()
					/ scoreDocPageSize
					: (scoreDocsList.size() / scoreDocPageSize) + 1;
			for (int scoreDocNum = 0; scoreDocNum < scoreDocPage; scoreDocNum++) {
				if (!it.hasNext()) {
					it = subgrid.iterator();
				}
				ClusterNode node = it.next();
				final List<ScoreDoc> subScoreDocList = scoreDocsList.subList(
						scoreDocNum * scoreDocPageSize, (scoreDocNum + 1)
								* scoreDocPageSize > scoreDocsList.size() ? scoreDocsList.size() : (scoreDocNum + 1)
										* scoreDocPageSize);
				map.put(new ComputeJobAdapter() {
					/**
					 * The field serialVersionUID
					 */
					private static final long serialVersionUID = 8846404467071310921L;

					@Override
					public Object execute() {
						String[] splitStrArray = firstEntry.getKey().split(DlcConstants.SYMBOL_AT);
						String targetPath = splitStrArray[0];
						String keyWord = splitStrArray[1];
						if (LOGEER.isDebugEnabled()) {
							LOGEER.debug(">>> Filter keyWord '" + keyWord
									+ "' on this node from target path '" + targetPath
									+ "'");
						}
						KeywordAnalyzer analyzer = new KeywordAnalyzer();
						LuceneIndexSearcher indexSearcher = LuceneIndexSearcher
								.indexSearcher(targetPath, analyzer);
						return filterScoreDocsAndBuildDlcLogs(subScoreDocList,
								indexSearcher, keyWord);
					}
				}, node);
			}
			return map;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.ignite.compute.ComputeTask#reduce(java.util.List)
		 */
		@Override
		public List<DlcLog> reduce(List<ComputeJobResult> results)
				throws IgniteException {
			List<DlcLog> dlcLogs = new ArrayList<DlcLog>();
			if (results == null || results.isEmpty()) {
				return null;
			}
			for (final ComputeJobResult res : results) {
				List<DlcLog> dataList = res.<List<DlcLog>> getData();
				if (dataList == null || dataList.isEmpty()) {
					continue;
				}
				dlcLogs.addAll(dataList);
			}
			return dlcLogs;
		}

		/**
		 * @MethodName: filterScoreDocsAndBuildDlcLogs
		 * @Description: the filterScoreDocsAndBuildDlcLogs
		 * @param subScoreDocList
		 * @param iSearcher
		 * @param keyWord
		 * @return List<DlcLog>
		 */
		private List<DlcLog> filterScoreDocsAndBuildDlcLogs(
				List<ScoreDoc> subScoreDocList, LuceneIndexSearcher iSearcher,
				String keyWord) {
			if (subScoreDocList == null || subScoreDocList.size() == 0) {
				return null;
			}
			List<DlcLog> dlcLogs = new ArrayList<>();
			DlcLog dlcLog = null;
			Document doc = null;
			for (final ScoreDoc scoreDoc : subScoreDocList) {
				doc = iSearcher.hitDocument(scoreDoc);
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
