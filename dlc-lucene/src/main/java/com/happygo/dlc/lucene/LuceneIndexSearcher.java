package com.happygo.dlc.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.happgo.dlc.base.Assert;
import com.happgo.dlc.base.DLCException;

/**
 * ClassName:LuceneIndexSearcher
 * 
 * @author sxp
 * @date:2017年5月29日 上午11:07:29
 */
public final class LuceneIndexSearcher {

	public LuceneHighlighter luceneHighlighter;

	public Analyzer analyzer;

	private IndexSearcher indexSearcher;

	private Directory directory;

	/**
	 * @param dirPath
	 * @param analyzer
	 */
	private LuceneIndexSearcher(String dirPath, Analyzer analyzer) {
		Assert.isNull(dirPath);

		try {
			directory = FSDirectory.open(Paths.get(dirPath));
			DirectoryReader iDirectoryReader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(iDirectoryReader);
		} catch (IOException e) {
			throw new DLCException(e.getMessage(), e);
		}
		this.analyzer = analyzer;
	}

	/**
	 * indexSearcher LuceneIndexSearcher
	 */
	public static LuceneIndexSearcher indexSearcher(String dirPath,
			Analyzer analyzer) {
		return new LuceneIndexSearcher(dirPath, analyzer);
	}

	/**
	 * fuzzySearch ScoreDoc[]
	 */
	public ScoreDoc[] fuzzySearch(String fieldName, String text, String preTag,
			String postTag, int fragmentSize) {
		Term term = new Term(fieldName, text);
		Query query = new FuzzyQuery(term);
		luceneHighlighter = LuceneHighlighter.highlight(preTag, postTag, query,
				fragmentSize);
		ScoreDoc[] scoreDocs;
		try {
			scoreDocs = indexSearcher.search(query, 10000).scoreDocs;
		} catch (IOException e) {
			throw new DLCException(e.getMessage(), e);
		}
		return scoreDocs;
	}

	/**
	 * multiFieldSearch
	 * ScoreDoc[]
	 */
	public ScoreDoc[] multiFieldSearch(String[] queryStrs, String[] fields, Occur[] occurs,
			String preTag, String postTag, int fragmentSize) {
		ScoreDoc[] scoreDocs;
		try {
			Query query = MultiFieldQueryParser.parse(queryStrs, fields, occurs, analyzer);
			luceneHighlighter = LuceneHighlighter.highlight(preTag, postTag,
					query, fragmentSize);
			scoreDocs = indexSearcher.search(query, 10000).scoreDocs;
		} catch (Exception e) {
			throw new DLCException(e.getMessage(), e);
		}
		return scoreDocs;
	}
}
