package ru.practicum.ewm.main.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.main.dto.EventFullDto;
import ru.practicum.ewm.main.dto.EventShortDto;
import ru.practicum.ewm.main.dto.enums.SortEnum;
import ru.practicum.ewm.main.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Public: События", description = "Публичный API для работы с событиями")
@Validated
public class EventPublicController {

    private final EventService eventService;

    private final StatsClient statsClient;

    @Value("${service-name}")
    private String serviceName;

    @GetMapping
    @Operation(summary = "Получение событий с возможностью фильтрации")
    public List<EventShortDto> getShortEvents(@RequestParam(required = false) String text,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) Boolean paid,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                              @RequestParam(defaultValue = "false", required = false) boolean onlyAvailable,
                                              @RequestParam(required = false) Float lat,
                                              @RequestParam(required = false) Float lon,
                                              @RequestParam(required = false) Float radius,
                                              @RequestParam(required = false) List<Long> locations,
                                              @RequestParam(required = false) String locationName,
                                              @RequestParam(required = false) SortEnum sort,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size,
                                              HttpServletRequest request) {
        sendHits(request);
        return eventService.getShortEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, lat, lon, radius, locations, locationName, sort, from, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение подробной информации об опубликованном событии по его идентификатору")
    public EventFullDto getFullEvent(@PathVariable long id, HttpServletRequest request) {
        sendHits(request);
        return eventService.getFullEvent(id);
    }

    private void sendHits(HttpServletRequest request) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app(serviceName)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.hit(endpointHit);
    }
}
