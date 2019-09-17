package com.absk.rtrader.exchange.upstox.exceptions;


public class AuthenticationMissingException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthenticationMissingException(String message) {
        super(message);
    }

    public AuthenticationMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
