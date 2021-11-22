package ru.shemich.task.tracker.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shemich.task.tracker.store.entities.ProjectEnity;

public interface ProjectRepository extends JpaRepository<ProjectEnity, Long> {

}
