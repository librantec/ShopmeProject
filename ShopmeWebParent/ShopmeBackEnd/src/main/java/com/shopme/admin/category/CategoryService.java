package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	@Autowired
	private CategoryRepository repo;
	
	public List<Category> listAll() {
		List<Category> rootCategories = repo.findRootCategories(Sort.by("name").ascending());
	    return listHierarchicalCategories(rootCategories);
	}

	
	private List<Category> listHierarchicalCategories(List<Category> rootCategories) {
	    List<Category> hierarchicalCategories = new ArrayList<>();

	    for (Category rootCategory : rootCategories) {
	        // 1. I-add ang Root (e.g. Computers)
	        hierarchicalCategories.add(Category.copyFull(rootCategory));

	        // 2. I-convert ang Set ngadto sa List para ma-sort nato
	        List<Category> children = new ArrayList<>(rootCategory.getChildren());
	        
	        // 3. I-sort ang children alphabetically base sa name
	        children.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));

	        for (Category subCategory : children) {
	            // 4. I-add ang Sub (e.g. --Laptops)
	            String name = "--" + subCategory.getName();
	            hierarchicalCategories.add(Category.copyFull(subCategory, name));

	            listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
	        }
	    }

	    return hierarchicalCategories;
	}


	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, 
	        Category parent, int subLevel) {
	    int newSubLevel = subLevel + 1;
	    
	    // I-sort gihapon ang mga apo/children dinhi
	    List<Category> children = new ArrayList<>(parent.getChildren());
	    children.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));

	    for (Category subCategory : children) {
	        String name = "";
	        for (int i = 0; i < newSubLevel; i++) {
	            name += "--";
	        }
	        name += subCategory.getName();

	        hierarchicalCategories.add(Category.copyFull(subCategory, name));

	        listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel);
	    }
	}

	
	public Category save(Category category) {
		return repo.save(category);
	}
	
	
	// Sa CategoryService.java
	public List<Category> listCategoriesUsedInForm() {
	    List<Category> categoriesUsedInForm = new ArrayList<>();
	    
	    // I-sort ang Root Categories alphabetically (ASC)
	    Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());
	    
	    for (Category category : categoriesInDB) {
	        // I-add ang root category
	        categoriesUsedInForm.add(Category.copyIdAndName(category));
	        
	        // I-add ang mga sub-categories (recursively)
	        listSubCategoriesUsedInForm(categoriesUsedInForm, category, 1);
	    }
	    
	    return categoriesUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int level) {
	    int newLevel = level + 1;
	    Set<Category> children = sortSubCategories(parent.getChildren()); // I-sort ang mga anak
	    
	    for (Category subCategory : children) {
	        String name = "";
	        for (int i = 0; i < newLevel; i++) {
	            name += "--"; // I-add ang dashes depende sa level
	        }
	        name += subCategory.getName();
	        
	        categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
	        
	        listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newLevel);
	    }
	}
	
	
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
	    return sortSubCategories(children, "asc");
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
	    SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
	        @Override
	        public int compare(Category cat1, Category cat2) {
	            if (sortDir.equals("asc")) {
	                return cat1.getName().compareTo(cat2.getName());
	            } else {
	                return cat2.getName().compareTo(cat1.getName());
	            }
	        }
	    });
	    
	    sortedChildren.addAll(children);
	    return sortedChildren;
	}

	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}

	
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Could not find any category with ID " + id);
		}
	}
	
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

	    // 1. Check uniqueness sa Name
	    Category categoryByName = repo.findByName(name);

	    if (isCreatingNew) {
	        if (categoryByName != null) return "DuplicateName";
	    } else {
	        if (categoryByName != null && categoryByName.getId() != id) {
	            return "DuplicateName";
	        }
	    }

	    // 2. Check uniqueness sa Alias
	    Category categoryByAlias = repo.findByAlias(alias);

	    if (isCreatingNew) {
	        if (categoryByAlias != null) return "DuplicateAlias";
	    } else {
	        if (categoryByAlias != null && categoryByAlias.getId() != id) {
	            return "DuplicateAlias";
	        }
	    }

	    return "OK";
	}
	
	
	public void delete(Integer id) throws CategoryNotFoundException {
	    Long countById = repo.countById(id);
	    if (countById == null || countById == 0) {
	        throw new CategoryNotFoundException("Could not find any category with ID " + id);
	    }

	    Category category = repo.findById(id).get();
	    if (!category.getChildren().isEmpty()) {
	        // Diri nimo dakpon, bai!
	        throw new RuntimeException("Cannot delete the category ID " + id + 
	            " because it has sub-categories. Delete the sub-categories first.");
	    }

	    repo.deleteById(id);
	}
	
}
