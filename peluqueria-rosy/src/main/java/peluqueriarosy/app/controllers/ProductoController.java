package peluqueriarosy.app.controllers;

import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import peluqueriarosy.app.models.entity.Producto;
import peluqueriarosy.app.models.service.IProductoService;
import peluqueriarosy.app.models.service.IUploadFileService;
import peluqueriarosy.app.util.paginator.PageRender;

@Controller
public class ProductoController {

	@Autowired
	private IProductoService productoService;

	@Autowired
	private IUploadFileService uploadFileService;

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = uploadFileService.load(filename);

		return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + recurso.getFilename() + "\"").body(recurso);

		// "attachment; filename=\""+ recurso.getFilename() + "\"");
	}

	@GetMapping("/productos")
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Locale locale) {

		Pageable pageRequest = PageRequest.of(page, 6);
		Page<Producto> productos = productoService.findAll(pageRequest);
		PageRender<Producto> pageRender = new PageRender<>("/productos", productos);

		model.addAttribute("productos", productos);
		model.addAttribute("page", pageRender);
		return "productos";
	}
	
	@GetMapping("/formProducto")
	public String crear(Map<String, Object> model) {
		Producto producto = new Producto();
		model.put("producto", producto);
		return "formProducto";
	}

	@RequestMapping("/formProducto/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Producto producto = null;

		if (id > 0) {
			producto = productoService.findById(id);
			if (producto == null) {
				flash.addFlashAttribute("error", "El producto no existe en la BBDD!");
				return "redirect:/productos";
			}
		} else {
			flash.addFlashAttribute("error", "El ID de cliente no puede ser cero!");
			return "redirect:/formProducto";
		}
		model.put("producto", producto);
		return "formProducto";
	}

	@RequestMapping(value = "/formProducto", method = RequestMethod.POST)
	public String guardar(@Valid Producto producto, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash) {

		if (result.hasErrors() || foto.isEmpty()) {
			flash.addFlashAttribute("error", "¡ERROR! Asegúrese de rellenar los campos y elegir una foto");
			if (producto.getId() != null) {
				return "redirect:formProducto/" + producto.getId();
			} else {
				return "redirect:formProducto/";
			}
		}
		if (producto.getId() != null && producto.getId() > 0 && producto.getFoto() != null
				&& producto.getFoto().length() > 0) {
			uploadFileService.delete(producto.getFoto());

		}
			System.out.println("Tamaño foto: ->" + foto.getSize());
			String uniqueFilename = uploadFileService.copy(foto);

			flash.addFlashAttribute("info", "Ha subido correctamente la imagen");

			producto.setFoto(uniqueFilename);

			String mensajeFlash = (producto.getId() != null) ? "Producto editado con éxito"
					: "Producto creado con éxito";
			productoService.save(producto);
			flash.addFlashAttribute("success", mensajeFlash);
		
		return "redirect:productos";
	}

	@GetMapping("/eliminarPro/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		Producto producto = productoService.findById(id);
		if (producto != null) {
			productoService.delete(producto);
			flash.addFlashAttribute("success", "Producto eliminado con éxito");

		} else {
			flash.addFlashAttribute("error", "¡ERROR! El producto no existe en la base de datos. No se pudo eliminar");
		}
		return "redirect:/productos/";
	}
}
