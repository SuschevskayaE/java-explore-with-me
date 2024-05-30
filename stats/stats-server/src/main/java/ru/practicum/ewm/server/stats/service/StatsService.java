package ru.practicum.ewm.server.stats.service;

import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;

import java.time.LocalDateTime;
import java.util.Set;

public interface StatsService {
    void hit(EndpointHit data);

    Set<ViewStats> stats(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique);
}
