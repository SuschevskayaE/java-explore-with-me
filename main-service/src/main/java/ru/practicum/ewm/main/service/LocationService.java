package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.dto.Location;
import ru.practicum.ewm.main.dto.NewLocationRequest;
import ru.practicum.ewm.main.entity.LocationEntity;

import java.util.List;

public interface LocationService {

    List<Location> getLocations(List<Long> ids, int from, int size);

    Location addLocation(NewLocationRequest request);

    void deleteLocation(long locationId);

    LocationEntity saveLocation(LocationEntity entity);
}
