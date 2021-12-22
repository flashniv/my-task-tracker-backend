package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.mytasktracking.data.entities.Organization;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {
}
