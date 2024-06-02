package ru.practicum.ewm.server.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.server.stats.entity.EndpointHitEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EndpointHitMapper {
    EndpointHitEntity toEntity(EndpointHit hit);
}
