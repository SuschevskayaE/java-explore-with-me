package ru.practicum.ewm.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class Location {

    private Long id;

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

    @Builder.Default
    private Float radius = 0F;

    private String name;

    public Location() {
        radius = 0F;
    }

}
