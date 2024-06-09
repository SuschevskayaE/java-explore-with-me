package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.entity.CompilationEntity;
import ru.practicum.ewm.main.entity.EventEntity;

import java.util.List;

public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {

    List<CompilationEntity> findAllByPinned(boolean pinned, Pageable pageable);

    @Query("select e from CompilationEntity c " +
            "left join CompilationEventsEntity ce on c.id = ce.compilation.id " +
            "left join EventEntity e  on e.id = ce.event.id " +
            "where c.id = :compilationId ")
    List<EventEntity> getCompilationEvents(@Param("compilationId") Long compilationId);
}
