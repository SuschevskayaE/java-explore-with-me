package ru.practicum.ewm.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Пользователь (краткая информация)
 */
@Getter
@AllArgsConstructor
public class UserShortDto {

    private Long id;

    private String name;
}
