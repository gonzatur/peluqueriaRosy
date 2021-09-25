package peluqueriarosy.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.IOcupacionDao;
import peluqueriarosy.app.models.entity.Ocupacion;

@Component
public class OcupacionServiceImpl implements IOcupacionService {

	@Autowired
	private IOcupacionDao ocupacionDao;

	@Override
	@Transactional(readOnly = true)
	public List<Ocupacion> findByDia(String dia) {
		return ocupacionDao.findByDia(dia);
	}

	@Override
	@Transactional
	public void save(Ocupacion ocupacion) {
		ocupacionDao.save(ocupacion);
	}
	
	@Override
	@Transactional
	public void delete(Ocupacion ocupacion) {
		ocupacionDao.delete(ocupacion);
	}

	@Override
	@Transactional(readOnly = true)
	public Ocupacion findById(String id) {
		return ocupacionDao.findById(id).orElse(null);
	}



}
