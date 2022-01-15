package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.serverhelp.mytasktracking.data.entities.Project;
import ua.com.serverhelp.mytasktracking.data.entities.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long>, TaskRepositoryCustom {
    List<Task> findByProject(Project project);
    @Query("SELECT t FROM Task t JOIN FETCH t.project p JOIN FETCH p.organization o JOIN FETCH o.owner ow WHERE ow.id = :uid")
    List<Task> findByOwner(long uid);
}
