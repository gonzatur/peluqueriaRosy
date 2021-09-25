package peluqueriarosy.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.IServicioDao;
import peluqueriarosy.app.models.entity.Servicio;

@Component
public class ServicioServiceImpl implements IServicioService{

	@Autowired
	private IServicioDao servicioDao;

	@Override
	@Transactional(readOnly = true)
	public List<Servicio> findByCategoria(int categoria) {
		return servicioDao.findByCategoria(categoria);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Servicio> findAll() {
		return (List<Servicio>) servicioDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Servicio findOne(Long id) {
		return servicioDao.findById(id).orElse(null);// OrElse busca Cliente por ID, si no hay retorna null
	}

	@Override
	@Transactional
	public void save(Servicio servicio) {
		servicioDao.save(servicio);
	}

	@Override
	@Transactional
	public void delete(Servicio servicio) {
		servicioDao.delete(servicio);
	}

	
}
