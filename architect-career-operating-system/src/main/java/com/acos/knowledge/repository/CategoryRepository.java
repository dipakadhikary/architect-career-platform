package com.acos.knowledge.repository;

import com.acos.knowledge.entity.Category;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link Category}. */
public interface CategoryRepository extends JpaRepository<Category, UUID> {

  /**
   * Finds a category by owner and name.
   *
   * @param ownerId owner id
   * @param name category name
   * @return matching category, if present
   */
  Optional<Category> findByOwnerIdAndName(UUID ownerId, String name);
}
