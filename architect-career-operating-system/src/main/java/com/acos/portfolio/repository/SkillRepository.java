package com.acos.portfolio.repository;

import com.acos.portfolio.entity.Skill;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link Skill}. */
public interface SkillRepository extends JpaRepository<Skill, UUID> {

  /**
   * Finds a skill by id and owner.
   *
   * @param id skill id
   * @param ownerId owner id
   * @return matching skill, if present
   */
  Optional<Skill> findByIdAndOwnerId(UUID id, UUID ownerId);

  /**
   * Lists skills for an owner.
   *
   * @param ownerId owner id
   * @return skills
   */
  List<Skill> findByOwnerIdOrderByNameAsc(UUID ownerId);

  /**
   * Reports whether a skill name already exists for an owner.
   *
   * @param ownerId owner id
   * @param name skill name
   * @return {@code true} when present
   */
  boolean existsByOwnerIdAndName(UUID ownerId, String name);

  /**
   * Reports whether a skill name exists for an owner excluding a given id.
   *
   * @param ownerId owner id
   * @param name skill name
   * @param id excluded skill id
   * @return {@code true} when present
   */
  boolean existsByOwnerIdAndNameAndIdNot(UUID ownerId, String name, UUID id);
}
