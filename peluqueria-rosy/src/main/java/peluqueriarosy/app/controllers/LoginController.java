package peluqueriarosy.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(@RequestParam(value="error", required=false) String error,
			@RequestParam(value="logout", required=false) String logout,
			Model model, Principal principal, RedirectAttributes flash) {
		
		if(principal != null) {
			flash.addFlashAttribute("info","Ya ha iniciado sesión");
			return "redirect:/";
		}
		
		if(error != null) {
			model.addAttribute("error", "¡ERROR! Usuario o contraseña incorrecta");
		}
		
		if(logout != null) {
			flash.addFlashAttribute("info","Ha cerrado sesión correctamente");
			model.addAttribute("info", "Ha cerrado sesión correctamente");
			return "redirect:/";
		}
		
		return "login";
	}
}
