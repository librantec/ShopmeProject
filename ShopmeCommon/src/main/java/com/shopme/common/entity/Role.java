package com.shopme.common.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "roles")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 40, nullable = false, unique = true)
	private String name;

	@Column(length = 150, nullable = false)
	private String description;

	// Default Constructor (Kinahanglanon sa JPA)
	public Role() {
	}
	
	public Role(Integer id) {
		this.id = id;
	}


	// Constructor para sa Name lang (Dali ra i-assign unya)
	public Role(String name) {
		this.name = name;
	}

	// Constructor para sa Name ug Description (Optional pero mapuslanon)
	public Role(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	// Importante: I-override ang hashCode ug equals para sa Many-to-Many
    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
        	return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role other = (Role) obj;
        if (id == null) {
			if (other.id != null)
				return false;
			
		} else if (!id.equals(other.id)) 
			return false;
        return true;
		
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
	@Override
    public String toString() {
        return this.name;
    }

}
