package com.poaex.app.monitor.heartbeat.service.entity;


import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
@NoArgsConstructor
@EqualsAndHashCode
public class HeartbeatLog {
    @Id
    private String id;
    private String instanceId;
    private Long lastNotifiedTime;
}
