package peluqueriarosy.app.models.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import peluqueriarosy.app.models.entity.Producto;

public interface IProductoService {

	public Page<Producto> findAll(Pageable pageable);
}
