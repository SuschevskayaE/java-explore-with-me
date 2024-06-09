package ru.practicum.ewm.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Пользователь
 */
@Getter
@AllArgsConstructor
public class UserDto {

    private String email;

    private Long id;

    private String name;
}
