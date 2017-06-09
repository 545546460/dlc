/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年5月30日 下午6:44:15
*
* @Package com.happgo.dlc.base  
* @Title: Strings.java
* @Description: Strings.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.happgo.dlc.base;

/**
 * ClassName:Strings
 * @Description: Strings.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年5月30日 下午6:44:15
 */
public class Strings {
	
	/**
	 * Constructor com.happgo.dlc.base.Strings
	 */
	private Strings() {}
	
	/**
	* @MethodName: isNotEmpty
	* @Description: the isNotEmpty
	* @param str
	* @return boolean
	*/
	public static boolean isNotEmpty(String str) {
		return (str == null || "".equals(str)) ? false : true;
	}
	
	/**
	* @MethodName: isEmpty
	* @Description: the isEmpty
	* @param str
	* @return boolean
	*/
	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str)) ? true : false;
	}
	
	/**
	* @MethodName: cutLatersubString
	* @Description: the cutLatersubString
	* @param source
	* @param firstToken
	* @return String
	*/
	public static String cutLatersubString(String source, String firstToken) {
		int firstTokenPosition = source.indexOf(firstToken);
		return source.substring(0, firstTokenPosition);
	}
}
