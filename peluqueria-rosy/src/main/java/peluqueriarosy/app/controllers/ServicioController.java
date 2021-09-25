package peluqueriarosy.app.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import peluqueriarosy.app.models.entity.Categoria;
import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.Disponible;
import peluqueriarosy.app.models.entity.Ocupacion;
import peluqueriarosy.app.models.entity.Reserva;
import peluqueriarosy.app.models.entity.Servicio;
import peluqueriarosy.app.models.service.EmailServiceImpl;
import peluqueriarosy.app.models.service.ICategoriaService;
import peluqueriarosy.app.models.service.IClienteService;
import peluqueriarosy.app.models.service.IDisponibleService;
import peluqueriarosy.app.models.service.IOcupacionService;
import peluqueriarosy.app.models.service.IReservaService;
import peluqueriarosy.app.models.service.IServicioService;

@Controller
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

	@Autowired
	private EmailServiceImpl emailService;

	private ArrayList<Categoria> categorias = new ArrayList<>();

	@GetMapping("/servicios")
	public String listar(Model model, Locale locale) {

		categorias = (ArrayList<Categoria>) categoriaService.findAll();
		ArrayList<Servicio> servicios = (ArrayList<Servicio>) servicioService.findAll();

		model.addAttribute("categorias", categorias);
		model.addAttribute("servicios", servicios);
		return "servicios";
	}

	@GetMapping("/formServicio")
	public String crear(Map<String, Object> model) {
		Servicio servicio = new Servicio();
		model.put("servicio", servicio);
		model.put("categorias", categorias);
		return "formServicio";
	}

	@GetMapping("/formServicio/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Servicio servicio = null;

		if (id > 0) {
			servicio = servicioService.findOne(id);

			if (servicio == null) {
				flash.addFlashAttribute("error", "El servicio no existe en la BBDD!");
				return "redirect:/servicios";
			}
		} else {
			flash.addFlashAttribute("error", "El ID de servicio no puede ser cero!");
			return "redirect:/formServicio";
		}
		model.put("servicio", servicio);
		model.put("categorias", categorias);
		return "formServicio";
	}

	@RequestMapping(value = "/formServicio", method = RequestMethod.POST)
	public String guardar(@Valid Servicio servicios, BindingResult result, Model model, RedirectAttributes flash) {
		Servicio servicio = null;
		if (servicios.getId() != null) {
			servicio = servicioService.findOne(servicios.getId());
		} else {
			servicio = new Servicio();
		}

		if (result.hasErrors()) {
			flash.addFlashAttribute("error", "¡ERROR! Asegúrese de rellenar los campos");
			if (servicio.getId() != null) {
				return "redirect:formServicio/" + servicio.getId();
			} else {
				return "redirect:formServicio/";
			}
		} else {
			if (servicios.getHoras() == 0 && servicios.getMinutos() == 0) {
				flash.addFlashAttribute("error", "¡ERROR! La duración del servicio no puede ser 0");
				if (servicio.getId() != null) {
					return "redirect:formServicio/" + servicio.getId();
				} else {
					return "redirect:formServicio/";
				}
			}
		}

		String mensajeFlash = (servicio.getId() != null) ? "Servicio editado con éxito" : "Servicio creado con éxito";
		servicio.setCategoria(servicios.getCategoria());
		servicio.setDescripcion(servicios.getDescripcion());
		servicio.setHoras(servicios.getHoras());
		servicio.setMinutos(servicios.getMinutos());
		servicio.setNombre(servicios.getNombre());
		servicio.setPrecio(servicios.getPrecio());
		servicioService.save(servicio);
		flash.addFlashAttribute("success", mensajeFlash);

		return "redirect:/servicios/";
	}

	@GetMapping("/eliminarS/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		Servicio servicio = servicioService.findOne(id);
		if (servicio != null) {
			servicioService.delete(servicio);
			flash.addFlashAttribute("success", "Servicio eliminado con éxito");

		} else {
			flash.addFlashAttribute("error", "¡ERROR! El servicio no existe en la base de datos. No se pudo eliminar");
		}
		return "redirect:/servicios/";
	}

	@GetMapping(value = "servicios/{term}", produces = { "application/json" })
	public @ResponseBody List<String> cargarOcupacion(@PathVariable String term) {
		System.out.println("Cargando ocupación..." + term);

		List<Disponible> listaDisponibles = disponibleService.findAll();
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

		return listadoHorasDisponibles;
	}

	@GetMapping(value = "servicios/reserva/{term}", produces = { "application/json" })
	public @ResponseBody String reservar(@PathVariable String term, RedirectAttributes flash) {

		User e = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Cliente cli = clienteService.findByEmail(e.getUsername());
		String[] parte = term.split("\\*");
		String servicioStr = parte[0];
		Long idServicio = Long.parseLong(servicioStr);
		String dia = parte[1];
		String hora = parte[2];
		Reserva reserva=null;
		if(parte.length==4) {
			Long idCliente = Long.parseLong(parte[3]);
			reserva = new Reserva(dia, hora, idCliente);
		}else {
			reserva = new Reserva(dia, hora, cli.getId());
		}

		reserva.setServicio(servicioService.findOne(idServicio));

		// Guardar reserva
		reservaService.save(reserva);
		// Guardar ocupacion
		List<Disponible> listaDisponibles = disponibleService.findAll();
		int duracionServicioMin = servicioService.findOne(idServicio).getMinutos();
		int duracionServicioHor = servicioService.findOne(idServicio).getHoras();
		int duracionServicio = (duracionServicioHor * 60) + duracionServicioMin;

		float tramos = duracionServicio / 15;
		tramos = tramos + 1;

		Disponible disp = disponibleService.findByHora(hora);

		int index = listaDisponibles.indexOf(disp);
		double tramosGuardar = Math.round(tramos) + index;

		for (int i = index; i < tramosGuardar; i++) {
			if (i < listaDisponibles.size()) {
				Disponible dispon = listaDisponibles.get(i);
				if (dispon != null) {
					String id = dia + "/" + dispon.getHora();
					Ocupacion ocu = new Ocupacion(id, dia, dispon.getHora(), reserva);
					ocupacionService.save(ocu);
				}
			}
		}
		flash.addFlashAttribute("success", "Su reserva se ha realizado correctamente");

		/*
		 * 
		 * 
		 * PROBANDO GOOGLE CALENDAR
		 * 
		 * 
		 */

		// Email message
		SimpleMailMessage emailReserva = new SimpleMailMessage();
		emailReserva.setFrom("peluqueriarosyinfo@gmail.com");
		emailReserva.setTo("peluqueriarosyinfo@gmail.com");
		emailReserva.setSubject("Nueva reserva");
		emailReserva.setText(
				"Nueva reserva. \n Día: "+reserva.getDia()+"\n Hora: "+reserva.getHora()+"\n Cliente: "+clienteService.findById(reserva.getCliente_id()).getNombre()+" "+clienteService.findById(reserva.getCliente_id()).getApellido()+" \n Servicio: "+reserva.getServicio().getNombre());
		emailService.sendEmail(emailReserva);

		return "redirect:/reserva";
	}

}
