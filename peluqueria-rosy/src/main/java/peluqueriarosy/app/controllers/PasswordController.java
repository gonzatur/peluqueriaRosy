package peluqueriarosy.app.controllers;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.service.ClienteServiceImpl;
import peluqueriarosy.app.models.service.EmailServiceImpl;

@Controller
public class PasswordController {

	@Autowired
	private ClienteServiceImpl clienteService;

	@Autowired
	private EmailServiceImpl emailService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Process form submission from forgotPassword page
	@RequestMapping(value = "/forgot", method = RequestMethod.POST)
	public String processForgotPasswordForm(ModelAndView modelAndView, @RequestParam("email") String userEmail,
			HttpServletRequest request, RedirectAttributes flash) {

		System.out.println(userEmail);
		// Lookup user in database by e-mail
		Cliente cli = clienteService.findByEmail(userEmail);

		if (cli == null) {
			flash.addFlashAttribute("error", "El correo que ha introducido no corresponde a ningún cliente.");
		} else {
			// Generate random 36-character string token for reset password
			cli.setResetToken(UUID.randomUUID().toString());

			// Save token to database
			clienteService.save(cli);

			String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

			// Email message
			SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
			passwordResetEmail.setFrom("gonzalo.mejias.parraga@gmail.com");
			passwordResetEmail.setTo(cli.getEmail());
			passwordResetEmail.setSubject("Cambio de contraseña");
			passwordResetEmail.setText(
					"To reset your password, click the link below:\n" + appUrl + "/reset?token=" + cli.getResetToken());
			emailService.sendEmail(passwordResetEmail);

			// Add success message to view
			flash.addFlashAttribute("success", "Hemos enviado un enlace al correo " + cli.getEmail()
					+ ". Siga los pasos para cambiar su contraseña");
		}

		return "redirect:login";
	}

	// Display form to reset password
	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public ModelAndView displayResetPasswordPage(ModelAndView modelAndView, @RequestParam("token") String token) {

		Cliente cli = clienteService.findUserByResetToken(token);

		if (cli != null) { // Token found in DB
			modelAndView.addObject("resetToken", token);
		} else { // Token not found in DB
			modelAndView.addObject("error",
					"¡ERROR! La ruta que ha introducido no es correcta, asegúrese de introducirla correctamente.");
		}

		modelAndView.setViewName("resetPassword");
		return modelAndView;
	}

	// Process reset password form
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public ModelAndView setNewPassword(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams,
			RedirectAttributes redir) {

		// Find the user associated with the reset token
		Cliente cli = clienteService.findUserByResetToken(requestParams.get("token"));

		// This should always be non-null but we check just in case
		if (cli != null) {

			// Validar equals password
			String password = requestParams.get("password");
			String passwordR = requestParams.get("passwordR");

			if (password.equals(passwordR)) {
				// Set new password
				cli.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));

				// Set the reset token to null so it cannot be used again
				cli.setResetToken(null);

				// Save user
				clienteService.save(cli);

				// In order to set a model attribute on a redirect, we must use
				// RedirectAttributes
				modelAndView.addObject("success", "¡Excelente! Su contraseña ha sido cambiada correctamente.");

				modelAndView.setViewName("login");
				return modelAndView;
			} else {
				modelAndView.addObject("error", "¡ERROR! Las contraseñas deben coincidir.");
				modelAndView.addObject("resetToken", requestParams.get("token"));
				modelAndView.setViewName("resetPassword");
				return modelAndView;
			}

		} else {
			modelAndView.addObject("error", "¡ERROR! No es posible modificar la contraseña a través de esta ruta");
			modelAndView.setViewName("resetPassword");
		}

		return modelAndView;
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public String changePassword(HttpServletRequest request,
			@RequestParam("passwordActual") String passwordActual, @RequestParam("passwordNew") String passwordNew,
			@RequestParam("passwordNewR") String passwordNewR, RedirectAttributes flash) {

		User e = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Cliente cli = clienteService.findByEmail(e.getUsername());

		String value = request.getHeader("referer");
		System.out.println("es?" + value + "¿es");
		String[] parts = value.split("\\/");
		String vista = parts[parts.length - 1];
		if (vista.equals("") || vista.equals("index")) {
			vista = "/";
		}
	
		if (cli == null) {
			flash.addFlashAttribute("error", "¡ERROR! Esto no debería de pasar, intente de nuevo cargar su sesión.");
		} else {
			boolean isPasswordMatches = bCryptPasswordEncoder.matches(passwordActual, cli.getPassword());
			if (isPasswordMatches) {
				if (passwordNew.equals(passwordNewR)) {
					String passwordEncrypt = bCryptPasswordEncoder.encode(passwordNew);
					cli.setPassword(passwordEncrypt);
					clienteService.save(cli);
					flash.addFlashAttribute("success", "Su contraseña ha sido cambiada.");
				} else {
					flash.addFlashAttribute("error", "¡ERROR! Las contraseñas deben coincidir.");
				}

			} else {
				flash.addFlashAttribute("error",
						"¡ERROR! Su contraseña actual es errónea, asegúrese de indicar la correcta.");
			}

		}

		return "redirect:" + vista;
	}

	// Going to reset page without a token redirects to login page
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
		return new ModelAndView("redirect:login");
	}

	@RequestMapping(value = "/contactar", method = RequestMethod.POST)
	public String contactar(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("phone") String phone, @RequestParam("message") String message, RedirectAttributes flash) {

		SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
		passwordResetEmail.setFrom("peluqueriarosyinfo@gmail.com");
		passwordResetEmail.setTo("peluqueriarosyinfo@gmail.com");
		passwordResetEmail.setSubject("Nuevo Mensaje");
		passwordResetEmail.setText(message + "\n \n ENVIADO POR: " + email);
		emailService.sendEmail(passwordResetEmail);
		flash.addFlashAttribute("success", "Mensaje enviado correctamente.");
		return "redirect:index";
	}
}