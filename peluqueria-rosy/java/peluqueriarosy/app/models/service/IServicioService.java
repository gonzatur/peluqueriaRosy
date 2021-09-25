package peluqueriarosy.app.models.service;

import java.util.List;

import peluqueriarosy.app.models.entity.Servicio;

public interface IServicioService {

	public List<Servicio> findAll();
	
	public List<Servicio> findByCategoria(int categoria);
	
	public Servicio findOne(Long id);

}
