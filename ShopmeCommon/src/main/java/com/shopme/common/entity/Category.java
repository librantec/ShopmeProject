package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Transient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 128, nullable = false, unique = true)
	private String name;

	@Column(length = 64, nullable = false, unique = true)
	private String alias;

	@Column(length = 128, nullable = false)
	private String image;

	private boolean enabled;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	@OneToMany(mappedBy = "parent")
	private Set<Category> children = new HashSet<>();
	
	public Category() {
	}
	
	public Category(Integer id) {
		this.id = id;
	}
	
	public Category(String name) {
		this.name = name;
		this.alias = name; // Maayo ni para dili null ang alias
	    this.image = "default.png";
	}

	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}
	
	
    public Category(Integer id, String name, String alias) {
		super();
		this.id = id;
		this.name = name;
		this.alias = alias;
	}
    
 // I-add ni sa sulod sa Category class
    public static Category copyIdAndName(Category category) {
        Category copy = new Category();
        copy.setId(category.getId());
        copy.setName(category.getName());
        
        return copy;
    }

    public static Category copyIdAndName(Integer id, String name) {
        Category copy = new Category();
        copy.setId(id);
        copy.setName(name);
        
        return copy;
    }


	// 1. Para sa pag-copy sa Root Category (nga walay dashes)
    public static Category copyFull(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setImage(category.getImage());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        
        return copyCategory;
    }

    // 2. Para sa pag-copy sa Sub-category (nga naay bag-ong name nga naay dashes)
    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name); // Dinhi ibutang ang "--Laptops"
        
        return copyCategory;
    }

    @Transient
    public String getImagePath() {
    	//Kung walay ID o walay image name, ipakita ang default placeholder
        if (this.id == null || this.image == null || this.image.isEmpty() || this.image.equals("default.png")) {
        	return "/images/image-thumbnail.png";
        }
        
        return "/category-images/" + this.id + "/" + this.image;
    }
    
    public boolean isHasChildren() {
    	return hasChildren;
    }
    
    public void setHasChildren(boolean hasChildren) {
    	this.hasChildren = hasChildren;
    }
    
    @Transient
    public boolean hasChildren;
    
    @Override
    public String toString() {
    	return this.name;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}
}
