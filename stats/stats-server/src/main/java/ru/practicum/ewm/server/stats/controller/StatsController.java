package ru.practicum.ewm.server.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.server.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void hit(@RequestBody EndpointHit request) {
        service.hit(request);
    }

    @GetMapping("/stats")
    public Set<ViewStats> stats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                @RequestParam(required = false) Set<String> uris,
                                @RequestParam(defaultValue = "false") boolean unique) {
        return service.stats(start, end, uris, unique);
    }
}
