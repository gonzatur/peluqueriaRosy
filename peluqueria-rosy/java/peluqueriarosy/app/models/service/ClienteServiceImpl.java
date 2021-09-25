package peluqueriarosy.app.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import peluqueriarosy.app.models.dao.IClienteDao;
import peluqueriarosy.app.models.dao.IReservaDao;
import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.Reserva;

@Component
public class ClienteServiceImpl implements IClienteService{

	@Autowired
	private IClienteDao clienteDao;
	
	@Autowired
	private IReservaDao reservaDao;
	
	@Override
	@Transactional
	public void save(Cliente cliente) {
		clienteDao.save(cliente);
	}

	@Override
	@Transactional(readOnly = true)
	public Cliente findByEmail(String email) {

		Cliente cli = clienteDao.findByEmail(email);
		if(cli!=null) {
			return cli;
		}
		return null;
	}

	@Override
	public Reserva findReservaById(Long id) {
		return reservaDao.findById(id).orElse(null);
	}

	@Override
	public void deleteReserva(Long id) {
		reservaDao.deleteById(id);
	}
	
	public void updatePassword(String password, Long userId) {
		clienteDao.updatePassword(password, userId);
	}
}
