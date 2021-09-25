package peluqueriarosy.app.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
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
public class PeluqueriaController {

	@Autowired
	private ICategoriaService categoriaService;

	@Autowired
	private IDisponibleService disponibleService;

	@Autowired
	private IServicioService servicioService;

	@Autowired
	private IOcupacionService ocupacionService;

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IReservaService reservaService;

	@Autowired
	private EmailServiceImpl emailService;

	@RequestMapping("/peluqueria")
	public String usuarios(Map<String, Object> model) {
		User e = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Cliente cli = clienteService.findByEmail(e.getUsername());

		List<Categoria> listadoCat = categoriaService.findAll();
		List<Disponible> listadoDisponibilidad = disponibleService.findAll();
		ArrayList<Disponible> tramoManana = new ArrayList<>();
		ArrayList<Disponible> tramoTarde = new ArrayList<>();
		ArrayList<String> horario = new ArrayList<>();
		ArrayList<String> horasDisponibles = new ArrayList<>();
		
		//Obtener todos los clientes ordenados por nombre
		List<Cliente> listadoCli = clienteService.findByRoleUser();
		listadoCli.sort(Comparator.comparing(Cliente::getNombre));
	
		//Obtener todos los servicios para reservar el administrador
		List<Servicio> listadoServicios = servicioService.findAll();
		
		//Dias reservados por el administrador
		ArrayList<Reserva> listaReservasAdmin = new ArrayList<>();
		List<Reserva> reservasAdmin = cli.getReservas();
		reservasAdmin.sort(Comparator.comparing(Reserva::getDia));
		for (Reserva reserva : reservasAdmin) {
			listaReservasAdmin.add(reserva);
		}

		// Obtener hora inicio y fin mañana
		for (Disponible disponible : listadoDisponibilidad) {
			if (disponible.getTramo() == false) {
				tramoManana.add(disponible);
			} else {
				tramoTarde.add(disponible);
			}
		}

		String time = "";
		for (int hora = 0; hora < 24; hora++) {
			for (int min = 00; min < 60; min += 30) {
				time = "";
				if (hora < 10) {
					time = "0" + hora + ":" + min;
				} else {
					time = hora + ":" + min;
				}
				if (min == 0) {
					time += "0";
				}
				horasDisponibles.add(time);
			}
		}

		horario.add(tramoManana.get(0).getHora());
		horario.add(tramoManana.get(tramoManana.size() - 1).getHora());
		horario.add(tramoTarde.get(0).getHora());
		horario.add(tramoTarde.get(tramoTarde.size() - 1).getHora());

		model.put("listadoCli", listadoCli);
		model.put("listadoServ", listadoServicios);
		model.put("categorias", listadoCat);
		model.put("horario", horario);
		model.put("horasDisponibles", horasDisponibles);
		model.put("listaReservasAdmin", listaReservasAdmin);
		return "peluqueria";
	}

	@GetMapping("/eliminarCat/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		Categoria categoria = categoriaService.findById(id);
		if (categoria != null) {
			categoriaService.delete(categoria);
			flash.addFlashAttribute("success", "Categoría eliminada con éxito");

		} else {
			flash.addFlashAttribute("error", "¡ERROR! La categoría no existe en la base de datos. No se pudo eliminar");
		}
		return "redirect:/peluqueria";
	}

	@GetMapping("/formCategoria")
	public String crear(Map<String, Object> model) {
		Categoria categoria = new Categoria();
		model.put("categoria", categoria);
		return "formCategoria";
	}

	@RequestMapping("/formCategoria/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Categoria categoria = null;

		if (id > 0) {
			categoria = categoriaService.findById(id);
			if (categoria == null) {
				flash.addFlashAttribute("error", "La categoría no existe en la BBDD!");
				return "redirect:/peluqueria";
			}
		} else {
			flash.addFlashAttribute("error", "El ID de cliente no puede ser cero!");
			return "redirect:/peluqueria";
		}
		model.put("categoria", categoria);
		return "formCategoria";
	}

	@RequestMapping(value = "/formCategoria", method = RequestMethod.POST)
	public String guardar(@Valid Categoria categoria, BindingResult result, Model model, RedirectAttributes flash) {

		if (result.hasErrors()) {
			flash.addFlashAttribute("error", "¡ERROR! Asegúrese de rellenar el campo nombre");
			if (categoria.getId() != null) {
				return "redirect:formCategoria/" + categoria.getId();
			} else {
				return "redirect:formCategoria";
			}
		}
		categoria.setServicios(categoriaService.findById(categoria.getId()).getServicios());
		String mensajeFlash = (categoria.getId() != null) ? "Categoría editada con éxito"
				: "Categoría creada con éxito";
		categoriaService.save(categoria);
		flash.addFlashAttribute("success", mensajeFlash);

		return "redirect:peluqueria";
	}

