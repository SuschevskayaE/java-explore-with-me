package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.dto.*;
import ru.practicum.ewm.main.dto.enums.SortEnum;
import ru.practicum.ewm.main.dto.enums.StateEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<EventShortDto> getEventsByUser(long userId, int from, int size);

    EventFullDto getEventByUser(long userId, long eventId);

    EventFullDto addEvent(long userId, NewEventDto eventDto);

    EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getEventParticipants(long userId, long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<EventShortDto> getShortEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       boolean onlyAvailable, Float lat, Float lon, Float radius, List<Long> locations, String locationName, SortEnum sort, int from, int size);

    EventFullDto getFullEvent(long id);

    List<EventFullDto> getFullEvents(List<Long> users, List<StateEnum> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,Float lat, Float lon, Float radius, List<Long> locations, String locationName, int from, int size);

    EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest eventAdminRequest);

    Map<Long, Long> getHits(List<Long> ids);
}
