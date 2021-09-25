package peluqueriarosy.app.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "ocupacion")
public class Ocupacion implements Serializable{

	@Id
	@NotEmpty
	private String id;
	
	@NotEmpty
	private String dia;
	
	@NotEmpty
	private String hora;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Reserva reserva;
	
	public Ocupacion() {
	}

	public Ocupacion(@NotEmpty String id, @NotEmpty String dia, @NotEmpty String hora) {
		this.id = id;
		this.dia = dia;
		this.hora = hora;
	}
	
	public Ocupacion(@NotEmpty String id, @NotEmpty String dia, @NotEmpty String hora, Reserva reserva) {
		this.id = id;
		this.dia = dia;
		this.hora = hora;
		this.reserva = reserva;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	private static final long serialVersionUID = 1L;
	
}
