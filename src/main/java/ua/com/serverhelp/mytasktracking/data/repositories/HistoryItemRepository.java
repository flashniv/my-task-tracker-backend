package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.mytasktracking.data.entities.HistoryItem;
import ua.com.serverhelp.mytasktracking.data.entities.Task;

import java.util.List;

public interface HistoryItemRepository extends JpaRepository<HistoryItem,Long> {
    List<HistoryItem> findByTask(Task task, Sort sort);
}
