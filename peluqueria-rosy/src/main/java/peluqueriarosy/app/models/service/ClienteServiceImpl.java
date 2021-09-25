package peluqueriarosy.app.models.service;

import java.util.ArrayList;
import java.util.List;

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
	@Transactional(readOnly = true)
	public Reserva findReservaById(Long id) {
		return reservaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteReserva(Long id) {
		reservaDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Cliente findUserByResetToken(String resetToken) {
		Cliente cli = clienteDao.findByResetToken(resetToken);
		if(cli!=null) {
			return cli;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAllClientes() {
		return (List<Cliente>) clienteDao.findAll();
	}

	@Override
	@Transactional
	public Cliente findById(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Cliente cliente) {
		clienteDao.delete(cliente);
		
	}

	@Override
	public List<Cliente> findByRoleUser() {
		ArrayList<Cliente> listadoAll = (ArrayList<Cliente>) clienteDao.findAll();
		List<Cliente> listadoCliUsers = new ArrayList<Cliente>();
		for (Cliente cliente : listadoAll) {
			String role = cliente.getRoles().get(0).getAuthority();
			if(role.equals("ROLE_USER")) {
				listadoCliUsers.add(cliente);
			}
		}
		return listadoCliUsers;
	}
	
}
