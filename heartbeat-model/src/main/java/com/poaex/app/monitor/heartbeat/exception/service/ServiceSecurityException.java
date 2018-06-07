package com.poaex.app.monitor.heartbeat.exception.service;

public class ServiceSecurityException extends ServiceException {
    public ServiceSecurityException() {
        super();
    }
    public ServiceSecurityException(String message) {
        super(message);
    }
    public ServiceSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
    public ServiceSecurityException(Throwable cause) {
        super(cause);
    }
}
