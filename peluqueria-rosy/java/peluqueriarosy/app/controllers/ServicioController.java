package peluqueriarosy.app.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import peluqueriarosy.app.models.entity.Categoria;
import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.Disponible;
import peluqueriarosy.app.models.entity.Ocupacion;
import peluqueriarosy.app.models.entity.Reserva;
import peluqueriarosy.app.models.entity.Servicio;
import peluqueriarosy.app.models.service.ICategoriaService;
import peluqueriarosy.app.models.service.IClienteService;
import peluqueriarosy.app.models.service.IDisponibleService;
import peluqueriarosy.app.models.service.IOcupacionService;
import peluqueriarosy.app.models.service.IReservaService;
import peluqueriarosy.app.models.service.IServicioService;

@Controller
@RequestMapping("/servicios")
@SessionAttributes("servicio")
public class ServicioController {

	@Autowired
	private ICategoriaService categoriaService;

	@Autowired
	private IServicioService servicioService;

	@Autowired
	private IOcupacionService ocupacionService;

	@Autowired
	private IDisponibleService disponibleService;

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IReservaService reservaService;

	@GetMapping("/")
	public String listar(Model model, Locale locale) {

		ArrayList<Categoria> categorias = (ArrayList<Categoria>) categoriaService.findAll();
		ArrayList<Servicio> servicios = (ArrayList<Servicio>) servicioService.findAll();

		model.addAttribute("categorias", categorias);
		model.addAttribute("servicios", servicios);
		return "servicios";
	}

	@GetMapping(value = "/{term}", produces = { "application/json" })
	public @ResponseBody List<String> cargarOcupacion(@PathVariable String term) {
		System.out.println("Cargando ocupación..." + term);

		// Solo de prueba-----------------------------
		List<Disponible> listaDisponibles = (List<Disponible>) disponibleService.findAll();
		List<String> listadoHorasDisponibles = new ArrayList<String>();
		for (Disponible disponible : listaDisponibles) {
			listadoHorasDisponibles.add(disponible.getHora());
		}
		Collections.sort(listadoHorasDisponibles);

		List<Ocupacion> listaOcupacion = ocupacionService.findByDia(term);
		List<String> listadoOcupacionStr = new ArrayList<String>();
		for (Ocupacion ocupacion : listaOcupacion) {
			listadoOcupacionStr.add(ocupacion.getHora());
			listadoHorasDisponibles.remove(ocupacion.getHora());
		}
		Collections.sort(listadoOcupacionStr);
		// Solo de prueba-----------------------------

		return listadoHorasDisponibles;
	}

	@GetMapping(value = "/reserva/{term}", produces = { "application/json" })
	public @ResponseBody String reservar(@PathVariable String term) {

		User e = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Cliente cli = clienteService.findByEmail(e.getUsername());
		System.out.println("Entrando en reserva..." + term);
		String[] parte = term.split("\\*");
		String servicioStr = parte[0];
		Long idServicio = Long.parseLong(servicioStr);
		String dia = parte[1];
		String hora = parte[2];

		Reserva reserva = new Reserva(dia, hora, cli.getId());
		reserva.setServicio(servicioService.findOne(idServicio));
//		cli.addReserva(reserva);

		// Guardar reserva
		reservaService.save(reserva);
		// Guardar ocupacion
		List<Disponible> listaDisponibles = (List<Disponible>) disponibleService.findAll();
		int duracionServicioMin = servicioService.findOne(idServicio).getMinutos();
		int duracionServicioHor = servicioService.findOne(idServicio).getHoras();
		int duracionServicio = (duracionServicioHor * 60) + duracionServicioMin;

		float tramos = duracionServicio / 15;
		tramos = tramos + 1;
		System.out.println("Duracion servicio: " + duracionServicio);
		System.out.println("Tramos:" + tramos);

		Disponible disp = disponibleService.findByHora(hora);

		int index = listaDisponibles.indexOf(disp);
		double tramosGuardar = Math.round(tramos) + index;
		System.out.println("Tramos?:" + tramosGuardar);

		for (int i = index; i < tramosGuardar; i++) {
			System.out.println("Tramo " + i);
			if (i < listaDisponibles.size()) {
				Disponible dispon = listaDisponibles.get(i);
				if (dispon != null) {
					String id= dia+"/"+dispon.getHora();
					Ocupacion ocu = new Ocupacion(id, dia, dispon.getHora());
					ocupacionService.save(ocu);
				}
			}
		}
		return "redirect:/reserva";
	}

}
