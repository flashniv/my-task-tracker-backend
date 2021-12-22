package ua.com.serverhelp.mytasktracking.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.serverhelp.mytasktracking.conf.AccountUserDetail;
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.entities.Organization;
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.OrganizationRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/organization")
public class OrganizationRest {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public ResponseEntity<Organization> getAllOrganizations(
            Authentication authentication
    ){
        long uid=((AccountUserDetail) authentication.getPrincipal()).getId();
        Optional<Account> account=accountRepository.findById(uid);
        if(account.isPresent()) {
            Optional<Organization> organization = organizationRepository.findByOwner(account.get());
            if (organization.isPresent()) {
                return ResponseEntity.ok(organization.get());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
