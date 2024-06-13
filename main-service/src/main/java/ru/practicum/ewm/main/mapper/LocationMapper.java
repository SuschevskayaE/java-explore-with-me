package ru.practicum.ewm.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.main.dto.NewLocationRequest;
import ru.practicum.ewm.main.entity.Location;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    Location toEntity(NewLocationRequest locationRequest);

    @Mapping(target = "id", ignore = true)
    Location toEntity(ru.practicum.ewm.main.dto.Location location);

    ru.practicum.ewm.main.dto.Location toLocation(Location entity);
}
