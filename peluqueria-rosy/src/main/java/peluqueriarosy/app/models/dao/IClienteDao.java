package peluqueriarosy.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import peluqueriarosy.app.models.entity.Cliente;

public interface IClienteDao extends CrudRepository<Cliente, Long> {

	public Cliente findByEmail(String email);

	public Cliente findByResetToken(String resetToken);

}
