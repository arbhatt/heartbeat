package com.poaex.app.monitor.heartbeat.exception.client;

import com.google.gson.Gson;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;

public class ClientRegistrationException extends ClientException {
    public ClientRegistrationException(String message) {
        super(message);
    }
    public ClientRegistrationException(String pid, String hostname, String processSignature, String identity, Throwable cause) {
        super(new StringBuilder()
                .append(" PID=").append(pid)
                .append(", HOSTNAME=").append(hostname)
                .append(", SIGNATURE=").append(processSignature)
                .append(", IDENTITY=").append(identity)
                .toString() , cause);
    }
    public ClientRegistrationException(Heartbeat hb, Throwable cause) {
        super(new Gson().toJson(hb), cause);
    }

    public ClientRegistrationException(Throwable cause) {
        super(cause);
    }

}
