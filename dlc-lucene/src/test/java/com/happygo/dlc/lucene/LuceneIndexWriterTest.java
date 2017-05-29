package com.happygo.dlc.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.junit.Test;

/**
 * ClassName:LuceneIndexWriterTest
 * @Description: LuceneIndexWriterTest.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年5月29日 下午7:32:32
 */
public class LuceneIndexWriterTest {

	@Test
	public void testAddDocument() {
		Analyzer analyzer = new StandardAnalyzer();
		String dirPath = "F:\\lucene_index";
		LuceneIndexWriter lIndexWriter = LuceneIndexWriter.indexWriter(analyzer, dirPath);
		
		Document doc = new Document();
		lIndexWriter.addField(doc, "name", "sxp", TextField.TYPE_STORED);
		lIndexWriter.addField(doc, "age", "27", TextField.TYPE_STORED);
		lIndexWriter.addField(doc, "sex", "man", TextField.TYPE_STORED);
		lIndexWriter.addField(doc, "introduce", "my name is sxp, i am from maanshan", TextField.TYPE_NOT_STORED);
		lIndexWriter.addDocument(doc);
		
		Document doc0 = new Document();
		lIndexWriter.addField(doc0, "name", "syx", TextField.TYPE_STORED);
		lIndexWriter.addField(doc0, "age", "25", TextField.TYPE_STORED);
		lIndexWriter.addField(doc0, "sex", "woman", TextField.TYPE_STORED);
		lIndexWriter.addField(doc0, "introduce", "my name is syx, i am from cq tongliang", TextField.TYPE_NOT_STORED);
		
		lIndexWriter.addDocument(doc0);
		
		lIndexWriter.close();
	}

}
