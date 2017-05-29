package com.happygo.dlc.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.happgo.dlc.base.Assert;
import com.happgo.dlc.base.DLCException;

/**
 * ClassName:LuceneIndexWriter
 * 
 * @author sxp
 * @date:2017年5月29日 上午10:23:02
 */
public final class LuceneIndexWriter {

	private IndexWriter indexWriter;

	private Directory directory;

	/**
	 * @param analyzer
	 * @param dirPath
	 */
	private LuceneIndexWriter(Analyzer analyzer, String dirPath) {
		Assert.isNull(analyzer);
		Assert.isNull(dirPath);

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try {
			directory = FSDirectory.open(Paths.get(dirPath));
			indexWriter = new IndexWriter(directory, indexWriterConfig);
		} catch (IOException e) {
			throw new DLCException(e.getMessage(), e);
		}
	}

	/**
	 * indexWriter LuceneIndexWriter
	 */
	public static LuceneIndexWriter indexWriter(Analyzer analyzer,
			String dirPath) {
		return new LuceneIndexWriter(analyzer, dirPath);
	}

	/**
	 * addField void
	 */
	public void addField(Document doc, String name, String value,
			FieldType fieldType) {
		Field field = new Field(name, value, fieldType);
		doc.add(field);
	}

	/**
	 * addDocument void
	 */
	public void addDocument(Document doc) {
		try {
			indexWriter.addDocument(doc);
			indexWriter.close();
			directory.close();
		} catch (IOException e) {
			throw new DLCException(e.getMessage(), e);
		}
	}
}
