package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
public class Period {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="task_id", nullable=false)
    private Task task;
    private Instant start=Instant.now();
    private Instant stop=null;
}
