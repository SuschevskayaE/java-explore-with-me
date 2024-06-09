package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.dto.enums.StateEnum;
import ru.practicum.ewm.main.dto.enums.StatusRequestEnum;
import ru.practicum.ewm.main.entity.EventEntity;
import ru.practicum.ewm.main.entity.ParticipationRequestEntity;
import ru.practicum.ewm.main.entity.UserEntity;
import ru.practicum.ewm.main.exeption.DataNotFoundException;
import ru.practicum.ewm.main.exeption.ValidationException;
import ru.practicum.ewm.main.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.ParticipationRequestRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestParticipationServiceImpl implements RequestParticipationService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getParticipationRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        return participationRequestRepository.findAllByRequesterId(userId)
                .stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(long userId, long eventId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Событие с id %s не найдено", eventId)));

        participationRequestRepository.findByRequesterIdAndEventId(userId, eventId).ifPresent(x -> {
            throw new ValidationException("Такой запрос уже существует");
        });
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException(String.format("Пользователь с id %s инициатор события", userId));
        }
        if (!event.getState().equals(StateEnum.PUBLISHED)) {
            throw new ValidationException(String.format("Событие с id %s не опубликованно", eventId));
        }
        Long count = participationRequestRepository.getCountRequestsForEvent(eventId, StatusRequestEnum.CONFIRMED);

        if (event.getParticipantLimit() <= count && event.getParticipantLimit() != 0) {
            throw new ValidationException(String.format("У события с id %s закончился лимит", eventId));
        }

        ParticipationRequestEntity entity = new ParticipationRequestEntity();
        entity.setEvent(event);
        entity.setRequester(user);
        entity.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || (event.getParticipantLimit() == 0)) {
            entity.setStatus(StatusRequestEnum.CONFIRMED);
        } else {
            entity.setStatus(StatusRequestEnum.PENDING);
        }

        entity = participationRequestRepository.save(entity);
        return participationRequestMapper.toParticipationRequestDto(entity);
    }

    @Override
    public ParticipationRequestDto updateParticipationRequest(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));

        ParticipationRequestEntity entity = participationRequestRepository.findAllByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Запрос на участие с id %s не найден", requestId)));
        if (!entity.getRequester().getId().equals(userId)) {
            throw new ValidationException("Это не ваш запрос");
        }
        entity.setStatus(StatusRequestEnum.CANCELED);
        return participationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(entity));
    }
}
