package com.poaex.app.monitor.heartbeat.exception.client;

public class ClientException extends Exception {
    public ClientException() {
        super();
    }
    public ClientException(String message) {
        super(message);
    }
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
    public ClientException(Throwable cause) {
        super(cause);
    }
}
