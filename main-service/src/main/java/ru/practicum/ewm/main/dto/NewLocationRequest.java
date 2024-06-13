package ru.practicum.ewm.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewLocationRequest {

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

    @NotNull
    @Positive
    private Float radius;

    @NotNull
    @NotBlank
    private String name;
}
