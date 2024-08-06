package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.serverhelp.mytasktracking.data.entities.Organization;
import ua.com.serverhelp.mytasktracking.data.entities.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p JOIN FETCH p.organization o JOIN FETCH o.owner ow WHERE ow.id = :id")
    List<Project> findByOwner(Long id);

    List<Project> findByOrganization(Organization organization);
}
