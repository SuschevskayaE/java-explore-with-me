package ru.practicum.ewm.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * Широта и долгота места проведения события
 */
@Getter
@AllArgsConstructor
public class Location {

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

}
