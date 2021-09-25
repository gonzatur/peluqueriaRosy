package peluqueriarosy.app.models.service;

import java.util.List;

import peluqueriarosy.app.models.entity.Ocupacion;

public interface IOcupacionService {

	public void save(Ocupacion ocupacion);
	
	public List<Ocupacion> findByDia(String dia);
	
	public void delete(Ocupacion ocupacion);
	
	public Ocupacion findById(String id);
}
