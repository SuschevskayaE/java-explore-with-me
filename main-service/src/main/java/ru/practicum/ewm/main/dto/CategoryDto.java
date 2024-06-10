package ru.practicum.ewm.main.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 50)
    private String name;

}
