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

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MyTaskTrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyTaskTrackingApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
				/*AccountRepository accountRepository=ctx.getBean(AccountRepository.class);
				OrganizationRepository organizationRepository=ctx.getBean(OrganizationRepository.class);
				ProjectRepository projectRepository=ctx.getBean(ProjectRepository.class);
				TaskRepository taskRepository=ctx.getBean(TaskRepository.class);
				TaskStatusRepository taskStatusRepository=ctx.getBean(TaskStatusRepository.class);
				PeriodRepository periodRepository=ctx.getBean(PeriodRepository.class);

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

				TaskStatus taskStatus=new TaskStatus();
				taskStatus.setTask(task);
				taskStatusRepository.save(taskStatus);

				Period period=new Period();
				period.setTask(task);
				periodRepository.save(period);

				Task task1=new Task();
				task1.setProject(project);
				task1.setTitle("task2");
				taskRepository.save(task1);

				TaskStatus taskStatus1=new TaskStatus();
				taskStatus1.setTask(task1);
				taskStatusRepository.save(taskStatus1);

				Period period1=new Period();
				period1.setTask(task1);
				periodRepository.save(period1);*/

            }
        };
    }

}
