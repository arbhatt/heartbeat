package com.poaex.app.monitor.heartbeat.svc.controller;

import com.poaex.app.monitor.heartbeat.exception.service.ServiceSecurityException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import com.poaex.app.monitor.heartbeat.model.MonitoringProfile;
import com.poaex.app.monitor.heartbeat.svc.service.HeartbeatService;
import com.poaex.app.monitor.heartbeat.svc.service.IdentityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;


@Slf4j
@RestController
public class HearbeatMonitorController {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private HeartbeatService heartbeatService;

    @RequestMapping(value = "/heartbeat/start", method = RequestMethod.POST)
    public ResponseEntity<Void> startFileScannerAgent(@RequestBody @NotNull MonitoringProfile pr) {
        try {
            if (!isValid(pr)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            heartbeatService.startMonitoring(pr);
        } catch (ServiceSecurityException se) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/heartbeat/stop", method = RequestMethod.POST)
    public ResponseEntity<Void> stopFileScannerAgent(@RequestBody @NotNull MonitoringProfile pr) {
        try {
            if (!isValid(pr)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            heartbeatService.stopMonitoring(pr);
        } catch (ServiceSecurityException se) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/heartbeat/beat", method = RequestMethod.POST)
    public ResponseEntity<Void> postFileStatusUpdate(@RequestBody @NotNull Heartbeat pr) {

        try {
            if (pr.getMonitoringProfileId() == null || pr.getLastheartbeatTime() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            heartbeatService.recordHeartbeat(pr.beat());
        } catch (ServiceSecurityException se) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean isValid(MonitoringProfile heartbeat) {
        return !(StringUtils.isEmpty(heartbeat.getProcessSignature()) ||
                StringUtils.isEmpty(heartbeat.getPid()) ||
                StringUtils.isEmpty(heartbeat.getHostname()));
    }


}