package com.shopme.admin.product;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {
	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
	    Brand brand = entityManager.find(Brand.class, 13); 
	    Category category = entityManager.find(Category.class, 17);

	    Product product = new Product();
	    product.setName("Acer Aspire Desktop"); 
	    product.setAlias("acer_aspire_desktop");
	    product.setShortDescription("Powerful desktop from Acer");
	    product.setFullDescription("Full description for Acer Aspire");
	    
	    // I-comment lang sa ni nato
	    product.setMainImage("default.png"); 
	    
	    product.setBrand(brand);
	    product.setCategory(category);
	    
	    product.setPrice(600);
	    product.setCost(500);
	    product.setEnabled(true);
	    product.setInStock(true);
	    
	    product.setCreatedTime(new Date());
	    product.setUpdatedTime(new Date());

	    Product savedProduct = repo.save(product);
	    
	    assertThat(savedProduct).isNotNull();
	    assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllProducts() {
		Iterable<Product> iterableProducts = repo.findAll();
		
		iterableProducts.forEach(System.out::println);
	}

}
