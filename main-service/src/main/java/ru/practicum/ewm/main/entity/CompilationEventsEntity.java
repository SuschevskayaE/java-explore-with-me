package ru.practicum.ewm.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "compilation_events")
@Getter
@Setter
public class CompilationEventsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compilation_id", nullable = false)
    private CompilationEntity compilation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id ", nullable = false)
    private EventEntity event;
}
