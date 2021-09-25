package peluqueriarosy.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import peluqueriarosy.app.models.entity.Role;

public interface IRoleDao extends CrudRepository<Role, Long>{

}
