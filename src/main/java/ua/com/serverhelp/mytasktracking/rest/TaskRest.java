package ua.com.serverhelp.mytasktracking.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.mytasktracking.conf.AccountUserDetail;
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.entities.Project;
import ua.com.serverhelp.mytasktracking.data.entities.Task;
import ua.com.serverhelp.mytasktracking.data.entities.TaskStatus;
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.OrganizationRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.ProjectRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.TaskRepository;

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

    @GetMapping(value = "/",produces = "application/json")
    public ResponseEntity<String> getTasks(
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            List<Task> taskList=taskRepository.findByOwner(uid);
            return ResponseEntity.ok(new JSONArray(taskList).toString());
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
                return ResponseEntity.ok().body(new JSONObject(taskRepository.save(task)).toString());
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
                    task.setStatus(TaskStatus.valueOf(newStatus));
                }catch (IllegalArgumentException e){
                    return ResponseEntity.badRequest().body("Status invalid");
                }
                return ResponseEntity.ok().body(new JSONObject(taskRepository.save(task)).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }
    @PutMapping(value = "/{taskId}/updateMinutes",produces = "application/json")
    public ResponseEntity<String> updateTaskMinutes(
            @PathVariable long taskId,
            @RequestParam String newMinutes,
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
                    task.setMinutes(getNewMinutesValue(task.getMinutes(),newMinutes));
                }catch (IllegalArgumentException e){
                    return ResponseEntity.badRequest().body("Status invalid");
                }
                return ResponseEntity.ok().body(new JSONObject(taskRepository.save(task)).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    private int getNewMinutesValue(int minutes, String newMinutes) throws IllegalArgumentException {
        if (newMinutes.matches("\\+[0-9]+")){
            return minutes+Integer.parseInt(newMinutes);
        } else if (newMinutes.matches("-[0-9]+")){
            minutes=minutes+Integer.parseInt(newMinutes);
            if (minutes>=0) {
                return minutes;
            }
            throw new IllegalArgumentException("Negative value returned");
        } else if (newMinutes.matches("[0-9]+")){
            return Integer.parseInt(newMinutes);
        }
        throw new IllegalArgumentException("Input newMinutes invalid");
    }
}