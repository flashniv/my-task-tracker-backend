package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.serverhelp.mytasktracking.data.entities.Period;
import ua.com.serverhelp.mytasktracking.data.entities.Task;

import java.util.List;

public interface PeriodRepository extends JpaRepository<Period,Long> {
    //@Query("select 1")
    //int getSecondsByTask(Task task);

    List<Period> findByTask(Task task, Sort unsorted);
}
