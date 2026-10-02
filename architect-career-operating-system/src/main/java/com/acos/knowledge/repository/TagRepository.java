package com.acos.knowledge.repository;

import com.acos.knowledge.entity.Tag;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link Tag}. */
public interface TagRepository extends JpaRepository<Tag, UUID> {

  /**
   * Finds a tag by owner and name.
   *
   * @param ownerId owner id
   * @param name tag name
   * @return matching tag, if present
   */
  Optional<Tag> findByOwnerIdAndName(UUID ownerId, String name);

  /**
   * Finds tags for an owner matching any of the given names.
   *
   * @param ownerId owner id
   * @param names tag names
   * @return matching tags
   */
  List<Tag> findByOwnerIdAndNameIn(UUID ownerId, Collection<String> names);
}
