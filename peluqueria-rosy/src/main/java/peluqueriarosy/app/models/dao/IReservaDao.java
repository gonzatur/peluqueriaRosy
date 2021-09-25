package peluqueriarosy.app.models.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import peluqueriarosy.app.models.entity.Reserva;

public interface IReservaDao extends CrudRepository<Reserva, Long>{

	public List<Reserva> findByDia(String dia);
}
