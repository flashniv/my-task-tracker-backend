package ua.com.serverhelp.mytasktracking.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/account")
public class AccountRest {
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(
            @PathVariable("id") long id
    ){
        Optional<Account> account=accountRepository.findById(id);
        return ResponseEntity.ok(account.orElse(null));
    }
}
