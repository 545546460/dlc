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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.DOMConfiguration;

/**
 * ClassName:LuceneAppenderTest
 * @Description: LuceneAppenderTest.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年5月30日 下午7:21:06
 */
public class LuceneAppenderTest {
	
	private static final String CONFIGURATION_FILE = "log4j2-lucene.xml";
	
	@Rule
	public LoggerContextRule ctx = new LoggerContextRule(CONFIGURATION_FILE);
	
	private static final String LOGGER_NAME = "TestLogger";
	
    private static final String LOG_MESSAGE = "Hello Lucene Index!";

	private static final int THREAD_COUNT = 50;
	
    @Test
    public void testSimpleThread() {
    	write();
    }
    
	@Test
	public void testMultiThreads() {
		final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
		
	}
	
	private final void write() throws Exception {
		final LuceneAppender appender = (LuceneAppender) ctx
				.getRequiredAppender("LuceneAppender");
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
	
	private class LuceneAppenderRunner implements Runnable {

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			
			
		}
	}
}
