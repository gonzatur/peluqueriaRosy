package peluqueriarosy.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.IClienteDao;
import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.Role;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {

	@Autowired
	private IClienteDao clienteDao;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Cliente cliente = clienteDao.findByEmail(email);

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (cliente != null) {
			for (Role role : cliente.getRoles()) {
				authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
			}
		

		return new User(cliente.getEmail(), cliente.getPassword(), cliente.getEnabled(), true, true, true, authorities);
		}
		
		return new User(" ", " ", false, true, true, true, authorities);
		
	}

}
