package com.tgb.ccl.simplespring.exception;

public class ServiceException extends Exception{

	private static final long serialVersionUID = 5730339384497062514L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
