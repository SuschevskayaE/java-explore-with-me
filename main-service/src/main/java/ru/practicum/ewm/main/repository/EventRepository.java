package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.dto.enums.StateEnum;
import ru.practicum.ewm.main.entity.EventEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findAllByInitiator_Id(Long userId, Pageable pageable);

    Optional<EventEntity> findByIdAndState(Long eventId, StateEnum state);

    List<EventEntity> findByCategoryId(Long categoryId);

    @Query("select e from EventEntity e " +
            "where ((:users) is null or e.initiator.id in (:users)) " +
            "and ((:states) is null or e.state in (:states)) " +
            "and ((:categories) is null or e.category.id in (:categories)) " +
            "and ((e.eventDate >= :rangeStart) or (cast(:rangeStart as date) is null)) " +
            "and ((e.eventDate <= :rangeEnd) or (cast(:rangeEnd as date) is null)) ")
    List<EventEntity> getFullEvents(@Param("users") List<Long> users,
                                    @Param("states") List<StateEnum> states,
                                    @Param("categories") List<Long> categories,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);


    @Query("select e from EventEntity e " +
            "where ((:text) is null or upper(e.annotation) like upper(concat('%', :text, '%')) or upper(e.description) like upper(concat('%', :text, '%')) ) " +
            "and ((:categories) is null or e.category.id in (:categories)) " +
            "and ((:paid) is null or e.paid = (:paid)) " +
            "and ((:onlyAvailable) = false or e.participantLimit >= 0 ) " +
            "and ((e.eventDate >= :rangeStart) or (cast(:rangeStart as date) is null)) " +
            "and ((e.eventDate <= :rangeEnd) or (cast(:rangeEnd as date) is null)) " +
            "and e.state in (:state) ")
    List<EventEntity> getShortEvents(@Param("text") String text,
                                     @Param("categories") List<Long> categories,
                                     @Param("paid") Boolean paid,
                                     @Param("rangeStart") LocalDateTime rangeStart,
                                     @Param("rangeEnd") LocalDateTime rangeEnd,
                                     @Param("onlyAvailable") Boolean onlyAvailable,
                                     @Param("state") StateEnum state,
                                     Pageable pageable);
}
