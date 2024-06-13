package ru.practicum.ewm.main.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.Location;
import ru.practicum.ewm.main.dto.NewLocationRequest;
import ru.practicum.ewm.main.service.LocationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/locations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Admin: Локации", description = "API для работы с локациями")
@RequiredArgsConstructor
@Validated
public class LocationAdminController {

    private final LocationService locationService;

    @GetMapping
    @Operation(summary = "Получение информации о локациях")
    public List<Location> getLocations(@RequestParam(required = false) List<Long> ids,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        return locationService.getLocations(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Добавление новой локации")
    public Location addLocation(@Valid @RequestBody NewLocationRequest request) {
        return locationService.addLocation(request);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление локации")
    public void deleteLocation(@PathVariable long locationId) {
        locationService.deleteLocation(locationId);
    }
}
