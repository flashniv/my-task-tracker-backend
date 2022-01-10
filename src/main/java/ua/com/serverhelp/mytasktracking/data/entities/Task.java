package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;
    private String title;
    private String description;
}
