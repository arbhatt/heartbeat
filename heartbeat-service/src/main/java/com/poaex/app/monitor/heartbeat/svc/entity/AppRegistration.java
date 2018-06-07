package com.poaex.app.monitor.heartbeat.svc.entity;

import com.poaex.app.monitor.heartbeat.model.MonitoringProfile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.UUID;

@Data
@Document
@NoArgsConstructor
@EqualsAndHashCode
public class AppRegistration {
    @Id
    private String id;
    private String pid;
    private String processSignature;
    private String hostname;
    private String environment;
    private String status;
    private Long registrationTime;
    private Long shutdownTime;
    private String instanceId;
    private String monitoringProfileId;
    private String threshold;
    private Map<String, String> customParams;
}
