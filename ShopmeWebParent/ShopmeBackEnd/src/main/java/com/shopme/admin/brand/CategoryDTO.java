package com.shopme.admin.brand;

public class CategoryDTO {
	private Integer id;
	private String name;
	
	public CategoryDTO() {
		
	}
	// Importante ni nga constructor para sa imong BrandRestController
	public CategoryDTO(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
		
	//Getters and Setters
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

}
