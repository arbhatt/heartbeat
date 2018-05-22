package com.poaex.app.monitor.heartbeat.service.controller;

import com.poaex.app.monitor.heartbeat.exception.service.ServiceSecurityException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import com.poaex.app.monitor.heartbeat.service.service.HeartbeatService;
import com.poaex.app.monitor.heartbeat.service.service.IdentityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.validation.constraints.NotNull;
import java.util.List;


@Slf4j
@RestController
public class HearbeatMonitorController {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private HeartbeatService heartbeatService;

//    @RequestMapping(value = "/heartbeat/identity", method = RequestMethod.GET)
//    public ResponseEntity<String> getAuthenticationKey(String identityKey) {
//        try {
//            return new ResponseEntity<String>(identityService.getSecretKey(identityKey), HttpStatus.OK);
//
//        } catch (ServiceSecurityException e) {
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//
//    }
//
    @RequestMapping(value = "/heartbeat/start", method = RequestMethod.POST)
    public ResponseEntity<Void> startFileScannerAgent(@RequestBody @NotNull Heartbeat pr) {
        try {
            if (!isValid(pr)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            heartbeatService.startMonitoring(pr.beat().up());
        } catch (ServiceSecurityException se) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


//    @RequestMapping(value = "/heartbeat/list/{identity}", method = RequestMethod.GET)
//    public ResponseEntity<List<Heartbeat>> stopFileScannerAgent(@PathVariable("identity") String identity,
//                                                                @RequestParam("offset") int offset,
//                                                                @RequestParam("size") int size) {
//
//        if (StringUtils.isEmpty(identity)) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        Pageable pageRequest = new PageRequest(offset, size);
//        Page<Heartbeat> resultPage = heartbeatLogRepository.findByIdentity(identity, pageRequest);
//        if (resultPage.getNumberOfElements() == 0) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        HttpHeaders responseHeaders = new HttpHeaders();
//        if (resultPage.hasNext()) {
//            responseHeaders.add(PaginationConstants.PAGE_OFFSET, String.valueOf(offset + 1));
//            responseHeaders.add(PaginationConstants.PAGE_SIZE, String.valueOf(size));
//        }
//
//        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
//    }
//

    @RequestMapping(value = "/heartbeat/stop", method = RequestMethod.POST)
    public ResponseEntity<Void> stopFileScannerAgent(@RequestBody @NotNull Heartbeat pr) {
        try {
            if (!isValid(pr)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            heartbeatService.stopMonitoring(pr.beat().down());
        } catch (ServiceSecurityException se) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //    @RequestMapping(value = "/heartbeat/identity", method = RequestMethod.POST,
//            produces = "application/text", consumes = "application/json")
//    public ResponseEntity<String> createIdentity(@RequestBody @NotNull Heartbeat heartbeat) {
//
//        //Validate
//        if (StringUtils.isEmpty(heartbeat.getHostname())
//                || StringUtils.isEmpty(heartbeat.getEnvironment())
//                || StringUtils.isEmpty(heartbeat.getProcessSignature())) {
//            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
//        }
//
//        try {
//            byte[] encodedValue = identityService.encryptAndEncode(heartbeat.getHostname(), heartbeat.getEnvironment(),
//                    heartbeat.getProcessSignature());
//            return ResponseEntity.ok(new String(encodedValue));
//        } catch (BadPaddingException | IllegalBlockSizeException e) {
//            log.error("Unable to create entity for the request", e);
//            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return ResponseEntity.ok().build();
//    }
//
    @RequestMapping(value = "/heartbeat/beat", method = RequestMethod.POST)
    public ResponseEntity<Void> postFileStatusUpdate(@RequestBody @NotNull Heartbeat pr) {

        try {
            if (!isValid(pr)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            heartbeatService.recordHeartbeat(pr.beat().beat());
        } catch (ServiceSecurityException se) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean isValid(Heartbeat heartbeat) {
        return !(StringUtils.isEmpty(heartbeat.getProcessSignature()) ||
                StringUtils.isEmpty(heartbeat.getPid()) ||
                StringUtils.isEmpty(heartbeat.getHostname()) ||
                StringUtils.isEmpty(heartbeat.getLastheartbeatTime()));
    }


}