package com.poaex.app.monitor.heartbeat.service.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document
@NoArgsConstructor
@EqualsAndHashCode
public class NotificationLog {
    @Id
    private String id;
    private String instanceId;
    private long lastNotifiedTime;
    private List<String> recepients;
    private boolean isNotified;
    private boolean isNotificationSuccess;
}
