package course.spring.jyra;

import course.spring.jyra.model.Administrator;
import course.spring.jyra.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JyraApplication {

	public static void main(String[] args) {
		SpringApplication.run(JyraApplication.class, args);
//		Administrator administrator = Administrator.builder().firstName("Ivan").lastName("Todorov").email("asd@asd.com").password("Ivan1!").username("little_ivankis").build();
//		User admin2 = Administrator.builder().firstName("Ivan").lastName("Todorov").email("asd@asd.com").password("Ivan1!").username("little_ivankis").build();
//		Administrator administrator1 = new Administrator();
	}
}
