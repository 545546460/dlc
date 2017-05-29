package com.happygo.dlc.lucene.search;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * ClassName:SearchLuceneIndexTest
 * @author sxp
 * @date:2017年5月28日 下午6:47:07
 */
public class SearchLuceneIndexTest {
	
	@Test
	public void testSearchSimpleField() throws Exception {
		Analyzer analyzer = new StandardAnalyzer();
		Directory directory = FSDirectory.open(Paths.get("F:\\lucene_index"));
	    DirectoryReader ireader = DirectoryReader.open(directory);
	    IndexSearcher isearcher = new IndexSearcher(ireader);
	    QueryParser parser = new QueryParser("introduce", analyzer);
	    Query query = parser.parse("sxp");
	    ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
	    for (int i = 0; i < hits.length; i++) {
	      Document hitDoc = isearcher.doc(hits[i].doc);
	      System.out.println("hitDoc name is : " + hitDoc.get("name"));
	      System.out.println("hitDoc introduce is: " + hitDoc.get("introduce"));
	    }
	    ireader.close();
	    directory.close();
	}
	
	@Test
	public void testMultiQueryField() throws Exception {
		String queryStr = "zhangsan";
		String fields[] = {"name", "introduce"};
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
		Query query = parser.parse(queryStr);
		Directory dir = FSDirectory.open(Paths.get("F:\\lucene_index"));
		DirectoryReader iDirectoryReader = DirectoryReader.open(dir);
		
		IndexSearcher iSearcher = new IndexSearcher(iDirectoryReader);
		TopDocs topDocs = iSearcher.search(query, 10000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			Document hitDoc = iSearcher.doc(scoreDocs[i].doc);
			System.out.println("hitDoc name is : " + hitDoc.get("name"));
			System.out.println("hitDoc introduce is: "
					+ hitDoc.get("introduce"));
		}
	    iDirectoryReader.close();
		dir.close();
	}
	
	@Test
	public void testTermQuery() throws Exception {
		Term term = new Term("name", "zhangsan");
		Query query = new TermQuery(term);
		Directory dir = FSDirectory.open(Paths.get("F:\\lucene_index"));
		DirectoryReader iDirectoryReader = DirectoryReader.open(dir);
		
		IndexSearcher iSearcher = new IndexSearcher(iDirectoryReader);
		TopDocs topDocs = iSearcher.search(query, 10000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			Document hitDoc = iSearcher.doc(scoreDocs[i].doc);
			System.out.println("hitDoc name is : " + hitDoc.get("name"));
			System.out.println("hitDoc introduce is: "
					+ hitDoc.get("introduce"));
		}
	    iDirectoryReader.close();
		dir.close();
	}
	
	@Test
	public void testHighLight() throws Exception {
		Term term = new Term("name", "zhangsan");
		Query query = new TermQuery(term);
		//高亮
		Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
		Scorer scorer = new QueryScorer(query);
		Highlighter highlighter  = new Highlighter(formatter, scorer);
		Fragmenter fragmenter = new SimpleFragmenter(20);
		highlighter.setTextFragmenter(fragmenter);
		Directory dir = FSDirectory.open(Paths.get("F:\\lucene_index"));
		DirectoryReader iDirectoryReader = DirectoryReader.open(dir);
		
		IndexSearcher iSearcher = new IndexSearcher(iDirectoryReader);
		TopDocs topDocs = iSearcher.search(query, 10000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			Document hitDoc = iSearcher.doc(scoreDocs[i].doc);
			String name = highlighter.getBestFragment(new StandardAnalyzer(), "name", hitDoc.get("name"));
			System.out.println(name);
		}
	    iDirectoryReader.close();
		dir.close();
	}
}
