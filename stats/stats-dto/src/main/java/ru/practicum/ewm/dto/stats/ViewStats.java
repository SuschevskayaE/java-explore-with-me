package ru.practicum.ewm.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}
