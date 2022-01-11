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
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectRest {
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
    public ResponseEntity<String> getProjects(
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            List<Project> projectList=projectRepository.findByOwner(account.get().getId());
            return ResponseEntity.ok().body(new JSONArray(projectList).toString());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PostMapping(value = "/")
    public ResponseEntity<Project> addProjects(
            @RequestBody Project project,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> optionalOrganization=organizationRepository.findById(project.getOrganization().getId());
            if (optionalOrganization.isPresent()){
                Organization organization=optionalOrganization.get();
                if(!Objects.equals(organization.getOwner().getId(), account.get().getId())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                project.setOrganization(organization);
                return ResponseEntity.ok().body(projectRepository.save(project));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping(value = "/{projectId}",produces = "application/json")
    public ResponseEntity<Project> getProjectById(
            @PathVariable long projectId,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Project> project = projectRepository.findById(projectId);
            if (project.isPresent()) {
                if (!Objects.equals(project.get().getOrganization().getOwner().getId(), account.get().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                return ResponseEntity.ok().body(project.get());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<String> deleteProjectById(
            @PathVariable long projectId,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Project> project=projectRepository.findById(projectId);
            if (project.isPresent()){
                if(!Objects.equals(project.get().getOrganization().getOwner().getId(), account.get().getId())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                projectRepository.delete(project.get());
                return ResponseEntity.ok().body("Success");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PutMapping(value = "/{projectId}",produces = "application/json")
    public ResponseEntity<Project> updateProjectById(
            @PathVariable long projectId,
            @RequestBody Project project,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Project> projectOptional=projectRepository.findById(projectId);
            if(projectOptional.isPresent()){
                if(!Objects.equals(projectOptional.get().getOrganization().getOwner().getId(), account.get().getId())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                project.setOrganization(projectOptional.get().getOrganization());
                project.setId(projectId);
                return ResponseEntity.ok(projectRepository.save(project));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping(value = "/{projectId}/tasks",produces = "application/json")
    public ResponseEntity<String> getProjectTasksById(
            @PathVariable long projectId,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Project> project = projectRepository.findById(projectId);
            if (project.isPresent()) {
                if (!Objects.equals(project.get().getOrganization().getOwner().getId(), account.get().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                JSONArray result=new JSONArray();
                List<Task> taskList=taskRepository.findByProject(project.get());
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

                return ResponseEntity.ok().body(result.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Access denied");
    }

}
