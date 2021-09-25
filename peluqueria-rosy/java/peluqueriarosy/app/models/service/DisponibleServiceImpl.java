package peluqueriarosy.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.IDisponibleDao;
import peluqueriarosy.app.models.entity.Disponible;

@Component
public class DisponibleServiceImpl implements IDisponibleService{

	@Autowired
	private IDisponibleDao disponibleDao;
	

	@Override
	@Transactional(readOnly = true)
	public List<Disponible> findAll(){
		return (List<Disponible>) disponibleDao.findAll();
	}


	@Override
	@Transactional(readOnly = true)
	public Disponible findByHora(String hora) {
		return disponibleDao.findByHora(hora);
	}
}
