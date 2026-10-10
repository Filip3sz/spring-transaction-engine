package code.filipesz.springtransactionengine.repositories;

import code.filipesz.springtransactionengine.dto.CategorySummaryResponse;
import code.filipesz.springtransactionengine.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);

    @Query("""
                SELECT new code.filipesz.springtransactionengine.dto.CategorySummaryResponse(
                    c.id, c.name, COUNT(p)
                )
                FROM Category c
                LEFT JOIN Product p ON p.category = c
                GROUP BY c.id, c.name
            """)
    List<CategorySummaryResponse> findAllWithProductCount();
}
