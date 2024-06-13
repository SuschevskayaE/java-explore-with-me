package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.dto.NewLocationRequest;
import ru.practicum.ewm.main.entity.Location;

import java.util.List;

public interface LocationService {

    List<ru.practicum.ewm.main.dto.Location> getLocations(List<Long> ids, int from, int size);

    ru.practicum.ewm.main.dto.Location addLocation(NewLocationRequest request);

    void deleteLocation(long locationId);

    Location saveLocation(Location entity);
}
