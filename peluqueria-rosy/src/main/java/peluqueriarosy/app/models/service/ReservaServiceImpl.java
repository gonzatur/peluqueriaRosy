package peluqueriarosy.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.IReservaDao;
import peluqueriarosy.app.models.entity.Reserva;

@Component
public class ReservaServiceImpl implements IReservaService {

	@Autowired
	private IReservaDao reservaDao;
	
	@Override
	@Transactional
	public void save(Reserva reserva) {
		reservaDao.save(reserva);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Reserva> findByDia(String dia) {
		return reservaDao.findByDia(dia);
	}

	@Override
	@Transactional
	public void delete(Reserva reserva) {
		reservaDao.delete(reserva);
	}

}
