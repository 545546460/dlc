/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年5月30日 下午7:21:06
*
* @Package com.happygo.dlc.log  
* @Title: LuceneAppenderTest.java
* @Description: LuceneAppenderTest.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.happygo.dlc.log;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

/**
 * ClassName:LuceneAppenderTest
 * @Description: LuceneAppenderTest.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年5月30日 下午7:21:06
 */
public class LuceneAppenderTest {
	
	private static final String CONFIGURATION_FILE = "src/test/resources/log4j2-lucene.xml";
	
	private LoggerContext loggerContext;
	
	private static final String LOGGER_NAME = "TestLogger";
	
    private static final String LOG_MESSAGE = "Hello Lucene Index!";

	private static final int THREAD_COUNT = 50;
	
	private static final String EXEPECTED_REGEX = "INFO "
			+ LOGGER_NAME + " - " + LOG_MESSAGE;
	
	@Before
	public void setUp() throws FileNotFoundException, IOException {
		 ConfigurationSource source = new ConfigurationSource(new FileInputStream(CONFIGURATION_FILE));
		 loggerContext = Configurator.initialize(null, source);	
	}
	
    @Test
	public void testSimpleThread() throws Exception {
		write();
		verify(3);
	}
    
	@Test
	public void testMultiThreads() throws Exception {
		final ExecutorService threadPool = Executors
				.newFixedThreadPool(THREAD_COUNT);
		final LuceneAppenderRunner runner = new LuceneAppenderRunner();
		for (int i = 0; i < THREAD_COUNT; ++i) {
			threadPool.execute(runner);
		}
		Thread.sleep(3000);
		verify(THREAD_COUNT);
	}
	
	private final void write() throws Exception {
		final LuceneAppender appender = loggerContext.getConfiguration().getAppender("LuceneAppender");
		try {
			appender.start();
			assertTrue("Appender did not start", appender.isStarted());
			final Log4jLogEvent event = Log4jLogEvent.newBuilder()
					.setLoggerName(LOGGER_NAME)
					.setLoggerFqcn(LuceneAppenderTest.class.getName())
					.setLevel(Level.INFO)
					.setMessage(new SimpleMessage(LOG_MESSAGE)).build();
			appender.append(event);
		} finally {
			appender.stop();
		}
		assertFalse("Appender did not stop", appender.isStarted());
	}
	
	private final synchronized void verify(final int exepectedTotalHits)
			throws Exception {
		final FSDirectory fsDir = FSDirectory.open(Paths
				.get("F:\\lucene_index"));
		final IndexReader reader = DirectoryReader.open(fsDir);
		try {
			final IndexSearcher searcher = new IndexSearcher(reader);
			final TopDocs all = searcher.search(new MatchAllDocsQuery(),
					Integer.MAX_VALUE);
			for (ScoreDoc scoreDoc : all.scoreDocs) {
				final Document doc = searcher.doc(scoreDoc.doc);
				assertEquals(4, doc.getFields().size());
				final String field1 = doc.get("level");
				assertTrue("Unexpected field1: " + field1, Level.INFO
						.toString().equals(field1));
				final String field2 = doc.get("content");
				final Pattern pattern = Pattern.compile(EXEPECTED_REGEX);
				final Matcher matcher = pattern.matcher(field2);
				assertTrue("Unexpected field2: " + field2, matcher.matches());
			}
		} finally {
			reader.close();
			fsDir.close();
		}
	}
	
	private class LuceneAppenderRunner implements Runnable {

		public void run() {
			try {
				write();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
