package ru.shemich.task.tracker.api.factories;

import org.springframework.stereotype.Component;
import ru.shemich.task.tracker.api.dto.TaskStateDto;
import ru.shemich.task.tracker.store.entities.TaskStateEntity;

@Component
public class TaskStateDtoFactory {

    public TaskStateDto makeTaskStateDto(TaskStateEntity enity) {

        return TaskStateDto.builder()
                .id(enity.getId())
                .name(enity.getName())
                .createdAt(enity.getCreatedAt())
                .ordinal(enity.getOrdinal())
                .build();
    }
}
