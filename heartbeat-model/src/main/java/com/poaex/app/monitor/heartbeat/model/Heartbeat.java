package com.poaex.app.monitor.heartbeat.model;

import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import lombok.Data;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain object for heartbeat
 */
@Data
public class Heartbeat implements Serializable {

    private UUID monitoringProfileId;
    private Long lastheartbeatTime;

    public Heartbeat beat() {
        this.setLastheartbeatTime(Instant.now().toEpochMilli());
        return this;
    }

}
