package com.shopme.admin.brand;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.shopme.common.entity.Brand;

// Gamita ang JpaRepository para makuha ang save(), findById(), ug uban pa
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    // Kinahanglan sad ni nimo para sa validation unya
    public Brand findByName(String name);

    // Kani para sa listing page nga naay sorting
    @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
    public List<Brand> findAllOrderByName();
    
    public Long countById(Integer id);
}