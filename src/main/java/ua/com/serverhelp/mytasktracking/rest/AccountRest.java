package ua.com.serverhelp.mytasktracking.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/account")
public class AccountRest {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(
            @PathVariable("id") long id
    ){
        Optional<Account> accountOptional=accountRepository.findById(id);
        return accountOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("/")
    public ResponseEntity<Account> addAccount(
            @RequestBody Account account
    ){
        account.setPasswordHash(passwordEncoder.encode(account.getPasswordHash()));
        return ResponseEntity.ok(accountRepository.save(account));
    }
}
