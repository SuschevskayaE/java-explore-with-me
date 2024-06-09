package ru.practicum.ewm.main.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortEnum {

    EVENT_DATE("EVENT_DATE"),
    VIEWS("VIEWS");

    private String data;
}
