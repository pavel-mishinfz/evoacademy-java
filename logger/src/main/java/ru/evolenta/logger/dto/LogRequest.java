package ru.evolenta.logger.dto;

import lombok.Data;
import ru.evolenta.logger.model.Action;

@Data
public class LogRequest {
    private String username;
    private Action action;
    private Integer taskId;
}
