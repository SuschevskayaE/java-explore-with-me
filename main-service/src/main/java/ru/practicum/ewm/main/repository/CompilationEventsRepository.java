package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.entity.CompilationEventsEntity;

public interface CompilationEventsRepository extends JpaRepository<CompilationEventsEntity, Long> {

    void deleteAllByCompilationId(Long compId);
}
