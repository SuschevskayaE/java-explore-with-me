package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.common.FromSizeRequest;
import ru.practicum.ewm.main.dto.CompilationDto;
import ru.practicum.ewm.main.dto.EventShortDto;
import ru.practicum.ewm.main.dto.NewCompilationDto;
import ru.practicum.ewm.main.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.entity.CompilationEntity;
import ru.practicum.ewm.main.entity.CompilationEventsEntity;
import ru.practicum.ewm.main.entity.EventEntity;
import ru.practicum.ewm.main.exeption.DataNotFoundException;
import ru.practicum.ewm.main.mapper.CompilationMapper;
import ru.practicum.ewm.main.mapper.EventMapper;
import ru.practicum.ewm.main.repository.CompilationEventsRepository;
import ru.practicum.ewm.main.repository.CompilationRepository;
import ru.practicum.ewm.main.repository.EventRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CompilationEventsRepository compilationEventsRepository;
    private final EventService eventService;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        CompilationEntity entity = compilationRepository.save(compilationMapper.toEntity(compilationDto));

        if (compilationDto.getEvents() == null || compilationDto.getEvents().isEmpty()) {
            compilationDto.setEvents(Collections.emptyList());
        }
        if (!compilationDto.getEvents().isEmpty()) {
            saveAllEvents(compilationDto.getEvents(), entity);
        }

        CompilationDto dto = compilationMapper.toCompilationDto(entity);
        if (compilationDto.getEvents() != null) {
            List<EventShortDto> events = compilationRepository.getCompilationEvents(entity.getId()).stream()
                    .map(eventMapper::toEventShortDto)
                    .collect(Collectors.toList());
            dto.setEvents(events);
        } else {
            dto.setEvents(Collections.emptyList());
        }
        return dto;
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest compilationRequest) {
        CompilationEntity compilationEntity = compilationMapper.toEntity(compilationRequest);
        compilationEntity.setId(compId);

        CompilationEntity entity = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Подборка с id %s не найдена", compId)));

        if (compilationRequest.getEvents() == null || compilationRequest.getEvents().isEmpty()) {
            compilationRequest.setEvents(Collections.emptyList());
        } else {
            compilationEventsRepository.deleteAllByCompilationId(compId);
            saveAllEvents(compilationRequest.getEvents(), entity);
        }

        compilationMapper.updateEntity(compilationEntity, entity);
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationRepository.save(entity));

        List<EventShortDto> events = compilationRepository.getCompilationEvents(compId).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        if (events.get(0) != null) {
            List<Long> ids = events.stream().map(EventShortDto::getId).collect(Collectors.toList());

            Map<Long, Long> hits = eventService.getHits(ids);
            events = events.stream().peek(i -> i.setViews(hits.getOrDefault(i.getId(), 0L))).collect(Collectors.toList());
            compilationDto.setEvents(events);
        } else {
            compilationDto.setEvents(events);
        }
        return compilationDto;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);

        if (pinned == null) {
            return compilationRepository.findAll(pageable)
                    .stream()
                    .map(compilationMapper::toCompilationDto)
                    .peek(dto ->
                            dto.setEvents(compilationRepository.getCompilationEvents(dto.getId()).stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()))
                    )
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAllByPinned(pinned, pageable)
                    .stream()
                    .map(compilationMapper::toCompilationDto)
                    .peek(dto ->
                            dto.setEvents(compilationRepository.getCompilationEvents(dto.getId()).stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()))
                    )
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDto getCompilation(long compId) {

        CompilationDto compilationDto = compilationRepository.findById(compId).map(compilationMapper::toCompilationDto)
                .orElseThrow(() -> new DataNotFoundException(String.format("Подборка с id %s не найдена", compId)));
        List<EventShortDto> eventShortDtos = compilationRepository.getCompilationEvents(compId).stream().map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
        if (eventShortDtos.get(0) == null) {
            eventShortDtos = new ArrayList<>();
        }
        compilationDto.setEvents(eventShortDtos);
        return compilationDto;
    }

    private void saveAllEvents(List<Long> eventIds, CompilationEntity compilationEntity) {
        List<EventEntity> events = eventRepository.findAllById(eventIds);

        if (events.size() != eventIds.size()) {
            throw new DataNotFoundException("Найдены не все события!");
        }

        List<CompilationEventsEntity> compilationEvents = events.stream()
                .map(e -> {
                    CompilationEventsEntity compilationEvent = new CompilationEventsEntity();
                    compilationEvent.setCompilation(compilationEntity);
                    compilationEvent.setEvent(e);
                    return compilationEvent;
                })
                .collect(Collectors.toList());

        compilationEventsRepository.saveAll(compilationEvents);
    }
}
