/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年5月29日 下午8:34:34
*
* @Package com.happygo.dlc.lucene  
* @Title: LuceneIndexSearcherTest.java
* @Description: LuceneIndexSearcherTest.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.happygo.dlc.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.BooleanClause.Occur;
import org.junit.Test;

/**
 * ClassName:LuceneIndexSearcherTest
 * @Description: LuceneIndexSearcherTest.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年5月29日 下午8:34:34
 */
public class LuceneIndexSearcherTest {

	@Test
	public void testFuzzySearch() {
		String dirPath = "F:\\lucene_index";
		Analyzer analyzer = new StandardAnalyzer();
		LuceneIndexSearcher luceneIndexSearcher = LuceneIndexSearcher.indexSearcher(dirPath, analyzer);
		ScoreDoc[] scoreDocs = luceneIndexSearcher.fuzzySearch("name", "sxp", "<font color='red'>", 
				"</font>", 20);
		
		Document doc = null;
		for (final ScoreDoc scoreDoc : scoreDocs) {
			doc = luceneIndexSearcher.hitDocument(scoreDoc);
			System.out.println(doc.get("name"));
			System.out.println(luceneIndexSearcher.luceneHighlighter.getBestFragment(analyzer, doc, "name"));
		}
	}
	
	@Test
	public void testMultiFieldSearch() {
		String dirPath = "F:\\lucene_index";
		Analyzer analyzer = new CJKAnalyzer();
		LuceneIndexSearcher luceneIndexSearcher = LuceneIndexSearcher.indexSearcher(dirPath, analyzer);
		String[] queryStrs = {"sxp", "sxp"};
		String[] fields = {"name", "introduce"};
		Occur[] occurs = {Occur.MUST, Occur.MUST};
		ScoreDoc[] scoreDocs = luceneIndexSearcher.multiFieldSearch(queryStrs, fields, occurs, "<font color='red'>", 
				"</font>", 20);
		
		Document doc = null;
		for (final ScoreDoc scoreDoc : scoreDocs) {
			doc = luceneIndexSearcher.hitDocument(scoreDoc);
			System.out.println(doc.get("name"));
			System.out.println(luceneIndexSearcher.luceneHighlighter.getBestFragment(analyzer, doc, "name"));
		}
	}

}
