package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
public class HistoryItem {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "task_id",nullable = false)
    private Task task;
    private Instant timestamp=Instant.now();
    @Enumerated(EnumType.ORDINAL)
    private TaskStatus status;
}
