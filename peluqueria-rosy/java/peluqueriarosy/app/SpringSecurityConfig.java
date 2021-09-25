package peluqueriarosy.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import peluqueriarosy.app.auth.handler.LoginSuccessHandler;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**","/productos","/servicios/**", "/images/**", "/index","/eliminar/**", "/locale", "/form")
				.permitAll().antMatchers("/ver/**").hasAnyRole("USER").antMatchers("/uploads/**").hasAnyRole("USER")
				.antMatchers("/form/**").hasAnyRole("ADMIN").antMatchers("/eliminar/**").hasAnyRole("ADMIN")
				.antMatchers("/factura/**").hasAnyRole("ADMIN").anyRequest().authenticated().and().formLogin()
				.successHandler(successHandler).loginPage("/login").permitAll().and().logout().permitAll().and()
				.exceptionHandling().accessDeniedPage("/error_403");
	}
	


}
