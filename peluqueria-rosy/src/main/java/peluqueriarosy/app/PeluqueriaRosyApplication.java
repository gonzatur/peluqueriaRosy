package peluqueriarosy.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import peluqueriarosy.app.models.service.IUploadFileService;


@SpringBootApplication
public class PeluqueriaRosyApplication {

	@Autowired
	IUploadFileService uploadFileService;
	
	public static void main(String[] args) {
		SpringApplication.run(PeluqueriaRosyApplication.class, args);
	}

}
