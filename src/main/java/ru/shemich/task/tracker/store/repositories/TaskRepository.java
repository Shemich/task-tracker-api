package ru.shemich.task.tracker.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shemich.task.tracker.store.entities.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
