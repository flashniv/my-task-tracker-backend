package ua.com.serverhelp.mytasktracking.data.repositories;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import ua.com.serverhelp.mytasktracking.data.entities.Period;
import ua.com.serverhelp.mytasktracking.data.entities.Task;
import ua.com.serverhelp.mytasktracking.data.entities.TaskStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class TaskRepositoryImpl implements TaskRepositoryCustom {
    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Override
    public JSONObject getCustomTask(Task task){
        JSONObject jsonTask=new JSONObject(task);
        Optional<TaskStatus> optionalTaskStatus=taskStatusRepository.findTopByTaskOrderByTimestampDesc(task);
        optionalTaskStatus.ifPresent(taskStatus -> jsonTask.put("status", taskStatus.getTaskStatus().toString()));
        //process periods
        List<Period> periods=periodRepository.findByTask(task, Sort.by(Sort.Direction.DESC,"start"));
        long seconds=0;
        for (Period period:periods){
            if(period.getStop()==null){
                //period.setStop(Instant.now());
                jsonTask.put("isPlay", period.getStart());
                continue;
            }
            Duration duration=Duration.between(period.getStart(), period.getStop());
            seconds+= duration.getSeconds();
        }
        jsonTask.put("seconds", seconds);
        return jsonTask;
    }

}
