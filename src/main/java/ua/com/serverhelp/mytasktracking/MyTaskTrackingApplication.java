package ua.com.serverhelp.mytasktracking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
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
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				AccountRepository accountRepository=ctx.getBean(AccountRepository.class);
				OrganizationRepository organizationRepository=ctx.getBean(OrganizationRepository.class);
				ProjectRepository projectRepository=ctx.getBean(ProjectRepository.class);
				TeamRepository teamRepository=ctx.getBean(TeamRepository.class);

				Organization organization=new Organization();
				organization.setOrganizationName("my org");
				organizationRepository.save(organization);

				Team team=new Team();
				team.setTeamName("my team");
				team.setOrganization(organization);

				Project project=new Project();
				project.setProjectName("my proj");
				project.setOrganization(organization);
				project.setTeams(List.of(team));
				projectRepository.save(project);

				Account account=new Account();
				account.setFirstName("acc1");
				account.setLogin("acc1");

				team.setAccounts(List.of(account));

				accountRepository.save(account);
				teamRepository.save(team);
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MyTaskTrackingApplication.class, args);
	}

}
