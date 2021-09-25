package peluqueriarosy.app.models.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import peluqueriarosy.app.models.entity.Ocupacion;


public interface IOcupacionDao extends CrudRepository<Ocupacion, String>{

	public List<Ocupacion> findByDia(String dia);
}
