package ru.practicum.ewm.server.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.server.stats.entity.EndpointHitEntity;

import java.time.LocalDateTime;
import java.util.Set;

public interface StatsRepository extends JpaRepository<EndpointHitEntity, Long> {

    @Query("select new ru.practicum.ewm.dto.stats.ViewStats(e.app, e.uri, " +
            "(case when :unique = true " +
            "then count(DISTINCT e.ip) " +
            "else count(e.ip) " +
            "end)) " +
            "from EndpointHitEntity e " +
            "where e.timestamp >= :start " +
            "and e.timestamp <= :end " +
            "and ((:uris) is null or e.uri in (:uris)) " +
            "group by e.app, e.uri " +
            "order by " +
            "(case when :unique = true " +
            "then count(DISTINCT e.ip)  " +
            "else count(e.ip) " +
            "end) desc ")
    Set<ViewStats> getStats(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            @Param("uris") Set<String> uris,
                            @Param("unique") boolean unique);
}
