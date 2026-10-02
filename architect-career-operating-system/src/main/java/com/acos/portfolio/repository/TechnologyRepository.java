package com.acos.portfolio.repository;

import com.acos.portfolio.entity.Technology;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link Technology}. */
public interface TechnologyRepository extends JpaRepository<Technology, UUID> {

  /**
   * Finds a technology by owner and name.
   *
   * @param ownerId owner id
   * @param name technology name
   * @return matching technology, if present
   */
  Optional<Technology> findByOwnerIdAndName(UUID ownerId, String name);

  /**
   * Finds a technology by id and owner.
   *
   * @param id technology id
   * @param ownerId owner id
   * @return matching technology, if present
   */
  Optional<Technology> findByIdAndOwnerId(UUID id, UUID ownerId);

  /**
   * Lists technologies for an owner.
   *
   * @param ownerId owner id
   * @return technologies
   */
  List<Technology> findByOwnerIdOrderByNameAsc(UUID ownerId);

  /**
   * Finds technologies for an owner matching any of the given names.
   *
   * @param ownerId owner id
   * @param names technology names
   * @return matching technologies
   */
  List<Technology> findByOwnerIdAndNameIn(UUID ownerId, Collection<String> names);

  /**
   * Reports whether a technology name already exists for an owner.
   *
   * @param ownerId owner id
   * @param name technology name
   * @return {@code true} when present
   */
  boolean existsByOwnerIdAndName(UUID ownerId, String name);

  /**
   * Reports whether a technology name exists for an owner excluding a given id.
   *
   * @param ownerId owner id
   * @param name technology name
   * @param id excluded technology id
   * @return {@code true} when present
   */
  boolean existsByOwnerIdAndNameAndIdNot(UUID ownerId, String name, UUID id);
}
