package peluqueriarosy.app.controllers;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.PasswordResetToken;
import peluqueriarosy.app.models.entity.Role;
import peluqueriarosy.app.models.service.IClienteService;
import peluqueriarosy.app.models.service.IRoleService;

@Controller
public class ClienteController {

	@Autowired
	private IClienteService clienteService;
	@Autowired
	private IRoleService roleService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping({ "/", "", "/index" })
	public String listar(Model model, Locale locale) {

		model.addAttribute("titulo", "Pagina de inicio");
		return "index";
	}

	@RequestMapping("/form")
	public String crear(Map<String, Object> model) {
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flash) {

		Cliente clienteValid = clienteService.findByEmail(cliente.getEmail());

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		} else if (!cliente.getPassword().equals(cliente.getPasswordRepeat())) {
			model.addAttribute("titulo", "Formulario de cliente");
			model.addAttribute("warning", "Las contraseñas deben coincidir");
			return "form";
		} else if (clienteValid != null) {
			model.addAttribute("titulo", "Formulario de cliente");
			model.addAttribute("error", "El email que ha introducido ya existe en nuestra BBDD");
			return "form";
		}

		String password = cliente.getPassword();
		String passwordEncrypt = passwordEncoder.encode(password);
		cliente.setPassword(passwordEncrypt);
		cliente.setPasswordRepeat(null);
		cliente.setEnabled(true);

		clienteService.save(cliente);

		Role role = new Role();
		role.setAuthority("ROLE_USER");
		role.setCliente_id(cliente.getId());

		roleService.save(role);

		flash.addFlashAttribute("success", "Su cuenta se ha creado correctamente. En su correo está la confirmación");
		return "redirect:login";
	}
}
