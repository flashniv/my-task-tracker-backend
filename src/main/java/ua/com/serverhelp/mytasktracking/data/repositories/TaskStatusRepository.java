package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.mytasktracking.data.entities.Task;
import ua.com.serverhelp.mytasktracking.data.entities.TaskStatus;

import java.util.List;

public interface TaskStatusRepository extends JpaRepository<TaskStatus,Long> {
    List<TaskStatus> findByTask(Task task, Sort unsorted);
}
