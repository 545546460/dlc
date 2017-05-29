package com.happgo.dlc.base;

/**
 * ClassName:Assert
 * @author sxp
 * @date:2017年5月29日 上午10:45:00
 */
public final class Assert {
	
	/**
	 * 
	 */
	private Assert() {}
	
	/**
	 * isNull
	 * boolean
	 */
	public static void isNull(Object obj) {
		boolean isNull = (obj == null) ? true : false;
		if (isNull) {
			throw new DLCException("object is required");
		}
	}
}
