package peluqueriarosy.app.models.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "disponible")
public class Disponible implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	private String hora;

	@NotNull
	@Column(columnDefinition = "tinyint(1)")
	private Boolean tramo;

	public Disponible() {
		super();
	}

	public Disponible(@NotEmpty String hora, @NotNull Boolean tramo) {
		this.hora = hora;
		this.tramo = tramo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public Boolean getTramo() {
		return tramo;
	}

	public void setTramo(Boolean tramo) {
		this.tramo = tramo;
	}

	private static final long serialVersionUID = 1L;
}
