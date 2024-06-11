package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestParticipationService {
    List<ParticipationRequestDto> getParticipationRequests(long userId);

    ParticipationRequestDto addParticipationRequest(long userId, long eventId);

    ParticipationRequestDto updateParticipationRequest(long userId, long requestId);

}
