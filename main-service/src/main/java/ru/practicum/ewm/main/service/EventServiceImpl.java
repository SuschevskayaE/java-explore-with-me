package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.main.common.FromSizeRequest;
import ru.practicum.ewm.main.dto.*;
import ru.practicum.ewm.main.dto.enums.*;
import ru.practicum.ewm.main.entity.*;
import ru.practicum.ewm.main.exeption.BagRequestException;
import ru.practicum.ewm.main.exeption.DataNotFoundException;
import ru.practicum.ewm.main.exeption.ValidationException;
import ru.practicum.ewm.main.mapper.EventMapper;
import ru.practicum.ewm.main.mapper.LocationMapper;
import ru.practicum.ewm.main.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.main.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    private final LocationService locationService;
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;

    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEventsByUser(long userId, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        return eventRepository.findAllByInitiator_Id(userId, pageable)
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(long userId, long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        EventEntity eventEntity = eventRepository.findById(eventId).filter(event -> event.getInitiator().getId() == userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Событие с id %s не найдено", eventId)));
        return eventMapper.toEventFullDto(eventEntity);
    }

    @Override
    public EventFullDto addEvent(long userId, NewEventDto eventDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        CategoryEntity category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new DataNotFoundException(String.format("Категория с id %s не найдена", eventDto.getCategory())));

        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(String.format("Событие должно содержать дату, которая еще не наступила %s", eventDto.getEventDate()));
        }
        LocationEntity locationEntity;
        if (eventDto.getLocation().getId() == null) {
            locationMapper.toEntity(eventDto.getLocation());
            locationEntity = locationService.saveLocation(locationMapper.toEntity(eventDto.getLocation()));
        } else {
            locationEntity = locationRepository.findById(eventDto.getLocation().getId())
                    .orElseThrow(() -> new DataNotFoundException(String.format("Локация с id %s не найдена", eventDto.getLocation().getId())));
        }
        EventEntity entity = eventMapper.toEntity(eventDto);
        entity.setInitiator(user);
        entity.setCategory(category);
        entity.setState(StateEnum.PENDING);
        entity.setCreatedOn(LocalDateTime.now());
        entity.setLocation(locationEntity);
        entity = eventRepository.save(entity);
        return eventMapper.toEventFullDto(entity);
    }

    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        EventEntity eventEntity = eventRepository.findById(eventId).filter(event -> event.getInitiator().getId() == userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Событие с id %s не найдено", eventId)));

        if (eventEntity.getState().equals(StateEnum.PUBLISHED)) {
            throw new ValidationException(String.format("Событие имеет статус %s", eventEntity.getState().getData()));
        }
        if (eventEntity.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(String.format("Событие должно содержать дату, которая еще не наступила %s", eventEntity.getEventDate()));
        }
        if (request.getCategory() != null) {
            categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new DataNotFoundException(String.format("Категория с id %s не найдена", request.getCategory())));
        }
        eventMapper.updateEntity(eventMapper.toEntity(request), eventEntity);
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(StateActionChangeEnum.SEND_TO_REVIEW)) {
                eventEntity.setState(StateEnum.PENDING);
            } else if (request.getStateAction().equals(StateActionChangeEnum.CANCEL_REVIEW)) {
                eventEntity.setState(StateEnum.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(eventEntity));
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipants(long userId, long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        eventRepository.findById(eventId).filter(event -> event.getInitiator().getId() == userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Событие с id %s не найдено", eventId)));

        return participationRequestRepository.getRequestsUser(userId, eventId)
                .stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        EventEntity eventEntity = eventRepository.findById(eventId).filter(event -> event.getInitiator().getId() == userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Событие с id %s не найдено", eventId)));
        if (updateRequest.getStatus().equals(StatusRequestEnum.PENDING)) {
            throw new ValidationException("Неверно указан статус");
        }

        Long countRequest = participationRequestRepository.getCountRequestsForEvent(eventId, StatusRequestEnum.CONFIRMED);

        if (eventEntity.getParticipantLimit() < (countRequest + updateRequest.getRequestIds().size())) {
            throw new ValidationException("The participant limit has been reached");
        }

        List<ParticipationRequestEntity> entities = participationRequestRepository.getRequestsUserForUpdate(userId, eventId, updateRequest.getRequestIds())
                .stream()
                .peek(r -> r.setStatus(updateRequest.getStatus()))
                .map(participationRequestRepository::save).collect(Collectors.toList());

        if (eventEntity.getParticipantLimit() == countRequest + updateRequest.getRequestIds().size()) {
            participationRequestRepository.findByEventIdAndStatusIn(eventId, Collections.singletonList(StatusRequestEnum.PENDING)).stream().filter(r -> r.getStatus().equals(StatusRequestEnum.PENDING))
                    .peek(r -> r.setStatus(StatusRequestEnum.REJECTED))
                    .map(participationRequestRepository::save).collect(Collectors.toList());
        }

        List<ParticipationRequestDto> requestsDto = participationRequestRepository
                .findByEventIdAndStatusIn(eventId, List.of(StatusRequestEnum.CONFIRMED, StatusRequestEnum.REJECTED))
                .stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(
                requestsDto.stream().filter(r -> r.getStatus().equals(StatusRequestEnum.CONFIRMED)).collect(Collectors.toList()),
                requestsDto.stream().filter(r -> r.getStatus().equals(StatusRequestEnum.REJECTED)).collect(Collectors.toList())
        );
    }

    @Override
    public List<EventShortDto> getShortEvents(String text, List<Long> categories, Boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              boolean onlyAvailable, Float lat, Float lon, Float radius, List<Long> locations, String locationName, SortEnum sort, int from, int size) {

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.now().plusYears(5);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BagRequestException("Некоректные даты");
        }
        LocationSearch locationSearch = getLocationSearch(lat, lon, radius, locations, locationName);

        Sort sorting = Sort.by(Sort.Direction.DESC, "id");
        final Pageable pageable = FromSizeRequest.of(from, size, sorting);

        List<EventShortDto> eventShortDtos = eventRepository.getShortEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, StateEnum.PUBLISHED, locationSearch, pageable)
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        List<Long> ids = eventShortDtos.stream().map(EventShortDto::getId).collect(Collectors.toList());

        List<ParticipationRequestEntity> requests = participationRequestRepository.getRequestsForEvents(ids, StatusRequestEnum.CONFIRMED);

        Map<Long, Long> hits = getHits(ids);

        eventShortDtos.stream().peek(i -> {
                    i.setConfirmedRequests(
                            requests.stream().filter(r -> r.getEvent().getId().equals(i.getId()))
                                    .count()
                    );
                    i.setViews(hits.getOrDefault(i.getId(), 0L));
                })
                .collect(Collectors.toList());

        if (sort == SortEnum.VIEWS) {
            eventShortDtos.stream().sorted(Comparator.comparingLong(EventShortDto::getViews));
        } else {
            eventShortDtos.stream().sorted(Comparator.comparing(EventShortDto::getEventDate));
        }

        return eventShortDtos;
    }

    @Override
    public EventFullDto getFullEvent(long id) {
        EventEntity eventEntity = eventRepository.findByIdAndState(id, StateEnum.PUBLISHED)
                .orElseThrow(() -> new DataNotFoundException(String.format("Событие с id %s не найдено", id)));

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventEntity);
        eventFullDto.setConfirmedRequests(participationRequestRepository.getCountRequestsForEvent(id, StatusRequestEnum.CONFIRMED));
        eventFullDto.setViews(getHits(Collections.singletonList(id)).getOrDefault(id, 0L));
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getFullEvents(List<Long> users, List<StateEnum> states,
                                            List<Long> categories, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Float lat, Float lon, Float radius, List<Long> locations, String locationName,
                                            int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);
        LocationSearch locationSearch = getLocationSearch(lat, lon, radius, locations, locationName);

        List<EventFullDto> eventFullDtos = eventRepository.getFullEvents(users, states,
                        categories, rangeStart, rangeEnd, locationSearch, pageable)
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        List<Long> ids = eventFullDtos.stream().map(EventFullDto::getId).collect(Collectors.toList());

        List<ParticipationRequestEntity> requests = participationRequestRepository.getRequestsForEvents(ids, StatusRequestEnum.CONFIRMED);
        Map<Long, Long> hits = getHits(ids);

        eventFullDtos.stream().peek(i -> {
                    i.setConfirmedRequests(
                            requests.stream().filter(r -> r.getEvent().getId().equals(i.getId()))
                                    .count()
                    );
                    i.setViews(hits.getOrDefault(i.getId(), 0L));
                })
                .collect(Collectors.toList());

        return eventFullDtos;
    }

    @Override
    public EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest eventAdminRequest) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Событие с id %s не найдено", eventId)));

        eventMapper.updateEntity(eventMapper.toEntity(eventAdminRequest), eventEntity);
        LocalDateTime now = LocalDateTime.now();

        if (eventEntity.getEventDate().isBefore(now.plusHours(1))) {
            throw new ValidationException(String.format("Дата события %s позднее даты публикации +1ч %s", eventEntity.getEventDate(), now));
        }
        if (eventAdminRequest.getStateAction() != null) {
            if (eventAdminRequest.getStateAction().equals(StateActionEnum.PUBLISH_EVENT)) {
                if (eventEntity.getState().equals(StateEnum.PENDING)) {
                    eventEntity.setPublishedOn(now);
                    eventEntity.setState(StateEnum.PUBLISHED);
                } else {
                    throw new ValidationException(String.format("Событие в статусе %s", eventEntity.getState()));
                }

            } else if (eventAdminRequest.getStateAction().equals(StateActionEnum.REJECT_EVENT)) {
                if (eventEntity.getState().equals(StateEnum.PENDING)) {
                    eventEntity.setState(StateEnum.CANCELED);
                } else {
                    throw new ValidationException(String.format("Событие в статусе %s", eventEntity.getState()));
                }
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(eventEntity));
    }

    @Override
    public Map<Long, Long> getHits(List<Long> ids) {
        Set<String> uris = ids.stream().map(id -> String.format("/events/%s", id)).collect(Collectors.toSet());
        Set<ViewStats> response = statsClient.getStats(LocalDateTime.now().minusDays(365), LocalDateTime.now().plusDays(365), uris, true)
                .getBody();

        Map<Long, Long> hits = new HashMap<>();

        if (response != null) {
            response.stream().map(stat -> {
                Long id = Long.valueOf(stat.getUri().substring(8));
                hits.put(id, stat.getHits());
                return id;
            }).collect(Collectors.toList());
        }
        return hits;
    }

    private LocationSearch getLocationSearch(Float lat, Float lon, Float radius, List<Long> locations, String locationName) {
        LocationSearch locationSearch;
        if (lat == null || lon == null || radius == null) {
            locationSearch = new LocationSearch(locations, 0, 0, 0, locationName);
        } else {
            locationSearch = new LocationSearch(locations, lat, lon, radius, locationName);
        }
        return locationSearch;
    }
}
