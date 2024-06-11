package ru.practicum.ewm.main.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.dto.NewCategoryDto;
import ru.practicum.ewm.main.entity.CategoryEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    CategoryEntity toEntity(NewCategoryDto categoryDto);

    CategoryEntity toEntity(CategoryDto categoryDto);

    CategoryDto toCategoryDto(CategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CategoryEntity category, @MappingTarget CategoryEntity entity);
}
