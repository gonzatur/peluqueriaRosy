package peluqueriarosy.app.models.service;

import java.util.List;

import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.Reserva;

public interface IClienteService {

	public void save(Cliente cliente);
	
	public Cliente findById(Long id);
	
	public Cliente findByEmail(String email);
	
	public Reserva findReservaById(Long id);
	
	public void deleteReserva(Long id);;
	
	public void delete(Cliente cliente);
	
    public Cliente findUserByResetToken(String resetToken);
    
    public List<Cliente> findAllClientes();
    
    public List<Cliente> findByRoleUser();
}
