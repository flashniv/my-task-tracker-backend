package ua.com.serverhelp.mytasktracking.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountUserDetailsService accountUserDetailsService;

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.formLogin().loginPage("/login").permitAll();
        http.logout().permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        http.authorizeRequests().antMatchers("/error").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/account/").permitAll();

        http.authorizeRequests().anyRequest().authenticated();
    }
}
