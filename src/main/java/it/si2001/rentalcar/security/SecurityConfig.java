package it.si2001.rentalcar.security;

import it.si2001.rentalcar.encoder.PlainPasswordEncoder;
import it.si2001.rentalcar.filter.CustomAuthenticationFilter;
import it.si2001.rentalcar.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

// overrides the methods to manage the users and the security on the application
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // because there is @RequiredArgsConstructor (that will build a constructor on the fly for this variable)
    // and the variables is declared final, the injection it has been done without the help of @Autowired

    // The UserServiceImpl implements UserDetailsService
    private final UserDetailsService userDetailsService;

    // Defined in RentalCarApplication class; use this for password encoded to decoded them
    private final BCryptPasswordEncoder passwordEncoder;

    // Defined in RentalCarApplication class; use this for normal plain passwords
    private final PlainPasswordEncoder plainPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // it takes a UserDetailService that is a Bean that it should be overridden to tell to Spring how to look for the users
        auth.userDetailsService(userDetailsService).passwordEncoder(plainPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //setup settings for sessions policy by JWT tokens system and not by cookies
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);

        http.cors();

        // REMEMBER: THE ORDER MATTERS! Do it from the smaller to the bigger
        http.authorizeRequests().antMatchers("/login/**").permitAll();

        http.authorizeRequests().antMatchers(GET, "/users").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(GET, "/users/customers/**").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(GET, "/users/email/*").hasAnyAuthority("SUPERUSER", "CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/users/*").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(POST, "/users/**").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(PUT, "/users/**").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(DELETE, "/users/**").hasAnyAuthority("SUPERUSER");

        http.authorizeRequests().antMatchers(GET, "/bookings").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(GET, "/bookings/*").hasAnyAuthority("SUPERUSER", "CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/bookings/add").hasAnyAuthority("SUPERUSER", "CUSTOMER");
        http.authorizeRequests().antMatchers(PUT, "/bookings/update/*").hasAnyAuthority("SUPERUSER", "CUSTOMER");
        http.authorizeRequests().antMatchers(DELETE, "/bookings/delete/*").hasAnyAuthority("SUPERUSER", "CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/bookings/customer/*").hasAnyAuthority( "CUSTOMER");

        http.authorizeRequests().antMatchers(GET, "/vehicles","/vehicles/*").hasAnyAuthority( "SUPERUSER", "CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/vehicles/add").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(DELETE, "/vehicles/delete/*").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(PUT, "/vehicles/update/*").hasAnyAuthority("SUPERUSER");
        http.authorizeRequests().antMatchers(GET, "/vehicles/add").hasAnyAuthority( "SUPERUSER");

        // this filter come before all others filter
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // to change in the future
        http.authorizeRequests().anyRequest().authenticated();

        // it specifies what to do with the data sent by the user when he is trying to log in
        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));

    }

    // take the bean AuthenticationManager
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManager();
    }


}
