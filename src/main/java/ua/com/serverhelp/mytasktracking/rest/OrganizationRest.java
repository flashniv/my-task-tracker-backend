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
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.OrganizationRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/organization")
@CrossOrigin
public class OrganizationRest {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping(value = "/",produces = "application/json")
    public ResponseEntity<String> getAllOrganizations(
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            List<Organization> organizations = organizationRepository.findByOwner(account.get());
            JSONArray result=new JSONArray(organizations);
            return ResponseEntity.ok(result.toString());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/")
    public ResponseEntity<Organization> addOrganization(
            @RequestBody Organization organization,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            organization.setOwner(account.get());
            return ResponseEntity.ok(organizationRepository.save(organization));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @DeleteMapping("/{organizationId}")
    public ResponseEntity<String> delete(
            @PathVariable long organizationId,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> organization=organizationRepository.findById(organizationId);
            if(organization.isPresent()){
                if(organization.get().getOwner().getId()==uid){
                    organizationRepository.delete(organization.get());
                    return ResponseEntity.ok("Success");
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organization not found");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
    }
    @PutMapping("/{organizationId}")
    public ResponseEntity<String> update(
            @PathVariable long organizationId,
            @RequestBody Organization inputOrganization,
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> organization=organizationRepository.findById(organizationId);
            if(organization.isPresent()){
                if(organization.get().getOwner().getId()==uid){
                    inputOrganization.setId(organizationId);
                    organizationRepository.save(inputOrganization);
                    return ResponseEntity.ok("Success");
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organization not found");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
    }

}
