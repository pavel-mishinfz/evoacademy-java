package ru.evolenta.task.dto;

import lombok.Data;
import ru.evolenta.task.model.Action;

@Data
public class LogRequest {
    private String username;
    private Action action;
    private Integer taskId;
}
