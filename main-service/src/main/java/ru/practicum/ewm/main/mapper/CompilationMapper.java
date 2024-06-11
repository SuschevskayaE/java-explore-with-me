package ru.practicum.ewm.main.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.main.dto.CompilationDto;
import ru.practicum.ewm.main.dto.NewCompilationDto;
import ru.practicum.ewm.main.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.entity.CompilationEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    CompilationEntity toEntity(NewCompilationDto newCompilationDto);

    @Mapping(target = "id", ignore = true)
    CompilationEntity toEntity(UpdateCompilationRequest compilationRequest);

    CompilationDto toCompilationDto(CompilationEntity entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CompilationEntity compilationRequest, @MappingTarget CompilationEntity entity);
}
