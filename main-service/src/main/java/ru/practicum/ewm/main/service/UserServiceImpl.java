package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.common.FromSizeRequest;
import ru.practicum.ewm.main.dto.NewUserRequest;
import ru.practicum.ewm.main.dto.UserDto;
import ru.practicum.ewm.main.entity.UserEntity;
import ru.practicum.ewm.main.exeption.ValidationException;
import ru.practicum.ewm.main.mapper.UserMapper;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable)
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.getUserEntitiesByIdIn(ids, pageable)
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public UserDto addUser(NewUserRequest userRequest) {
        userRepository.findByEmail(userRequest.getEmail())
                .ifPresent(f -> {
                    throw new ValidationException(String.format("Почта %s уже существует", userRequest.getEmail()));
                });
        UserEntity entity = userRepository.save(userMapper.toEntity(userRequest));
        return userMapper.toUserDto(entity);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
