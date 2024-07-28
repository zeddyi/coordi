package com.musinsa.coordi.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CoordiRepository extends JpaRepository<Coordi, Long> {
    Optional<Coordi> findFirstByCategoryOrderByPriceAsc(Category category);
    Optional<Coordi> findFirstByCategoryOrderByPriceDesc(Category category);

    @Query(
            value = "select new com.musinsa.coordi.domain.LowestBrand(brand, sum(price)) "
                    + "from Coordi "
                    + "group by brand "
                    + "order by sum(price) "
                    + "limit 1"
    )
    LowestBrand findLowestBrand();

    List<Coordi> findAllByBrand(String brand);

    List<Coordi> findAllByCategoryAndPrice(Category category, Integer price);

    boolean existsByBrand(String brand);

    List<Coordi> findAllByBrandAndCategoryIn(String brand, List<Category> categories);

    void deleteAllByBrand(String brand);


}
