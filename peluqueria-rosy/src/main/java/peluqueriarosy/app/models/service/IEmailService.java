package peluqueriarosy.app.models.service;

import org.springframework.mail.SimpleMailMessage;

public interface IEmailService {
	public void sendEmail(SimpleMailMessage email);
}
