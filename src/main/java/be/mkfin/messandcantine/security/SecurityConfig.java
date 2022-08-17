package be.mkfin.messandcantine.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    // Encodeur pour les passwords lors du login
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
        super.configure(auth);
    }

    @Override
    public void configure(WebSecurity web) {
        //super.configure(web);
        //URL pour lesquels il n'y a pas de s�curit�
        web.ignoring()
                .antMatchers("/images/**",
                        "/img/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()

                .antMatchers("/admin/**/*").hasRole("ADMIN")
                .antMatchers("/command/**/*").hasRole("EMPLOYEE")
             //   .antMatchers("/auth/*").hasAnyRole("ADMIN","USER")
             //   .antMatchers("/api/admin/**/*").fullyAuthenticated()
              //  .antMatchers("/admin/**/*").fullyAuthenticated()

                .and().formLogin().loginPage("/").failureUrl("/login-error")
                .successForwardUrl("/").permitAll()
                .and().logout().logoutSuccessUrl("/").permitAll();
    }

}
