package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.mytasktracking.data.entities.Team;

public interface TeamRepository extends JpaRepository<Team,Long> {
}
