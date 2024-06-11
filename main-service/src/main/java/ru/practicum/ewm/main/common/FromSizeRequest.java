package ru.practicum.ewm.main.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ToString
@EqualsAndHashCode
public class FromSizeRequest extends PageRequest {

    @Getter
    private final int from;

    protected FromSizeRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    public static FromSizeRequest of(int from, int size, Sort sort) {
        return new FromSizeRequest(from, size, sort);
    }
}
