package peluqueriarosy.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import peluqueriarosy.app.models.entity.Reserva;

public interface IReservaDao extends CrudRepository<Reserva, Long>{

	
}
