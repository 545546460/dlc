package com.happygo.dlc.lucene.index;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * ClassName:CreateLuceneIndexTest
 * @author sxp
 * @date:2017年5月28日 下午6:46:20
 */
public class CreateLuceneIndexTest {

	@Test
	public void testCreateIndex() throws IOException {
		Analyzer analyzer = new StandardAnalyzer();
		
//		Directory directory = new RAMDirectory();
		Directory directory = FSDirectory.open(Paths.get("F:\\lucene_index"));
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		Document document = new Document();
		document.add(new Field("name", "sxp", TextField.TYPE_STORED));
		document.add(new Field("address", "maanshan", TextField.TYPE_STORED));
		document.add(new Field("age", "27", TextField.TYPE_STORED));
		document.add(new Field("sex", "man", TextField.TYPE_STORED));
		document.add(new Field("introduce", "i am a enginer, my name is sxp", TextField.TYPE_NOT_STORED));
		indexWriter.addDocument(document);
		
		Document document1 = new Document();
		document1.add(new Field("name", "zhangsan", TextField.TYPE_STORED));
		document1.add(new Field("address", "chongqing", TextField.TYPE_STORED));
		document1.add(new Field("age", "29", TextField.TYPE_STORED));
		document1.add(new Field("sex", "man", TextField.TYPE_STORED));
		document1.add(new Field("introduce", "i am a enginer, my name is zhangsan", TextField.TYPE_NOT_STORED));
		indexWriter.addDocument(document1);
		indexWriter.close();
	}
}
