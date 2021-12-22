package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Organization {
    @Id
    @GeneratedValue
    private Long id;
    private String organizationName;
    @OneToMany(mappedBy="organization")
    private List<Project> projects;
    @OneToMany(mappedBy="organization")
    private List<Team> teams;
}
