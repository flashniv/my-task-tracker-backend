package ua.com.serverhelp.mytasktracking.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.mytasktracking.data.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByLogin(String s);
}
