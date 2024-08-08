package ru.evolenta.user.dto;

import lombok.Data;
import ru.evolenta.user.model.Action;

@Data
public class LogRequest {
    private String username;
    private Action action;
    private Integer taskId;
}
