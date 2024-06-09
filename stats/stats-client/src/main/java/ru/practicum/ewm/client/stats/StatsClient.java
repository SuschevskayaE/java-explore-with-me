package ru.practicum.ewm.client.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@Service
public class StatsClient {

    protected final RestTemplate rest;

    private static final String API_HIT = "/hit";
    private static final String API_STATS = "/stats";


    public StatsClient(@Value("${spring.datasource.statservice.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void hit(EndpointHit request) {
        rest.exchange(API_HIT, HttpMethod.POST, new HttpEntity<>(request), Void.class);
    }

    public ResponseEntity<Set<ViewStats>> getStats(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> parameters = Map.of(
                "start", formatter.format(start),
                "end", formatter.format(end),
                "uris", String.join(",", uris),
                "unique", unique
        );
        return rest.exchange(API_STATS + "?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                }, parameters);
    }

}
