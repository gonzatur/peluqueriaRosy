package peluqueriarosy.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import peluqueriarosy.app.models.entity.Categoria;

public interface ICategoriaDao extends CrudRepository<Categoria, Long>{

}
