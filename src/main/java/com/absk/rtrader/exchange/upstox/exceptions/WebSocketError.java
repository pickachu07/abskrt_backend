package com.absk.rtrader.exchange.upstox.exceptions;

public class WebSocketError extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WebSocketError(String message) {
        super(message);
    }

    public WebSocketError(String message, Throwable cause) {
        super(message, cause);
    }
}