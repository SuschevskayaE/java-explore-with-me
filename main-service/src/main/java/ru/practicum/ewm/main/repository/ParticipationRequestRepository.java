package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.dto.enums.StatusRequestEnum;
import ru.practicum.ewm.main.entity.ParticipationRequestEntity;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequestEntity, Long> {

    List<ParticipationRequestEntity> findAllByRequesterId(Long userId);

    List<ParticipationRequestEntity> findByEventId(Long eventId);

    Optional<ParticipationRequestEntity> findAllByIdAndRequesterId(Long id, Long userId);

    Optional<ParticipationRequestEntity> findByRequesterIdAndEventId(Long userId, Long eventId);


    @Query("select r from ParticipationRequestEntity r " +
            "left join EventEntity e on r.event.id = e.id " +
            "where e.initiator.id = :userId " +
            "and e.id = :eventId ")
    List<ParticipationRequestEntity> getRequestsUser(@Param("userId") Long userId,
                                                     @Param("eventId") Long eventId);

    @Query("select r from ParticipationRequestEntity r " +
            "left join EventEntity e on r.event.id = e.id " +
            "where e.initiator.id = :userId " +
            "and e.id = :eventId " +
            "and r.id in (:requestIds) ")
    List<ParticipationRequestEntity> getRequestsUserForUpdate(@Param("userId") Long userId,
                                                              @Param("eventId") Long eventId,
                                                              @Param("requestIds") List<Long> requestIds);

    @Query("select count(r) from ParticipationRequestEntity r " +
            "left join EventEntity e on r.event.id = e.id " +
            "where r.event.id = :eventId " +
            "and r.status in (:status) ")
    Long getCountRequestsForEvent(@Param("eventId") Long eventId, @Param("status") StatusRequestEnum status);

    @Query("select r from ParticipationRequestEntity r " +
            "where r.event.id in (:eventId) " +
            "and r.status in (:status) ")
    List<ParticipationRequestEntity> getRequestsForEvents(@Param("eventId") List<Long> eventId, @Param("status") StatusRequestEnum status);


}
