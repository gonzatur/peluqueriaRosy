package peluqueriarosy.app.models.service;

import java.util.List;

import peluqueriarosy.app.models.entity.Categoria;

public interface ICategoriaService {

	public List<Categoria> findAll();
}
