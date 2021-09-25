package peluqueriarosy.app.controllers;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import peluqueriarosy.app.models.dao.PasswordResetTokenRepository;
import peluqueriarosy.app.models.entity.Cliente;
import peluqueriarosy.app.models.entity.PasswordResetToken;
import peluqueriarosy.app.models.service.ClienteServiceImpl;

@Controller
@RequestMapping("/reset-password")
public class PasswordResetController {

    @Autowired private ClienteServiceImpl clienteService;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @ModelAttribute("passwordResetForm")
    public PasswordResetDto passwordReset() {
        return new PasswordResetDto();
    }

    @GetMapping
    public String displayResetPasswordPage(@RequestParam(required = false) String token,
                                           Model model) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null){
            model.addAttribute("error", "Could not find password reset token.");
        } else if (resetToken.isExpired()){
            model.addAttribute("error", "Token has expired, please request a new password reset.");
        } else {
            model.addAttribute("token", resetToken.getToken());
        }

        return "reset-password";
    }

    @PostMapping
    @Transactional
    public String handlePasswordReset(@ModelAttribute("passwordResetForm") @Valid PasswordResetDto form,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {

        if (result.hasErrors()){
            redirectAttributes.addFlashAttribute(BindingResult.class.getName() + ".passwordResetForm", result);
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }

        PasswordResetToken token = tokenRepository.findByToken(form.getToken());
        Cliente cli = token.getCliente();
        String updatedPassword = passwordEncoder.encode(form.getPassword());
        clienteService.updatePassword(updatedPassword, cli.getId());
        tokenRepository.delete(token);

        return "redirect:/login?resetSuccess";
    }

}
