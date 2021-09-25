package peluqueriarosy.app.controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import peluqueriarosy.app.models.entity.Producto;
import peluqueriarosy.app.models.service.IProductoService;
import peluqueriarosy.app.util.paginator.PageRender;

@Controller
public class ProductoController {

	@Autowired
	private IProductoService productoService;
	
	@GetMapping("/productos")
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Locale locale) {

		Pageable pageRequest = PageRequest.of(page, 6);
		Page<Producto> productos = productoService.findAll(pageRequest);
		PageRender<Producto> pageRender = new PageRender<>("/productos", productos);

		model.addAttribute("productos", productos);
		model.addAttribute("page", pageRender);
		return "productos";
	}
}
