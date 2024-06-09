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
import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.service.CategoryService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Public: Категории", description = "Публичный API для работы с категориями")
@Validated
public class CategoryPublicController {

    private final CategoryService categoryService;

    private final StatsClient statsClient;

    @Value("${service-name}")
    private String serviceName;

    @GetMapping
    @Operation(summary = "Получение категорий")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        sendHits(request);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @Operation(summary = "Получение информации о категории по её идентификатору")
    public CategoryDto getCategory(@PathVariable long catId, HttpServletRequest request) {
        sendHits(request);
        return categoryService.getCategory(catId);
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
