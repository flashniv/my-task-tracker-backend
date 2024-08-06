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

import java.time.Instant;
import java.util.ArrayList;
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
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private PeriodRepository periodRepository;

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<String> getTasks(
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            JSONArray res = new JSONArray();
            List<Task> taskList = taskRepository.findByOwner(uid);
            for (Task task : taskList) {
                res.put(taskRepository.getCustomTask(task));
            }

            return ResponseEntity.ok(res.toString());
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
            Optional<Project> projectOptional = projectRepository.findById(task.getProject().getId());
            if (projectOptional.isPresent()) {
                if (projectOptional.get().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                Task task1 = taskRepository.save(task);
                TaskStatus taskStatus = new TaskStatus();
                taskStatus.setTask(task1);
                taskStatusRepository.save(taskStatus);
                return ResponseEntity.ok().body(new JSONObject(task1).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PostMapping(value = "/updateStatus", produces = "application/json")
    public ResponseEntity<String> updateAllTaskStatus(
            @RequestParam String newStatus,
            @RequestBody Task[] tasks,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            List<Long> taskIds = new ArrayList<>();
            List<TaskStatus> newStatuses = new ArrayList<>();
            //get IDs in array
            for (Task task : tasks) {
                taskIds.add(task.getId());
            }
            List<Task> tasksFromDB = taskRepository.findAllById(taskIds);
            //generate new statuses
            for (Task task : tasksFromDB) {
                if (task.getProject().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                //Check if period was started
                List<Period> periods = periodRepository.findByTask(task, Sort.by(Sort.Direction.DESC, "start"));
                if (!periods.isEmpty() && periods.get(0).getStop() == null) {
                    Period period = periods.get(0);
                    period.setStop(Instant.now());
                    periodRepository.save(period);
                }
                //set status
                try {
                    TaskStatus taskStatus = new TaskStatus();
                    taskStatus.setTask(task);
                    taskStatus.setTaskStatus(TaskStatusEnum.valueOf(newStatus));
                    newStatuses.add(taskStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Status invalid");
                }
            }
            //save new statuses
            if (tasks.length > 0 && tasks.length == newStatuses.size()) {
                taskStatusRepository.saveAll(newStatuses);
                return ResponseEntity.ok().body("Success");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @GetMapping(value = "/{taskId}", produces = "application/json")
    public ResponseEntity<String> getTask(
            @PathVariable long taskId,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> task = taskRepository.findById(taskId);
            if (task.isPresent()) {
                if (task.get().getProject().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                return ResponseEntity.ok().body(new JSONObject(task.get()).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @DeleteMapping(value = "/{taskId}", produces = "application/json")
    public ResponseEntity<String> deleteTask(
            @PathVariable long taskId,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> task = taskRepository.findById(taskId);
            if (task.isPresent()) {
                if (task.get().getProject().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                List<TaskStatus> taskStatuses = taskStatusRepository.findByTask(task.get(), Sort.unsorted());
                taskStatusRepository.deleteAll(taskStatuses);
                List<Period> periods = periodRepository.findByTask(task.get(), Sort.unsorted());
                periodRepository.deleteAll(periods);

                taskRepository.delete(task.get());
                return ResponseEntity.ok().body("Success");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PutMapping(value = "/{taskId}", produces = "application/json")
    public ResponseEntity<String> updateTask(
            @PathVariable long taskId,
            @RequestBody Task updTask,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> task = taskRepository.findById(taskId);
            if (task.isPresent()) {
                if (task.get().getProject().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                updTask.setId(task.get().getId());
                updTask.setProject(task.get().getProject());
                return ResponseEntity.ok().body(new JSONObject(taskRepository.save(updTask)).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PutMapping(value = "/{taskId}/updateStatus", produces = "application/json")
    public ResponseEntity<String> updateTaskStatus(
            @PathVariable long taskId,
            @RequestParam String newStatus,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isPresent()) {
                if (taskOptional.get().getProject().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                Task task = taskOptional.get();
                try {
                    TaskStatus taskStatus = new TaskStatus();
                    taskStatus.setTask(task);
                    taskStatus.setTaskStatus(TaskStatusEnum.valueOf(newStatus));
                    taskStatusRepository.save(taskStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Status invalid");
                }
                return ResponseEntity.ok().body(new JSONObject(task).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PutMapping(value = "/{taskId}/startPeriod", produces = "application/json")
    public ResponseEntity<String> startPeriod(
            @PathVariable long taskId,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isPresent()) {
                if (taskOptional.get().getProject().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                Task task = taskOptional.get();

                List<Period> periods = periodRepository.findByTask(task, Sort.by(Sort.Direction.DESC, "start"));
                if (!periods.isEmpty()) {
                    if (periods.get(0).getStop() == null) {
                        return ResponseEntity.badRequest().body("Task already started");
                    }
                }
                try {
                    TaskStatus taskStatus = new TaskStatus();
                    taskStatus.setTask(task);
                    taskStatus.setTaskStatus(TaskStatusEnum.valueOf("IN_PROGRESS"));
                    taskStatusRepository.save(taskStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Status invalid");
                }

                Period period = new Period();
                period.setTask(task);
                periodRepository.save(period);

                return ResponseEntity.ok().body(new JSONObject(task).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PutMapping(value = "/{taskId}/stopPeriod", produces = "application/json")
    public ResponseEntity<String> stopPeriod(
            @PathVariable long taskId,
            @RequestParam(required = false, defaultValue = "ON_PAUSE") String newStatus,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isPresent()) {
                if (taskOptional.get().getProject().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                Task task = taskOptional.get();

                List<Period> periods = periodRepository.findByTask(task, Sort.by(Sort.Direction.DESC, "start"));
                if (!periods.isEmpty()) {
                    if (periods.get(0).getStop() == null) {
                        try {
                            TaskStatus taskStatus = new TaskStatus();
                            taskStatus.setTask(task);
                            taskStatus.setTaskStatus(TaskStatusEnum.valueOf(newStatus));
                            taskStatusRepository.save(taskStatus);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.badRequest().body("Status invalid");
                        }

                        Period period = periods.get(0);
                        period.setStop(Instant.now());
                        periodRepository.save(period);
                        return ResponseEntity.ok().body(new JSONObject(task).toString());
                    }
                    return ResponseEntity.badRequest().body("Task was stopped");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PutMapping(value = "/{taskId}/resetPeriod", produces = "application/json")
    public ResponseEntity<String> resetPeriod(
            @PathVariable long taskId,
            @RequestParam int newSeconds,
            Authentication authentication
    ) {
        long uid = ((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account = accountRepository.findById(uid);
        if (account.isPresent()) {
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isPresent()) {
                if (taskOptional.get().getProject().getOrganization().getOwner().getId() != uid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                Task task = taskOptional.get();

                List<Period> periods = periodRepository.findByTask(task, Sort.unsorted());
                periodRepository.deleteAll(periods);
                try {
                    TaskStatus taskStatus = new TaskStatus();
                    taskStatus.setTask(task);
                    taskStatus.setTaskStatus(TaskStatusEnum.valueOf("ON_PAUSE"));
                    taskStatusRepository.save(taskStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Status invalid");
                }

                Period period = periods.get(0);
                period.setStart(Instant.now().minusSeconds(newSeconds));
                period.setStop(Instant.now());
                periodRepository.save(period);
                return ResponseEntity.ok().body(new JSONObject(task).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }
}
