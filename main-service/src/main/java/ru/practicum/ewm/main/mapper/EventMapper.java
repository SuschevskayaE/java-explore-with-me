package ru.practicum.ewm.main.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.main.dto.*;
import ru.practicum.ewm.main.entity.EventEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryMapper.class, UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "category", target = "category.id")
    EventEntity toEntity(NewEventDto newEventDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "category", target = "category.id")
    EventEntity toEntity(UpdateEventUserRequest updateEventUserRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "category", target = "category.id")
    EventEntity toEntity(UpdateEventAdminRequest updateEventAdminRequest);

    @Mapping(source = "initiator.id", target = "initiator.id")
    @Mapping(source = "initiator.name", target = "initiator.name")
    EventFullDto toEventFullDto(EventEntity entity);

    EventShortDto toEventShortDto(EventEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    void updateEntity(EventEntity event, @MappingTarget EventEntity entity);
}
