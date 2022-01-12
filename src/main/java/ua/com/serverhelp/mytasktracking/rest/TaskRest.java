package ua.com.serverhelp.mytasktracking.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.mytasktracking.conf.AccountUserDetail;
import ua.com.serverhelp.mytasktracking.data.entities.*;
import ua.com.serverhelp.mytasktracking.data.repositories.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/task")
@CrossOrigin
public class TaskRest {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private HistoryItemRepository historyItemRepository;

    @GetMapping(value = "/",produces = "application/json")
    public ResponseEntity<String> getTasks(
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            JSONArray result=new JSONArray();
            List<Task> taskList=taskRepository.findByOwner(uid);
            for (Task task:taskList){
                JSONObject jsonTask=new JSONObject(task);
                List<HistoryItem> historyItems=historyItemRepository.findByTask(task, Sort.by(Sort.Direction.DESC,"timestamp"));

                Instant start=null;
                int seconds=0;
                for (int i = historyItems.size()-1; i >=0 ; i--) {
                    HistoryItem historyItem=historyItems.get(i);
                    if(start==null && historyItem.getStatus()!=TaskStatus.IN_PROGRESS) continue;
                    if(start==null && historyItem.getStatus()==TaskStatus.IN_PROGRESS) start=historyItem.getTimestamp();
                    if(start!=null && historyItem.getStatus()!=TaskStatus.IN_PROGRESS) {
                        Duration duration=Duration.between(start,historyItem.getTimestamp());
                        seconds+=duration.getSeconds();
                        start=null;
                    }
                }
                if (start!=null){
                    seconds+=Duration.between(start, Instant.now()).getSeconds();
                }
                jsonTask.put("history", new JSONArray(historyItems));
                jsonTask.put("seconds", seconds);
                result.put(jsonTask);
            }

            return ResponseEntity.ok(result.toString());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<String> addTasks(
            @RequestBody Task task,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Project> projectOptional=projectRepository.findById(task.getProject().getId());
            if(projectOptional.isPresent()){
                if(projectOptional.get().getOrganization().getOwner().getId()!=uid){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                Task task1=taskRepository.save(task);
                HistoryItem historyItem=new HistoryItem();
                historyItem.setTask(task1);
                historyItem.setStatus(TaskStatus.NEW);
                historyItemRepository.save(historyItem);
                return ResponseEntity.ok().body(new JSONObject(task1).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @GetMapping(value = "/{taskId}",produces = "application/json")
    public ResponseEntity<String> getTask(
            @PathVariable long taskId,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> task=taskRepository.findById(taskId);
            if (task.isPresent()){
                if(task.get().getProject().getOrganization().getOwner().getId()!=uid){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                return ResponseEntity.ok().body(new JSONObject(task.get()).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }
    @DeleteMapping(value = "/{taskId}",produces = "application/json")
    public ResponseEntity<String> deleteTask(
            @PathVariable long taskId,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> task=taskRepository.findById(taskId);
            if (task.isPresent()){
                if(task.get().getProject().getOrganization().getOwner().getId()!=uid){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                List<HistoryItem> history=historyItemRepository.findByTask(task.get(), Sort.unsorted());
                historyItemRepository.deleteAll(history);
                taskRepository.delete(task.get());
                return ResponseEntity.ok().body("Success");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }
    @PutMapping(value = "/{taskId}",produces = "application/json")
    public ResponseEntity<String> updateTask(
            @PathVariable long taskId,
            @RequestBody Task updTask,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> task=taskRepository.findById(taskId);
            if (task.isPresent()){
                if(task.get().getProject().getOrganization().getOwner().getId()!=uid){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                updTask.setId(task.get().getId());
                updTask.setProject(task.get().getProject());
                return ResponseEntity.ok().body(new JSONObject(taskRepository.save(updTask)).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }
    @PutMapping(value = "/{taskId}/updateStatus",produces = "application/json")
    public ResponseEntity<String> updateTaskStatus(
            @PathVariable long taskId,
            @RequestParam String newStatus,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> taskOptional=taskRepository.findById(taskId);
            if (taskOptional.isPresent()){
                if(taskOptional.get().getProject().getOrganization().getOwner().getId()!=uid){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                Task task=taskOptional.get();
                try {
                    HistoryItem historyItem = new HistoryItem();
                    historyItem.setTask(task);
                    historyItem.setStatus(TaskStatus.valueOf(newStatus));
                    historyItemRepository.save(historyItem);
                }catch (IllegalArgumentException e){
                    return ResponseEntity.badRequest().body("Status invalid");
                }
                return ResponseEntity.ok().body(new JSONObject(task).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }
}
