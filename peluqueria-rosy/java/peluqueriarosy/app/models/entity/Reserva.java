package peluqueriarosy.app.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "reservas")
public class Reserva implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String dia;
	
	private String hora;
	
	private Long cliente_id;

//	private Long servicio_id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Servicio servicio;
	
	public Reserva() {
	}

	public Reserva(String dia, String hora, Long cliente_id) {
		super();
		this.dia = dia;
		this.hora = hora;
		this.cliente_id = cliente_id;
//		this.servicio_id = servicio_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public Long getCliente_id() {
		return cliente_id;
	}

	public void setCliente_id(Long cliente_id) {
		this.cliente_id = cliente_id;
	}

/*	public Long getServicio_id() {
		return servicio_id;
	}

	public void setServicio_id(Long servicio_id) {
		this.servicio_id = servicio_id;
	}
	*/
	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	private static final long serialVersionUID = 1L;
	
}