	@RequestMapping(value = "/changeHorario", method = RequestMethod.POST)
	public String changePassword(ModelAndView modelAndView, HttpServletRequest request,
			@RequestParam("inicioMnn") String inicioMnn, @RequestParam("finMnn") String finMnn,
			@RequestParam("inicioTrd") String inicioTrd, @RequestParam("finTrd") String finTrd,
			RedirectAttributes flash) {

		if (inicioMnn.isEmpty() || finMnn.isEmpty() || inicioTrd.isEmpty() || finTrd.isEmpty()) {
			flash.addFlashAttribute("error", "¡ERROR! Para cambiar horario seleccione hora en los 4 campos");
		} else {
			// Hora y minutos INICIO MNN
			String[] parts = inicioMnn.split("\\:");
			int horaInicioMnn = Integer.parseInt(parts[0]);
			int minInicioMnn = Integer.parseInt(parts[1]);
			// Hora y minutos FIN MNN
			String[] parts2 = finMnn.split("\\:");
			int horaFinMnn = Integer.parseInt(parts2[0]);
			int minFinMnn = Integer.parseInt(parts2[1]);
			// Hora y minutos INICIO TRD
			String[] parts3 = inicioTrd.split("\\:");
			int horaInicioTrd = Integer.parseInt(parts3[0]);
			int minInicioTrd = Integer.parseInt(parts3[1]);
			// Hora y minutos FIN TRD
			String[] parts4 = finTrd.split("\\:");
			int horaFinTrd = Integer.parseInt(parts4[0]);
			int minFinTrd = Integer.parseInt(parts4[1]);

			// Controlar que hora inicio y fin MNN esten ok
			if (horaInicioMnn > horaFinMnn || horaInicioMnn == horaFinMnn && minInicioMnn > minFinMnn) {
				flash.addFlashAttribute("error", "¡ERROR! El horario de mañana no es correcto, revise las horas");
				return "redirect:peluqueria";
			}
			if (horaInicioTrd > horaFinTrd || horaInicioTrd == horaFinTrd && minInicioTrd > minFinTrd) {
				flash.addFlashAttribute("error", "¡ERROR! El horario de tarde no es correcto, revise las horas");
				return "redirect:peluqueria";
			}

			disponibleService.deleteAll();

			String time = "";
			for (int hora = horaInicioMnn; hora < horaFinMnn; hora++) {
				for (int min = 00; min < 60; min += 15) {
					time = "";
					if (hora < 10) {
						time = "0" + hora + ":" + min;
					} else {
						time = hora + ":" + min;
					}
					if (min == 0) {
						time += "0";
					}
					Disponible disponible = new Disponible(time, false);
					disponibleService.save(disponible);
				}
			}

			for (int hora = horaInicioTrd; hora < horaFinTrd; hora++) {
				for (int min = 00; min < 60; min += 15) {
					time = "";
					if (hora < 10) {
						time = "0" + hora + ":" + min;
					} else {
						time = hora + ":" + min;
					}
					if (min == 0) {
						time += "0";
					}
					Disponible disponible = new Disponible(time, true);
					disponibleService.save(disponible);
				}
			}

			flash.addFlashAttribute("success", "El horario ha sido modificado correctamente");

		}
		return "redirect:peluqueria";
	}

	@RequestMapping(value = "/reservarDia", method = RequestMethod.POST)
	public String reservarDia(@RequestParam("reservarDia") String reservarDia, RedirectAttributes flash) {

		User e = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Cliente cli = clienteService.findByEmail(e.getUsername());
		// Validar que fecha NO esté vacía
		if (reservarDia.isEmpty()) {
			flash.addFlashAttribute("error", "¡ERROR! Seleccione día para poder reservarlo");
			return "redirect:peluqueria";
		}

		// Validar que el día a reservar NO esté ocupado
		List<Reserva> listaReservasAdmin = cli.getReservas();
		for (Reserva reserva2 : listaReservasAdmin) {
			if (reserva2.getDia().equals(reservarDia)) {
				flash.addFlashAttribute("error", "¡ERROR! El día que intenta reservar ya está reservado");
				return "redirect:peluqueria";
			}
		}

		// Eliminar reservas existentes para este día
		List<Reserva> listaReservas = reservaService.findByDia(reservarDia);
		for (Reserva reserva : listaReservas) {
			reservaService.delete(reserva);

			SimpleMailMessage emailReserva = new SimpleMailMessage();
			emailReserva.setFrom("peluqueriarosyinfo@gmail.com");
			emailReserva.setTo("peluqueriarosyinfo@gmail.com");
			emailReserva.setSubject("RESERVA CANCELADA");
			emailReserva.setText(
					"La reserva con ID " + reserva.getId() + " ha sido eliminada. Estos eran los datos: \n Cliente: "
							+ clienteService.findById(reserva.getCliente_id()).getNombre() + " "
							+ clienteService.findById(reserva.getCliente_id()).getApellido() + " \n Servicio: "
							+ reserva.getServicio().getNombre());
			emailService.sendEmail(emailReserva);
		}

		// Reservar día
		List<Disponible> listaDisponibles = disponibleService.findAll();
		Reserva reserva = new Reserva(reservarDia, "00:00", cli.getId());
		reservaService.save(reserva);
		for (int i = 0; i < listaDisponibles.size(); i++) {
			Disponible dispon = listaDisponibles.get(i);
			String id = reservarDia + "/" + dispon.getHora();
			Ocupacion ocu = new Ocupacion(id, reservarDia, dispon.getHora(), reserva);
			ocupacionService.save(ocu);
		}
		flash.addFlashAttribute("success", "Día reservado correctamente. Las reservas que había para este día se han cancelado, revise su correo");

		return "redirect:peluqueria";
	}

	@GetMapping("/eliminarDia/{id}")
	public String eliminarDia(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		Reserva reservaBorrar = clienteService.findReservaById(id);
		reservaService.delete(reservaBorrar);
		flash.addFlashAttribute("success", "El día "+reservaBorrar.getDia() +" está de nuevo disponible para poder reservarlo");

		return "redirect:/peluqueria";
	}
}
