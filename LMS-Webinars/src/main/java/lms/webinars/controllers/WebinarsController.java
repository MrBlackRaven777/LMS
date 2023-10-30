package lms.webinars.controllers;

import lms.webinars.dto.WebinarRequest;
import lms.webinars.dto.WebinarResponse;
import lms.webinars.service.IWebinars;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static lms.webinars.mappings.Mappings.*;
import static lms.webinars.mappings.APIConstants.*;

@RestController
@Slf4j
public class WebinarsController {
    @Autowired
    IWebinars service;

    @GetMapping(GET_WEBINAR_BY_ID_PATH)
    public WebinarResponse getWebinarById(@PathVariable Long id) {
        WebinarRequest request = new WebinarRequest();
        request.setParams("type", HTTP_GET, "path", GET_WEBINAR_BY_ID_PATH, "paramName", "id", "paramValue", String.valueOf(id));
        log.info(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.getWebinarById(id)));
    }

    @GetMapping(GET_LAST_WEBINAR_PATH)
    public WebinarResponse getLastWebinar() {
        WebinarRequest request = new WebinarRequest();
        request.setParams("type", HTTP_GET, "path", GET_LAST_WEBINAR_PATH);
        log.info(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.getLastWebinar()));
    }

    @GetMapping(GET_GROUP_WEBINARS_PATH)
    public WebinarResponse getGroupWebinars(@PathVariable String group) {
        WebinarRequest request = new WebinarRequest();
        request.setParams("type", HTTP_GET, "path", GET_GROUP_WEBINARS_PATH, "paramName", "group", "paramValue", group);
        log.info(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                service.getAllByGroup(group));
    }

    @GetMapping("/webinars/theme/{theme}")
    public WebinarResponse getThemeWebinars(@PathVariable String theme) {
        WebinarRequest request = new WebinarRequest();
        log.info(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                service.getAllByTheme(theme));
    }


    @PostMapping(POST_ADD_NEW_WEBINAR_PATH)
    public WebinarResponse addWebinar(@RequestBody WebinarRequest request) {
        log.debug(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.addNewWebinar(request.getData())));
    }

    @PutMapping(PUT_UPDATE_WEBINAR_PATH)
    public WebinarResponse updateWebinar(@RequestBody WebinarRequest request) {
        log.debug(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.updateWebinar(request.getData())));
    }

    @DeleteMapping(DELETE_DELETE_WEBINAR_PATH)
    public WebinarResponse deleteWebinar(@PathVariable Long id) {
        WebinarRequest request = new WebinarRequest();
        request.setParams("type", HTTP_DELETE, "path", DELETE_DELETE_WEBINAR_PATH, "paramName", "id", "paramValue", String.valueOf(id));
        log.info(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.deleteWebinar(id)));
    }

    @GetMapping(GET_FILE_BY_ID_PATH)
    public WebinarResponse getFile(@PathVariable Long id) {
        WebinarRequest request = new WebinarRequest();
        request.setParams("type", HTTP_GET, "path", GET_FILE_BY_ID_PATH, "paramName", "id", "paramValue", String.valueOf(id));
        log.info(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.getFile(id)));
    }

    @PostMapping(POST_ADD_NEW_FILES_PATH)
    public WebinarResponse addFiles(@RequestBody WebinarRequest request) {
        log.info(request.toString());
        if (request.getData() == null || request.getData().getFiles() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No files were given");
        }

        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.addNewFiles(request.getData())));

    }

    @PutMapping(PUT_UPDATE_FILES_PATH)
    public WebinarResponse updateFiles(@RequestBody WebinarRequest request) {
        log.debug(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.updateFiles(request.getData())));
    }

    @DeleteMapping(DELETE_DELETE_FILE_PATH)
    public WebinarResponse deleteFile(@PathVariable Long id) {
        WebinarRequest request = new WebinarRequest();
        request.setParams("type", HTTP_DELETE, "path", DELETE_DELETE_FILE_PATH, "paramName", "id", "paramValue", String.valueOf(id));
        log.info(request.toString());
        return new WebinarResponse(request.getRqId(),
                LocalDateTime.now(),
                List.of(service.deleteFile(id)));
    }
}
