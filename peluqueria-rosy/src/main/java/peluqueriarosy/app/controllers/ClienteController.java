package peluqueriarosy.app.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import peluqueriarosy.app.models.entity.Cliente;
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

	@GetMapping({ "/", "", "/index/","/index" })
	public String listar(Model model, Locale locale) {

		model.addAttribute("titulo", "Pagina de inicio");
		return "index";
	}

	@RequestMapping("/usuarios")
	public String usuarios(Map<String, Object> model) {
		List<Cliente> listadoCli = clienteService.findByRoleUser();
		model.put("clientes", listadoCli);
		model.put("titulo", "Formulario de Cliente");
		return "usuarios";
	}
	
	@GetMapping(value = "usuarios/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = clienteService.findById(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "¡ERROR! El cliente no existe en la base de datos");
			return "redirect/usuarios";
		}

		model.put("cliente", cliente);
		model.put("titulo", "Reservas del cliente: " + cliente.getNombre());
		return "ver";
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
			model.addAttribute("warning", "¡ERROR! Las contraseñas deben coincidir");
			return "form";
		} else if (clienteValid != null) {
			model.addAttribute("titulo", "Formulario de cliente");
			model.addAttribute("error", "¡ERROR! El email que ha introducido ya existe en nuestra BBDD");
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
	
	@GetMapping(value = "usuarios/{term}", produces = { "application/json" })
	public @ResponseBody String reservar(@PathVariable String term, RedirectAttributes flash) {

		System.out.println("USUARIOS: "+term);

		String[] parts = term.split("\\-");
		String stringID = parts[1];
		    
		String idS = stringID.replaceAll("[^0-9]+", "");
		Long id =Long.parseLong(idS);
		
		Cliente cli = clienteService.findById(id);

		if(cli.getEnabled()) {
			cli.setEnabled(false);
		}else {
			cli.setEnabled(true);
		}
		clienteService.save(cli);
		return "redirect:/usuarios";
	}
	
	@GetMapping("/eliminarC/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		Cliente cliente = clienteService.findById(id);
		if (cliente != null) {
			clienteService.delete(cliente);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito");

		} else {
			flash.addFlashAttribute("error", "¡ERROR! El cliente no existe en la base de datos. No se pudo eliminar");
		}
		return "redirect:/usuarios/";
	}

}
