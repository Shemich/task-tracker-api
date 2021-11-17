package ru.shemich.task.tracker.store.entities;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE) //экспериментальная фича, аккуратнее с ее использованием
@Entity
@Table(name = "project")
public class ProjectEnity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long Id;

    @Column(unique = true)
    String name;

    @Builder.Default
    Instant createdAt = Instant.now();

    @Builder.Default
    @OneToMany
    List<TaskStateEntity> taskStates = new ArrayList<>();

}
