package ru.practicum.ewm.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LocationSearch {

    private List<Long> ids;

    private float lat;

    private float lon;

    private float radius;

    private String name;
}
