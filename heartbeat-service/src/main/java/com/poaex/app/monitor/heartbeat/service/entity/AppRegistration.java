package com.poaex.app.monitor.heartbeat.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private Long startTime;
    private Long downTime;
    private String instanceId;
}
