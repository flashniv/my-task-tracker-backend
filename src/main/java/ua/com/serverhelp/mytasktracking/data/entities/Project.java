package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Project {
    @Id
    @GeneratedValue
    private Long id;
    private String projectName;
    @ManyToOne
    @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;
    @ManyToMany(mappedBy = "projects")
    private List<Team> teams = new ArrayList<>();

}
