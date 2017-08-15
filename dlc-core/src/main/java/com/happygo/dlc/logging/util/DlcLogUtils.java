/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年8月15日 下午8:13:56
*
* @Package com.happygo.dlc.logging.util  
* @Title: DlcLogUtils.java
* @Description: DlcLogUtils.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.happygo.dlc.logging.util;

import java.util.List;
import java.util.Map;

import com.happgo.dlc.base.util.ClassLoaderUtils;
import com.happygo.dlc.logging.Log4j2LuceneAppender;
import com.happygo.dlc.logging.Log4jLuceneAppender;
import com.happygo.dlc.lucene.LuceneIndexWriter;

/**
 * ClassName:DlcLogUtils
 * @Description: DlcLogUtils.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年8月15日 下午8:13:56
 */
public class DlcLogUtils {
	
	/**
	 * List<String> the queryConditions 
	 */
	private static List<String> queryConditions;
	
	/**
	 * Map<String,LuceneIndexWriter> the writeMap 
	 */
	private static Map<String, LuceneIndexWriter> writeMap;
	
	/**
	 * String[] the LUCENE_APPENDER_TYPE_NAMES 
	 */
	private static final String[] LUCENE_APPENDER_TYPE_NAMES = new String[] {
		"com.happygo.dlc.logging.Log4jLuceneAppender",
		"com.happygo.dlc.logging.Log4j2LuceneAppender"};
	
	/**
	 * Constructor com.happygo.dlc.logging.util.LogUtils
	 */
	private DlcLogUtils() {}
	
	/**
	* @MethodName: getQueryConditionsFromAppender
	* @Description: the getQueryConditionsFromAppender
	* @return List<String>
	*/
	public static List<String> getQueryConditionsFromAppender() {
		if (queryConditions != null && !queryConditions.isEmpty()) {
			return queryConditions;
		}
		ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
		String luceneAppenderName = findType(classLoader);
		if ("Log4jLuceneAppender".equals(luceneAppenderName)) {
			return Log4jLuceneAppender.indexFieldNameList;
		} else if ("Log4j2LuceneAppender".equals(luceneAppenderName)) {
			return Log4j2LuceneAppender.indexFieldNameList;
		}
		return null;
	}
	
	/**
	* @MethodName: getWriteMap
	* @Description: the getWriteMap
	* @return Map<String,LuceneIndexWriter>
	*/
	public static Map<String, LuceneIndexWriter> getWriteMap() {
		if (writeMap != null && !writeMap.isEmpty()) {
			return writeMap;
		}
		ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
		String luceneAppenderName = findType(classLoader);
		if ("Log4jLuceneAppender".equals(luceneAppenderName)) {
			return Log4jLuceneAppender.writerMap;
		} else if ("Log4j2LuceneAppender".equals(luceneAppenderName)) {
			return Log4j2LuceneAppender.writerMap;
		}
		return null;
	}
	
	/**
	* @MethodName: findType
	* @Description: the findType
	* @param classLoader
	* @return String
	*/
	private static String findType(ClassLoader classLoader) {
		for (String name : LUCENE_APPENDER_TYPE_NAMES) {
			try {
				classLoader.loadClass(name);
				String luceneAppenderName = name.substring("com.happygo.dlc.logging.".length());
				return luceneAppenderName;
			} catch (Exception e) {
				// ignore exception
			}
		}
		return null;
	}
}
