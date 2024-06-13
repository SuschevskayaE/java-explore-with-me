package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.entity.LocationEntity;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    Optional<LocationEntity> findByName(String name);

    List<LocationEntity> getLocationEntitiesByIdIn(List<Long> ids, Pageable pageable);

    @Query("select l from LocationEntity as l " +
            "where distance(l.lat, l.lon, :lat2, :lon2 ) < l.radius ")
    List<LocationEntity> getLocationWithDistance(@Param("lat2") float lat2,
                                                 @Param("lon2") float lon2);


}
