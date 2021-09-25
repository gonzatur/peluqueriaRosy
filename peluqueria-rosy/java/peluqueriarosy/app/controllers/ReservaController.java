package peluqueriarosy.app.controllers;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.Disponible;
import peluqueriarosy.app.models.entity.Ocupacion;
import peluqueriarosy.app.models.entity.Reserva;
import peluqueriarosy.app.models.service.IClienteService;
import peluqueriarosy.app.models.service.IDisponibleService;
import peluqueriarosy.app.models.service.IOcupacionService;

@Controller
@RequestMapping("/reserva")
@SessionAttributes("reserva")
public class ReservaController {

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IDisponibleService disponibleService;

	@Autowired
	private IOcupacionService ocupacionService;

	@GetMapping("/")
	public String listar(Model model, Locale locale) {

		User e = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Cliente cli = clienteService.findByEmail(e.getUsername());
		model.addAttribute("titulo", "Sus reservas");
		model.addAttribute("cliente", cli);
		return "reserva";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		Reserva reserva = clienteService.findReservaById(id);
		if (reserva != null) {
			clienteService.deleteReserva(id);
			flash.addFlashAttribute("success", "Reserva eliminada con éxito");

			// Eliminar ocupacion
			List<Disponible> listaDisponibles = (List<Disponible>) disponibleService.findAll();
			Disponible disp = disponibleService.findByHora(reserva.getHora());

			int duracionServicioMin = reserva.getServicio().getMinutos();
			int duracionServicioHor = reserva.getServicio().getHoras();
			int duracionServicio = (duracionServicioHor * 60) + duracionServicioMin;

			float tramos = duracionServicio / 15;
			tramos = tramos + 1;

			int index = listaDisponibles.indexOf(disp);
			double tramosGuardar = Math.round(tramos) + index;

			for (int i = index; i < tramosGuardar; i++) {
				System.out.println("Tramo " + i);
				if (i < listaDisponibles.size()) {
					Disponible dispon = listaDisponibles.get(i);
					if (dispon != null) {
						String idOcu = reserva.getDia() + "/" + dispon.getHora();
						Ocupacion ocu = ocupacionService.findById(idOcu);
						ocupacionService.delete(ocu);
					}
				}
			}
			return "redirect:/reserva/";

		}
		flash.addFlashAttribute("error", "La reserva no existe en la base de datos. No se pudo eliminar");
		return "redirect:/reserva/";

	}
}
