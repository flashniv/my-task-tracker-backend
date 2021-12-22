package ua.com.serverhelp.mytasktracking.data.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Organization {
    @Id
    @GeneratedValue
    private Long id;
    private String organizationName;
}
