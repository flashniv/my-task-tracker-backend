package ua.com.serverhelp.mytasktracking.rest;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.mytasktracking.conf.AccountUserDetail;
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.entities.Organization;
import ua.com.serverhelp.mytasktracking.data.entities.Project;
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.OrganizationRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.ProjectRepository;

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

    @GetMapping(value = "/",produces = "application/json")
    public ResponseEntity<String> getProjects(
            @RequestParam long organizationId,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> optionalOrganization=organizationRepository.findById(organizationId);
            if (optionalOrganization.isPresent()){
                Organization organization=optionalOrganization.get();
                if(!Objects.equals(organization.getOwner().getId(), account.get().getId())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                List<Project> projectList=projectRepository.findByOrganization(organization);
                return ResponseEntity.ok().body(new JSONArray(projectList).toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PostMapping(value = "/")
    public ResponseEntity<Project> addProjects(
            @RequestParam long organizationId,
            @RequestBody Project project,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> optionalOrganization=organizationRepository.findById(organizationId);
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
            @RequestParam long organizationId,
            @PathVariable long projectId,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> optionalOrganization=organizationRepository.findById(organizationId);
            if (optionalOrganization.isPresent()){
                Organization organization=optionalOrganization.get();
                if(!Objects.equals(organization.getOwner().getId(), account.get().getId())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                Optional<Project> project=projectRepository.findById(projectId);

                if (project.isPresent()) {
                    if(!Objects.equals(project.get().getOrganization().getId(), organization.getId())){
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                    }
                    return ResponseEntity.ok().body(project.get());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<String> deleteProjectById(
            @RequestParam long organizationId,
            @PathVariable long projectId,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> optionalOrganization=organizationRepository.findById(organizationId);
            if (optionalOrganization.isPresent()){
                Organization organization=optionalOrganization.get();
                if(!Objects.equals(organization.getOwner().getId(), account.get().getId())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                Optional<Project> project=projectRepository.findById(projectId);
                if (project.isPresent()) {
                    if(!Objects.equals(project.get().getOrganization().getId(), organization.getId())){
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                    }
                    projectRepository.delete(project.get());
                    return ResponseEntity.ok().body("Success");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PutMapping(value = "/{projectId}",produces = "application/json")
    public ResponseEntity<Project> updateProjectById(
            @RequestParam long organizationId,
            @PathVariable long projectId,
            @RequestBody Project project,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> optionalOrganization=organizationRepository.findById(organizationId);
            if (optionalOrganization.isPresent()){
                Organization organization=optionalOrganization.get();
                if(!Objects.equals(organization.getOwner().getId(), account.get().getId())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                project.setOrganization(organization);
                project.setId(projectId);
                return ResponseEntity.ok(projectRepository.save(project));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
