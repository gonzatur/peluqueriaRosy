package peluqueriarosy.app.models.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import peluqueriarosy.app.models.entity.Servicio;

public interface IServicioDao extends CrudRepository<Servicio, Long>{

	public List<Servicio> findByCategoria(int categoria);
}
