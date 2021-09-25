package peluqueriarosy.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import peluqueriarosy.app.models.entity.Producto;

public interface IProductoDao extends PagingAndSortingRepository<Producto, Long>{

}
