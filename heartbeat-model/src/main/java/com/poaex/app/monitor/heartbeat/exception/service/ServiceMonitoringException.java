package com.poaex.app.monitor.heartbeat.exception.service;

public class ServiceMonitoringException extends ServiceException {
    public ServiceMonitoringException() {
        super();
    }
    public ServiceMonitoringException(String message) {
        super(message);
    }
    public ServiceMonitoringException(String message, Throwable cause) {
        super(message, cause);
    }
    public ServiceMonitoringException(Throwable cause) {
        super(cause);
    }
}
