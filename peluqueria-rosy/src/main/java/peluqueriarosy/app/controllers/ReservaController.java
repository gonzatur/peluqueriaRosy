package peluqueriarosy.app.controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.Reserva;
import peluqueriarosy.app.models.service.IClienteService;

@Controller
public class ReservaController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/reserva")
	public String listar(Model model, Locale locale) {

		User e = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Cliente cli = clienteService.findByEmail(e.getUsername());
		model.addAttribute("titulo", "Sus reservas");
		model.addAttribute("cliente", cli);
		return "reserva";
	}

	@GetMapping("reserva/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") String idStr, RedirectAttributes flash) {
		User e = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Cliente cli = clienteService.findByEmail(e.getUsername());

		String[] parts = idStr.split("\\-");
		String stringID = parts[0];
		String idS = stringID.replaceAll("[^0-9]+", "");
		Long id = Long.parseLong(idS);
		Reserva reserva = clienteService.findReservaById(id);
		if (reserva != null) {
			if (!cli.getRoles().get(0).getAuthority().equals("ROLE_ADMIN")) {
				Cliente cliente = clienteService.findById(reserva.getCliente_id());
				if (cliente.getEmail().equals(cli.getEmail())) {
					clienteService.deleteReserva(id);
					flash.addFlashAttribute("success", "Reserva eliminada con éxito");
				} else {
					flash.addFlashAttribute("error", "¡ERROR! No se pudo borrar su reserva");
				}
			}else {
				clienteService.deleteReserva(id);
				flash.addFlashAttribute("success", "Reserva eliminada con éxito");
			}
		} else {
			flash.addFlashAttribute("error", "¡ERROR! La reserva no existe en la base de datos. No se pudo eliminar");
		}

		if (cli.getRoles().get(0).getAuthority().equals("ROLE_ADMIN")) {
			stringID = parts[1];
			idS = stringID.replaceAll("[^0-9]+", "");
			id = Long.parseLong(idS);
			return "redirect:/usuarios/ver/" + id;

		}
		return "redirect:/reserva";
	}
}
