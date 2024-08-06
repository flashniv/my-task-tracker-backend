package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Organization {
    @Id
    @GeneratedValue
    private Long id;
    private String organizationName;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;
}
