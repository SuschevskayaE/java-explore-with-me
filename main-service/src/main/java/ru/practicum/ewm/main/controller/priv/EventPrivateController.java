package ru.practicum.ewm.main.controller.priv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.*;
import ru.practicum.ewm.main.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Private: События", description = "Закрытый API для работы с событиями")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "Получение событий, добавленных текущим пользователем")
    public List<EventShortDto> getEventsByUser(@PathVariable long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Получение полной информации о событии добавленном текущим пользователем")
    public EventFullDto getEventByUser(@PathVariable long userId,
                                       @PathVariable long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @PostMapping
    @Operation(summary = "Добавление нового события")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId,
                                 @Valid @RequestBody NewEventDto eventDto) {
        return eventService.addEvent(userId, eventDto);
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "Изменение события добавленного текущим пользователем")
    public EventFullDto updateEventByUser(@PathVariable long userId,
                                          @PathVariable long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest request) {
        return eventService.updateEventByUser(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    @Operation(summary = "Получение информации о запросах на участие в событии текущего пользователя")
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable long userId,
                                                              @PathVariable long eventId) {
        return eventService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @Operation(summary = "Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя")
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable long userId,
                                                              @PathVariable long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return eventService.changeRequestStatus(userId, eventId, updateRequest);
    }

}
