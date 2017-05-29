package com.happgo.dlc.base;

/**
 * ClassName:DLCException
 * @author sxp
 * @date:2017年5月29日 上午10:37:59
 */
public class DLCException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1366024128692739724L;
	
	private String message;
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * setMessage
	 * void
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param message
	 */
	public DLCException(String message) {
		this(message, null);
	}
	
	/**
	 * @param message
	 */
	public DLCException(String message, Throwable e) {
		super(message, e);
	}
}
