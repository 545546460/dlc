/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月13日 下午9:33:20
 *
 * @Package com.happgo.dlc.base.util  
 * @Title: CollectionUtils.java
 * @Description: CollectionUtils.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happgo.dlc.base.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * ClassName:CollectionUtils
 * 
 * @Description: CollectionUtils.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月13日 下午9:33:20
 */
public class CollectionUtils {

	private CollectionUtils() {}

	/**
	 * @MethodName: getFirstEntry
	 * @Description: the getFirstEntry
	 * @param map
	 * @return Entry<K, V>
	 */
	public static <K, V> Entry<K, V> getFirstEntry(Map<K, V> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		for (Entry<K, V> entry : map.entrySet()) {
			return entry;
		}
		return null;
	}
}
