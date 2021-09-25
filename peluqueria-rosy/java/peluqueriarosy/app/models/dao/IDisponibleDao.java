package peluqueriarosy.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import peluqueriarosy.app.models.entity.Disponible;

public interface IDisponibleDao extends CrudRepository<Disponible, Long>{

	public Disponible findByHora(String hora);
}
