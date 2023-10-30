package lms.webinars.service;

import jakarta.transaction.Transactional;
import lms.webinars.domain.entities.FileEntity;
import lms.webinars.domain.entities.WebinarEntity;
import lms.webinars.domain.repo.FilesRepository;
import lms.webinars.domain.repo.WebinarsRepository;
import lms.webinars.dto.FileDto;
import lms.webinars.dto.WebinarDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WebinarsJPA implements IWebinars {

    @Autowired
    WebinarsRepository webRepo;

    @Autowired
    FilesRepository filesRepo;

    @Autowired
    ModelMapper mapper;


    @Override
    public WebinarDto getWebinarById(Long id) {
        WebinarEntity webinar = webRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar with id " + id + " not found"));
        return mapper.map(webinar, WebinarDto.class);
    }

    @Override
    public WebinarDto getLastWebinar() {
        WebinarEntity webinar = webRepo.findFirstByDateBeforeOrderByIdDesc(LocalDate.now()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Last webinar not found"));
        return mapper.map(webinar, WebinarDto.class);
    }

    @Override
    public List<WebinarDto> getAllByTheme(String theme) {
        return webRepo.findAllByTheme(theme).stream()
                .map(w -> mapper.map(w, WebinarDto.class)).toList();
    }

    @Override
    public List<WebinarDto> getAllByGroup(String group) {
        return webRepo.findAllByGroupName(group).stream()
                .map(w -> mapper.map(w, WebinarDto.class)).toList();
    }

    @Override
    @Transactional
    public WebinarDto addNewWebinar(WebinarDto webinar) {
        if (webinar.getId() != null && webRepo.existsById(webinar.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Webinar already exists");

        WebinarEntity newWebinar = mapper.map(webinar, WebinarEntity.class);
        webRepo.save(newWebinar);
        newWebinar.setFiles(webinar.getFiles().stream()
                .map(f -> addNewFile(f, newWebinar))
                .map(fe -> mapper.map(fe, FileEntity.class))
                .peek(filesRepo::save)
                .toList());
        log.info(newWebinar.toString());

        return mapper.map(newWebinar, WebinarDto.class);// toWebinarDto(newWebinar);
    }

    @Override
    @Transactional
    public WebinarDto updateWebinar(WebinarDto webinarDto) {
        WebinarEntity webinarEntity = webRepo.findById(webinarDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar not found with id: " + webinarDto.getId()));

        mapper.map(webinarDto, webinarEntity);
        List<FileEntity> updatedFiles = new ArrayList<>();
        Map<Long, FileEntity> filesMap = webinarEntity.getFiles().stream().collect(Collectors.toMap(FileEntity::getId, Function.identity()));

        webinarDto.getFiles().forEach(fileDto -> {
            if (fileDto.getId() == null) {
                FileEntity newFileEntity = addNewFile(fileDto, webinarEntity);
                updatedFiles.add(newFileEntity);
            } else {
                if (filesMap.containsKey(fileDto.getId())) {
                    updatedFiles.add(updateFileFromDto(filesMap.remove(fileDto.getId()), fileDto));
                }
            }
        });
        updatedFiles.addAll(filesMap.values());
        webinarEntity.setFiles(updatedFiles);

        webRepo.save(webinarEntity);
        return mapper.map(webinarEntity, WebinarDto.class);
    }

    @Override
    public WebinarDto deleteWebinar(Long id) {
        WebinarEntity webinar = webRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar not found with id: " + id));
        webRepo.deleteById(id);
        return mapper.map(webinar, WebinarDto.class);
    }

    @Override
    public WebinarDto getFile(Long id) {
        FileEntity file = filesRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File " + id + " not found"));
        WebinarDto webinar = mapper.map(file.getWebinar(), WebinarDto.class);
        webinar.setFiles(List.of(mapper.map(file, FileDto.class)));
        return webinar;
    }

    @Override
    @Transactional
    public WebinarDto addNewFiles(WebinarDto webinar) {
        WebinarEntity webinarEntity = webRepo.findById(webinar.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar " + webinar.getId() + " not found"));
        webinarEntity.setFiles(webinar.getFiles().stream()
                .map(f -> addNewFile(f, webinarEntity)).toList());
        log.info(webRepo.findById(webinar.getId()).get().toString());
        return mapper.map(webinarEntity, WebinarDto.class);
    }

    private FileEntity addNewFile(FileDto file, WebinarEntity webinar) {
        FileEntity fileEntity = createFileEntity(file, webinar);
        filesRepo.save(fileEntity);
        return fileEntity;
    }

    private FileEntity createFileEntity(FileDto dto, WebinarEntity webinar) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setWebinar(webinar);
        fileEntity.setUrl(dto.getUrl());
        fileEntity.setDescription(dto.getDescription() == null || dto.getDescription().isBlank() ?
                String.format("Webinar's %s: '%s' file", webinar.getGroupName(), webinar.getLabel()) :
                dto.getDescription());
        return fileEntity;
    }

    @Override
    @Transactional
    public WebinarDto updateFiles(WebinarDto webinar) {
        WebinarEntity webinarEntity = webRepo.findById(webinar.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Webinar " + webinar.getId() + " not found"));
        Map<Long, FileDto> filesMap = webinar.getFiles().stream().collect(Collectors.toMap(FileDto::getId, Function.identity()));
        webinarEntity.getFiles().forEach(fe -> updateFileFromDto(fe, filesMap.get(fe.getId())));
        return mapper.map(webinarEntity, WebinarDto.class);
    }

    private FileEntity updateFileFromDto(FileEntity fileEntity, FileDto fileDto) {
        if (fileEntity == null || fileDto == null) return null;
        mapper.map(fileDto, fileEntity);
        return fileEntity;
    }

    @Override
    @Transactional
    public WebinarDto deleteFile(Long id) {
        FileEntity fileEntity = filesRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found with id: " + id));
        filesRepo.delete(fileEntity);
        return mapper.map(fileEntity.getWebinar(), WebinarDto.class);
    }
}
