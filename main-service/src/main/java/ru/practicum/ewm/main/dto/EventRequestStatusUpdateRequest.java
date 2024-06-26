package ru.practicum.ewm.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.dto.enums.StatusRequestEnum;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private StatusRequestEnum status;

}
