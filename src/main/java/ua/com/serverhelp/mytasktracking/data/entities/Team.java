package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Team {
    @Id
    @GeneratedValue
    private Long id;
    private String teamName;
    @ManyToOne
    @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;
    @ManyToMany(mappedBy = "teams")
    private List<Account> accounts = new ArrayList<>();
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "team_project",
            joinColumns = { @JoinColumn(name = "team_id") },
            inverseJoinColumns = { @JoinColumn(name = "project_id") }
    )
    private List<Project> projects;

}
