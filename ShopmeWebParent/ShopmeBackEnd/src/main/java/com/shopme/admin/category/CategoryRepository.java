package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository
		extends PagingAndSortingRepository<Category, Integer>, CrudRepository<Category, Integer> {
		
		//@Query("SELECT c FROM Category c WHERE c.parent.id is NULL ORDER BY c.name ASC")
		//public List<Category> listRootCategories();
	
		// Mao ni ang saktong method para sa findRootCategories
	    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
	    public List<Category> findRootCategories(Sort sort);
		
		
		// Usba kini para mahimong usa ra ka Category object ang ibalik
	    public Category findByName(String name);

	    // Idugang kini para sa pag-check sa Alias
	    public Category findByAlias(String alias);
	    
	    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	    @Modifying
	    public void updateEnabledStatus(Integer id, boolean enabled);


		public Long countById(Integer id);

}
