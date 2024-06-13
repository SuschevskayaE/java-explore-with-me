package ru.practicum.ewm.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.main.dto.Location;
import ru.practicum.ewm.main.dto.NewLocationRequest;
import ru.practicum.ewm.main.entity.LocationEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    LocationEntity toEntity(NewLocationRequest locationRequest);

    @Mapping(target = "id", ignore = true)
    LocationEntity toEntity(Location location);

    Location toLocation(LocationEntity entity);
}
