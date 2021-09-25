package peluqueriarosy.app.models.service;

import java.util.List;

import peluqueriarosy.app.models.entity.Reserva;

public interface IReservaService {

	public void save(Reserva reserva);
	
	public List<Reserva> findByDia(String dia);

	public void delete(Reserva reserva);
	
}
