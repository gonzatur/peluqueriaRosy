package peluqueriarosy.app.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.IRoleDao;
import peluqueriarosy.app.models.entity.Role;

@Component
public class RoleServiceImpl implements IRoleService{

	@Autowired
	private IRoleDao roleDao;
	
	@Override
	@Transactional
	public void save(Role role) {
		roleDao.save(role);
		
	}

	
}
