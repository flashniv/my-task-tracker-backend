package ua.com.serverhelp.mytasktracking.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;

@Service
public class AccountUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account account = accountRepository.findByLogin(s);
        if (account == null) {
            throw new UsernameNotFoundException("SimpleUserDetailsService::loadUserByUsername User " + s + " not found");
        }
        return new AccountUserDetail(account);
    }
}