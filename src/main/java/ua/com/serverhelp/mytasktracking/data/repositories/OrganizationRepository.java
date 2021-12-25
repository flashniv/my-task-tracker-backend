package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.entities.Organization;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {
    List<Organization> findByOwner(Account account);
}
