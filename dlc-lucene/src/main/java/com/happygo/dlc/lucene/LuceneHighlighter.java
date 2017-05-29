package com.happygo.dlc.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import com.happgo.dlc.base.Assert;
import com.happgo.dlc.base.DLCException;

public class LuceneHighlighter {

	private Highlighter highlighter;

	/**
	 * @param preTag
	 * @param postTag
	 * @param query
	 */
	private LuceneHighlighter(String preTag, String postTag, Query query,
			int fragmentSize) {
		Assert.isNull(preTag);
		Assert.isNull(postTag);
		Assert.isNull(query);

		Formatter formatter = new SimpleHTMLFormatter(preTag, postTag);
		Scorer scorer = new QueryScorer(query);
		highlighter = new Highlighter(formatter, scorer);
		Fragmenter fragmenter = new SimpleFragmenter(fragmentSize);
		highlighter.setTextFragmenter(fragmenter);
	}

	/**
	 * highlight LuceneHighlighter
	 */
	public static LuceneHighlighter highlight(String preTag, String postTag,
			Query query, int fragmentSize) {
		return new LuceneHighlighter(preTag, postTag, query, fragmentSize);
	}

	/**
	 * getBestFragment String
	 */
	public String getBestFragment(Analyzer analyzer, Document doc,
			String fieldName) {
		try {
			return highlighter.getBestFragment(new StandardAnalyzer(),
					fieldName, doc.get(fieldName));
		} catch (Exception e) {
			throw new DLCException(e.getMessage(), e);
		}
	}

	/**
	 * getBestFragment String
	 */
	public String getBestFragment(Analyzer analyzer, String fieldName,
			String text) {
		try {
			return highlighter.getBestFragment(new StandardAnalyzer(),
					fieldName, text);
		} catch (Exception e) {
			throw new DLCException(e.getMessage(), e);
		}
	}
}
