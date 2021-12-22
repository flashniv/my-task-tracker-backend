package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    private String login;
    private String passwordHash;
    private String firstName;
    private String lastName;
    @ManyToMany(mappedBy = "accounts")
    private List<Team> teams;

}
