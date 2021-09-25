package peluqueriarosy.app.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
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
	
	public Ocupacion() {
	}

	public Ocupacion(@NotEmpty String id, @NotEmpty String dia, @NotEmpty String hora) {
		this.id = id;
		this.dia = dia;
		this.hora = hora;
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

	
	private static final long serialVersionUID = 1L;
	
}
