package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.serverhelp.mytasktracking.data.entities.Period;
import ua.com.serverhelp.mytasktracking.data.entities.Task;

import java.time.Instant;
import java.util.List;

public interface PeriodRepository extends JpaRepository<Period, Long> {
    @Query(value = "select sum(extract(epoch FROM case when stop is null then :now else stop end - start)) from period where task_id =:task_id", nativeQuery = true)
    int getSecondsByTask(Instant now, Long task_id);

    List<Period> findByTask(Task task, Sort unsorted);
}
