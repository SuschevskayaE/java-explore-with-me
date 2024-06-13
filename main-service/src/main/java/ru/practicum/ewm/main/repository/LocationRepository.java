package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByName(String name);

    List<Location> getLocationEntitiesByIdIn(List<Long> ids, Pageable pageable);

    @Query("select l from Location as l " +
            "where distance(l.lat, l.lon, :lat2, :lon2 ) < l.radius ")
    List<Location> getLocationWithDistance(@Param("lat2") float lat2,
                                           @Param("lon2") float lon2);


}
