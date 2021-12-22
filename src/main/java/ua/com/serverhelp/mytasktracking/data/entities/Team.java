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
    @ManyToMany
    @JoinTable(
            name = "account_team",
            joinColumns = { @JoinColumn(name = "account_id") },
            inverseJoinColumns = { @JoinColumn(name = "team_id") }
    )
    private List<Account> accounts = new ArrayList<>();
}
