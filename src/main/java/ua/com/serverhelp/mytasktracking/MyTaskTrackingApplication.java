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
import ua.com.serverhelp.mytasktracking.data.entities.Account;
import ua.com.serverhelp.mytasktracking.data.entities.Organization;
import ua.com.serverhelp.mytasktracking.data.entities.Project;
import ua.com.serverhelp.mytasktracking.data.entities.Task;
import ua.com.serverhelp.mytasktracking.data.repositories.AccountRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.OrganizationRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.ProjectRepository;
import ua.com.serverhelp.mytasktracking.data.repositories.TaskRepository;

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
				TaskRepository taskRepository=ctx.getBean(TaskRepository.class);

				Account account=new Account();
				account.setFirstName("acc1");
				account.setLogin("acc1");
				account.setPasswordHash("$2a$04$4WXX95/CCl59lHaOAI9oSe0OYbcAb7vV.rc.spjRKsZBTC4hMCl52");
				accountRepository.save(account);

				Organization organization=new Organization();
				organization.setOrganizationName("my org");
				organization.setOwner(account);
				organizationRepository.save(organization);

				Project project=new Project();
				project.setProjectName("my proj");
				project.setOrganization(organization);
				projectRepository.save(project);

				Task task=new Task();
				task.setProject(project);
				task.setTitle("task1");
				taskRepository.save(task);

				Task task1=new Task();
				task1.setProject(project);
				task1.setTitle("task2");
				taskRepository.save(task1);
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MyTaskTrackingApplication.class, args);
	}

}
