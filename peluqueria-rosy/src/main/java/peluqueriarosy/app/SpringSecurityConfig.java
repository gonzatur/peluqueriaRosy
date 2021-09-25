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

		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**","/productos","/servicios/**","/reset/**","/assets/**", "/images/**", "/index","/forgot","/eliminar/**", "/locale", "/form", "/contactar", "/uploads/**")
				.permitAll()
				.antMatchers("/usuarios/**").hasAnyRole("ADMIN")
				.antMatchers("/formProducto/**").hasAnyRole("ADMIN")
				.antMatchers("/formServicio/**").hasAnyRole("ADMIN")
				.antMatchers("/formCategoria/**").hasAnyRole("ADMIN")
				.antMatchers("/peluqueria/**").hasAnyRole("ADMIN")
				.antMatchers("/eliminarPro/**").hasAnyRole("ADMIN")
				.antMatchers("/eliminarS/**").hasAnyRole("ADMIN")
				.antMatchers("/eliminarCat/**").hasAnyRole("ADMIN")
				.antMatchers("/eliminarC/**").hasAnyRole("ADMIN")
				.antMatchers("/eliminarDia/**").hasAnyRole("ADMIN")
				.anyRequest().authenticated().and().formLogin()
				.successHandler(successHandler).loginPage("/login").permitAll().and().logout().permitAll().and()
				.exceptionHandling().accessDeniedPage("/error_403");
	}
}