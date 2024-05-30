package ru.practicum.ewm.server.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.server.stats.entity.EndpointHitEntity;
import ru.practicum.ewm.server.stats.mapper.EndpointHitMapper;
import ru.practicum.ewm.server.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;
    private final EndpointHitMapper mapper;

    @Override
    public void hit(EndpointHit data) {
        EndpointHitEntity entity = mapper.toEntity(data);
        repository.save(entity);
    }

    @Override
    public Set<ViewStats> stats(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique) {
        return repository.getStats(start, end, uris, unique);
    }
}
