package peluqueriarosy.app.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import peluqueriarosy.app.models.dao.IRoleDao;
import peluqueriarosy.app.models.entity.Role;

@Component
public class RoleServiceImpl implements IRoleService{

	@Autowired
	private IRoleDao roleDao;
	
	@Override
	public void save(Role role) {
		roleDao.save(role);
		
	}

	
}
