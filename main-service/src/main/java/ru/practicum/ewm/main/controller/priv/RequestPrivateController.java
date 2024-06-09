package ru.practicum.ewm.main.controller.priv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.service.RequestParticipationService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Private: Запросы на участие", description = "Закрытый API для работы с запросами текущего пользователя на участие в событиях")
@RequiredArgsConstructor
public class RequestPrivateController {

    private final RequestParticipationService requestService;

    @GetMapping
    @Operation(summary = "Получение информации о заявках текущего пользователя на участие в чужих событиях")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable long userId) {
        return requestService.getParticipationRequests(userId);
    }

    @PostMapping
    @Operation(summary = "Добавление запроса от текущего пользователя на участие в событии")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto getParticipationRequest(@PathVariable long userId,
                                                           @RequestParam long eventId) {
        return requestService.addParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @Operation(summary = "Отмена своего запроса на участие в событии")
    public ParticipationRequestDto updateParticipationRequest(@PathVariable long userId,
                                                              @PathVariable long requestId) {
        return requestService.updateParticipationRequest(userId, requestId);
    }

}
