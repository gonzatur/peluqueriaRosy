package peluqueriarosy.app.models.service;

import java.util.List;

import peluqueriarosy.app.models.entity.Disponible;

public interface IDisponibleService {

	public Disponible findByHora(String hora);
	
	public List<Disponible>findAll();
}
