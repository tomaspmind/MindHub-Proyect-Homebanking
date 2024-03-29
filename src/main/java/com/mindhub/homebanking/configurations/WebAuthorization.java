package com.mindhub.homebanking.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
public class WebAuthorization {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()

                .antMatchers("/rest/**").hasAuthority("ADMIN")
                .antMatchers("/web/assets/**","/web/images/**","/web/JS/**","/api/login","/web/index.html").permitAll()
                .antMatchers("/api/logout").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers(HttpMethod.POST, "/api/clients").permitAll()
                .antMatchers(HttpMethod.POST, "/api/clients/current/cards").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST,"/api/clients/current/accounts").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST,"/api/transactions").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST,"/api/loans").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.PATCH,"/api/clients/current/cards").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.PATCH,"/api/clients/current/accounts").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST,"/api/admin/loans").hasAuthority("ADMIN")
                .antMatchers("/web/admin.html").hasAuthority("ADMIN")
                .antMatchers("/api/loans").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/api/clients/current/cards").hasAuthority("CLIENT")
                .antMatchers("/api/clients/current/accounts").hasAuthority("CLIENT")
                .antMatchers("/api/clients/current").hasAuthority("CLIENT")
                .antMatchers("/api/clients").hasAuthority("ADMIN")
                .antMatchers("/api/clients/{id}").hasAuthority("ADMIN")
                .antMatchers("/api/accounts/{id}").hasAuthority("CLIENT")
                .antMatchers("/api/accounts").hasAuthority("ADMIN")
                .antMatchers("/manager/**").hasAuthority("ADMIN")
                .antMatchers("/web/**").hasAuthority("CLIENT")
                .anyRequest().denyAll();

        http.formLogin()

                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout").deleteCookies("JSESSIONID");

        // turn off checking for CSRF tokens, investigar que tipo de token es

        http.csrf().disable();

        //disabling frameOptions so h2-console can be accessed

        http.headers().frameOptions().disable();

        // if user is not authenticated, just send an authentication failure response

        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication

        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response

        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response

        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        return http.build();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

    }

}
