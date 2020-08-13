package at.htlstp.aslan.carrent.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomSuccessHandler successHandler;

    /**
     * Creates two in memory users with each having a specific role.
     * username: admin and password: admin has the role of ADMIN.
     * username: emp and password: emp has the role of EMPLOYEE
     *
     * @param auth the AuthenticationManagerBuilder used to define the in memory users.
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .and()
                .withUser("emp")
                .password("{noop}emp")
                .roles("EMPLOYEE");
    }

    /**
     * Enables basic HTTP authorization, disables CSRF.
     * Any route with the wildcard /admin/** can only be accessed by an authorized user with the role ADMIN.
     * Any route with the wildcard /employee/** can only be accessed by an authorized user with the role EMPLOYEE.
     * All other routes are public.
     *
     * @param http HttpSecurity object used for configurtaion
     * @throws Exception
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.
                httpBasic().
                and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/employee/**").hasRole("EMPLOYEE")
                .antMatchers("/**", "/h2-console/**").permitAll()
                .and()
                .formLogin().successHandler(this.successHandler)
                .and()
                .exceptionHandling().accessDeniedPage("/login")
                .and()
                .logout().logoutSuccessUrl("/");

        http.headers().frameOptions().disable(); // display h2 console correctly
    }
}

@Component
class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        if (response.isCommitted()) {
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    /**
     * Determines where the user should be redirected after a login considering the role.
     * ADMIN is redirected to /admin/running-rentals, which is a REST endpoint.
     * EMPLOYEE is redirected to the homepage /.
     * Everyone else is redirected to the login page.
     *
     * @param authentication authentication object used for getting the authorities of a request
     * @return the redirection URL as String
     */
    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<>();

        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        String url = "/login";

        if (roles.contains("ROLE_ADMIN")) {
            url = "/admin/running-rentals";
        }
        if (roles.contains("ROLE_EMPLOYEE")) {
            url = "/";
        }

        return url;
    }
}
