package course.spring.jyra.config;

import course.spring.jyra.model.User;
import course.spring.jyra.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
//        http.csrf().disable()
//                .authorizeRequests()
//                    .antMatchers("/","/register")
//                    .permitAll()
//                    .anyRequest()
//                    .authenticated()
//                    .and()
//                .formLogin()
//                    .loginPage("/login")
//                    .permitAll()
//                    .defaultSuccessUrl("/recipes")
//                    .and()
//                .logout()
//                    .logoutUrl("/logout")
//                    .permitAll();
        // @formatter:on

        // @formatter:off
        http.csrf().disable()
                .authorizeRequests()
                    //.antMatchers("/").permitAll()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/register").permitAll()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/index")
                    .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/index")
                    .and()
                .exceptionHandling();
        // .antMatchers("/dashboard/**").hasAuthority("ADMIN").anyRequest()

        // @formatter:on
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        // return userService::findByUsername;

        //for debugging purposes
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userService.findByUsername(username);
                return user;
            }
        };
    }
}
