package peluqueriarosy.app.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import peluqueriarosy.app.models.dao.IReservaDao;
import peluqueriarosy.app.models.entity.Reserva;

@Component
public class ReservaServiceImpl implements IReservaService {

	@Autowired
	private IReservaDao reservaDao;
	
	@Override
	public void save(Reserva reserva) {
		reservaDao.save(reserva);
	}

}
