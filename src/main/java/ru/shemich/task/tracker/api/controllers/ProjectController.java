package ru.shemich.task.tracker.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.shemich.task.tracker.api.dto.AckDto;
import ru.shemich.task.tracker.api.dto.ProjectDto;
import ru.shemich.task.tracker.api.exceptions.BadRequestException;
import ru.shemich.task.tracker.api.exceptions.NotFoundException;
import ru.shemich.task.tracker.api.factories.ProjectDtoFactory;
import ru.shemich.task.tracker.store.entities.ProjectEntity;
import ru.shemich.task.tracker.store.repositories.ProjectRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)//non stable
@Transactional
@RestController
public class ProjectController {

    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;//заинъектили

    private static final String FETCH_PROJECTS = "/api/projects";
    private static final String DELETE_PROJECT = "/api/projects{project_id}";

    private static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";

    @GetMapping(FETCH_PROJECTS)
    public List<ProjectDto> fetchProjects(@RequestParam(value = "prefix_name",required = false) Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName
                .filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAll);

        return projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName
            //Another params...
    ){

        optionalProjectName = optionalProjectName.filter((projectName -> !projectName.trim().isEmpty()));

        boolean isCreate = !optionalProjectId.isPresent();

        final ProjectEntity project = optionalProjectId
                .map(this::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

       if (isCreate && optionalProjectName.isPresent()) {
           throw new BadRequestException("Project name can't be empty.");
       }

       optionalProjectName
               .ifPresent((projectName -> {
                   projectRepository
                           .findByName(projectName)
                           .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                           .ifPresent(anotherProject -> {
                               throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
                           });
                   project.setName(projectName);
               }));

       final ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(savedProject);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AckDto deleteProject(@PathVariable("project_id") Long projectId) {

        ProjectEntity project = getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);

        return AckDto.makeDefault(true);
    }

    private ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with \"%s\" doesnt exist.",
                                        projectId)));
    }
}
