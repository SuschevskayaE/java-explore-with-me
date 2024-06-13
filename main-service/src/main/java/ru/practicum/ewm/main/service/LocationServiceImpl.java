package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.common.FromSizeRequest;
import ru.practicum.ewm.main.dto.Location;
import ru.practicum.ewm.main.dto.NewLocationRequest;
import ru.practicum.ewm.main.entity.LocationEntity;
import ru.practicum.ewm.main.exeption.ValidationException;
import ru.practicum.ewm.main.mapper.LocationMapper;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.LocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    private final EventRepository eventRepository;

    @Override
    public List<Location> getLocations(List<Long> ids, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);

        if (ids == null || ids.isEmpty()) {
            return locationRepository.findAll(pageable)
                    .stream()
                    .map(locationMapper::toLocation)
                    .collect(Collectors.toList());
        } else {
            return locationRepository.getLocationEntitiesByIdIn(ids, pageable)
                    .stream()
                    .map(locationMapper::toLocation)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Location addLocation(NewLocationRequest request) {
        return locationMapper.toLocation(saveLocation(locationMapper.toEntity(request)));
    }

    @Override
    public void deleteLocation(long locationId) {
        eventRepository.findByLocationId(locationId).stream().findFirst()
                .ifPresent(f -> {
                    throw new ValidationException(String.format("У локации %s есть события", locationId));
                });
        locationRepository.deleteById(locationId);
    }

    @Override
    public LocationEntity saveLocation(LocationEntity entity) {
        if (entity.getName() != null) {
            locationRepository.findByName(entity.getName())
                    .ifPresent(f -> {
                        throw new ValidationException(String.format("Локация с именем %s уже существует", f.getName()));
                    });
        }
        locationRepository.getLocationWithDistance(entity.getLat(), entity.getLon()).stream().findAny().ifPresent(f -> {
            throw new ValidationException(String.format("Локация с координатами %s %s уже существует", f.getLat(), f.getLon()));
        });
        return locationRepository.save(entity);
    }
}
