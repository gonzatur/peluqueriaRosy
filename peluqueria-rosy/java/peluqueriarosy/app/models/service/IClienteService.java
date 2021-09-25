package peluqueriarosy.app.models.service;

import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.Reserva;

public interface IClienteService {

	public void save(Cliente cliente);
	
	public Cliente findByEmail(String email);
	
	public Reserva findReservaById(Long id);
	
	public void deleteReserva(Long id);
	
	public void updatePassword(String password, Long userId);
}
