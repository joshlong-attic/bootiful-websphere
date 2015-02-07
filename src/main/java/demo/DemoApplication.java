package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class DemoApplication {
/*
    private static final String JNDI_NAME = "jdbc/mysql";

    @Bean
    @Primary
    DataSource dataSource() {
        JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.setResourceRef(true);
        return jndiDataSourceLookup.getDataSource(JNDI_NAME);
    }*/

    @Bean
    CommandLineRunner commandLineRunner(AccountService accountService) {
        return args -> {
            accountService.findAll().forEach(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}


@Service
class AccountService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Account> findAll() {
        return jdbcTemplate.query("select id, username from account",
                (rs, i) -> new Account(rs.getLong("id"), rs.getString("username")));
    }
}

@RestController
class GreetingController {

    @Autowired
    private AccountService accountService;

    @RequestMapping("/accounts")
    List<Account> accounts() {
        return this.accountService.findAll();
    }

    @RequestMapping("/hi")
    String hi(@RequestParam Optional<String> name) {
        return "Hello" + (name.map(x -> ", " + x).orElse("")) + "!";
    }
}

/*
 */
class Account {
    private Long id;

    Account() {
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Account(Long id, String username) {

        this.id = id;
        this.username = username;
    }

    private String username;
}