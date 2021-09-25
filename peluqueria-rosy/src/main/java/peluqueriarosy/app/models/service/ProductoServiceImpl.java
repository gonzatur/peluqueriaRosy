package peluqueriarosy.app.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.IProductoDao;
import peluqueriarosy.app.models.entity.Producto;

@Component
public class ProductoServiceImpl implements IProductoService{

	@Autowired
	private IProductoDao productoDao;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Producto> findAll(Pageable pageable) {
		return productoDao.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Producto findById(Long id) {
		return productoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Producto producto) {
		productoDao.delete(producto);	
	}

	@Override
	@Transactional
	public void save(Producto producto) {
		productoDao.save(producto);
	}

}
