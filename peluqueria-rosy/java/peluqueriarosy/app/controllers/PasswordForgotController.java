package peluqueriarosy.app.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import peluqueriarosy.app.models.dao.PasswordResetTokenRepository;
import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.PasswordForgotDto;
import peluqueriarosy.app.models.entity.PasswordResetToken;
import peluqueriarosy.app.models.service.ClienteServiceImpl;

@Controller
@RequestMapping("/forgot-password")
public class PasswordForgotController {

	@Autowired
	private ClienteServiceImpl clienteService;
	@Autowired
	private PasswordResetTokenRepository tokenRepository;
//	@Autowired
//	private EmailService emailService;

	@ModelAttribute("forgotPasswordForm")
	public PasswordForgotDto forgotPasswordDto() {
		return new PasswordForgotDto();
	}

	@GetMapping
	public String displayForgotPasswordPage() {
		return "forgot-password";
	}

	@PostMapping
	public String processForgotPasswordForm(@ModelAttribute("forgotPasswordForm") @Valid PasswordForgotDto form,
			BindingResult result, HttpServletRequest request) {

		if (result.hasErrors()) {
			return "forgot-password";
		}

		Cliente cli = clienteService.findByEmail(form.getEmail());
		if (cli == null) {
			result.rejectValue("email", null, "No hemos encontrado ninguna cuenta con este email");
			return "forgot-password";
		}

		PasswordResetToken token = new PasswordResetToken();
		token.setToken(UUID.randomUUID().toString());
		token.setCliente(cli);
		token.setExpiryDate(30);
		tokenRepository.save(token);

/*		Mail mail = new Mail();
		mail.setFrom("no-reply@memorynotfound.com");
		mail.setTo(user.getEmail());
		mail.setSubject("Password reset request");
*/
		Map<String, Object> model = new HashMap<>();
		model.put("token", token);
		model.put("cli", cli);
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		model.put("resetUrl", url + "/reset-password?token=" + token.getToken());
//		mail.setModel(model);
//		emailService.sendEmail(mail);

		return "redirect:/forgot-password?success";

	}

}
