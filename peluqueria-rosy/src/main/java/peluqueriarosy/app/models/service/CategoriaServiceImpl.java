package peluqueriarosy.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.ICategoriaDao;
import peluqueriarosy.app.models.entity.Categoria;

@Component
public class CategoriaServiceImpl implements ICategoriaService{

	@Autowired
	private ICategoriaDao categoriaDao;

	@Override
	@Transactional(readOnly = true)
	public List<Categoria> findAll() {
		return (List<Categoria>) categoriaDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Categoria findById(Long id) {
		return categoriaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Categoria categoria) {
		categoriaDao.delete(categoria);
		
	}

	@Override
	@Transactional
	public void save(Categoria categoria) {
		categoriaDao.save(categoria);
	}
	
}
