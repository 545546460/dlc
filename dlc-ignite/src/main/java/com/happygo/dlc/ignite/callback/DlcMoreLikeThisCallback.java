/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年6月13日 下午9:00:55
*
* @Package DlcMoreLikeThisSearchTask  
* @Title: DlcMoreLikeThisCallback.java
* @Description: DlcMoreLikeThisCallback.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.happygo.dlc.ignite.callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.lucene.search.ScoreDoc;

import com.happgo.dlc.base.bean.DlcLog;
import com.happygo.dlc.ignite.task.DlcMoreLikeThisSearchTask.DlcMoreLikeThisComputeJob;

/**
 * ClassName:DlcMoreLikeThisCallback
 * @Description: DlcMoreLikeThisCallback.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年6月13日 下午9:00:55
 */
public class DlcMoreLikeThisCallback implements IgniteCallable<List<DlcLog>> {

	/**
	 * long the serialVersionUID 
	 */
	private static final long serialVersionUID = 896575173963049507L;
	
	/**
	 * Ignite the ignite 
	 */
	@IgniteInstanceResource
	private Ignite ignite;
	
	/**
	 * ScoreDoc[] the scoreDocs 
	 */
	private ScoreDoc[] scoreDocs;
	
	/**
	 * String the targetPathAndKeyWord 
	 */
	private String targetPathAndKeyWord;
	
	/**
	 * Constructor com.happygo.dlc.ignite.callback.DlcMoreLikeThisCallback
	 * @param scoreDocs
	 * @param targetPathAndKeyWord
	 */
	public DlcMoreLikeThisCallback(ScoreDoc[] scoreDocs, String targetPathAndKeyWord) {
		this.scoreDocs = scoreDocs;
		this.targetPathAndKeyWord = targetPathAndKeyWord;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public List<DlcLog> call() throws Exception {
		Map<String, ScoreDoc[]> targetPathAndScoreDocsMap = new HashMap<>();
		targetPathAndScoreDocsMap.put(targetPathAndKeyWord, scoreDocs);
		return ignite.compute().execute(DlcMoreLikeThisComputeJob.class, targetPathAndScoreDocsMap);
	}
}
