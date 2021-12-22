package ua.com.serverhelp.mytasktracking.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.com.serverhelp.mytasktracking.data.entities.Organization;
import ua.com.serverhelp.mytasktracking.data.repositories.OrganizationRepository;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api/organization")
public class OrganizationRest {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private HttpSession httpSession;

    @GetMapping("")
    public ResponseEntity<Organization> getAllOrganizations(
            @RequestParam String accountId
    ){
        Optional<Organization> organization=organizationRepository.findById(Long.parseLong(accountId));
        return ResponseEntity.ok(organization.orElse(null));
    }
}
