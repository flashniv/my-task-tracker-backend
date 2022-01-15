package ua.com.serverhelp.mytasktracking.data.repositories;

import org.json.JSONObject;
import ua.com.serverhelp.mytasktracking.data.entities.Task;

public interface TaskRepositoryCustom {
    JSONObject getCustomTask(Task task);
}
