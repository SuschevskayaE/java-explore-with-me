package ru.practicum.ewm.main.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.main.dto.CompilationDto;
import ru.practicum.ewm.main.service.CompilationService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Public: Подборки событий", description = "Публичный API для работы с подборками событий")
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {

    private final CompilationService compilationService;

    private final StatsClient statsClient;

    @Value("${service-name}")
    private String serviceName;

    @GetMapping
    @Operation(summary = "Получение подборок событий")
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size,
                                                HttpServletRequest request) {
        sendHits(request);
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @Operation(summary = "Получение подборок событий")
    public CompilationDto getCompilation(@PathVariable long compId, HttpServletRequest request) {
        sendHits(request);
        return compilationService.getCompilation(compId);
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
