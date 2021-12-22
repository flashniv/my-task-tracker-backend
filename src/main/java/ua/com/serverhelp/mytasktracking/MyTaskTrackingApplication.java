package ua.com.serverhelp.mytasktracking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.entities.Organization;
import ua.com.serverhelp.mytasktracking.data.entities.Project;
import ua.com.serverhelp.mytasktracking.data.entities.Team;
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.OrganizationRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.ProjectRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.TeamRepository;

import java.util.List;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MyTaskTrackingApplication {
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(4);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				AccountRepository accountRepository=ctx.getBean(AccountRepository.class);
				OrganizationRepository organizationRepository=ctx.getBean(OrganizationRepository.class);
				ProjectRepository projectRepository=ctx.getBean(ProjectRepository.class);
				TeamRepository teamRepository=ctx.getBean(TeamRepository.class);

				Account account=new Account();
				account.setFirstName("acc1");
				account.setLogin("acc1");
				account.setPasswordHash("$2a$04$4WXX95/CCl59lHaOAI9oSe0OYbcAb7vV.rc.spjRKsZBTC4hMCl52");
				accountRepository.save(account);

				Organization organization=new Organization();
				organization.setOrganizationName("my org");
				organization.setOwner(account);
				organizationRepository.save(organization);

				Team team=new Team();
				team.setTeamName("my team");
				team.setOrganization(organization);
				team.setAccounts(List.of(account));
				teamRepository.save(team);

				Project project=new Project();
				project.setProjectName("my proj");
				project.setOrganization(organization);
				project.setTeams(List.of(team));
				projectRepository.save(project);
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MyTaskTrackingApplication.class, args);
	}

}
