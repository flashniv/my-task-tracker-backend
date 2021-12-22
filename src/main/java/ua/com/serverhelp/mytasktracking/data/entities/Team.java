package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;

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

}
