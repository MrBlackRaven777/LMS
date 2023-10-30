package lms.webinars.service;

import lms.webinars.dto.WebinarDto;

import java.util.List;

public interface IWebinars {
    WebinarDto getWebinarById(Long id);
    WebinarDto getLastWebinar();
    WebinarDto addNewWebinar(WebinarDto webinar);
    WebinarDto updateWebinar(WebinarDto webinar);
    List<WebinarDto> getAllByTheme(String theme);
    List<WebinarDto> getAllByGroup(String group);
    WebinarDto addNewFiles(WebinarDto webinar);
    WebinarDto deleteWebinar(Long id);
    WebinarDto getFile(Long id);
    WebinarDto updateFiles(WebinarDto webinar);
    WebinarDto deleteFile(Long id);
}
