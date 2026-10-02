package com.acos.career.repository;

import com.acos.career.entity.Company;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link Company}. */
public interface CompanyRepository extends JpaRepository<Company, UUID> {

  /**
   * Finds a non-archived company by id and owner.
   *
   * @param id company id
   * @param ownerId owner id
   * @return matching company, if present
   */
  Optional<Company> findByIdAndOwnerIdAndArchivedFalse(UUID id, UUID ownerId);

  /**
   * Lists non-archived companies for an owner ordered by name.
   *
   * @param ownerId owner id
   * @return companies
   */
  List<Company> findByOwnerIdAndArchivedFalseOrderByNameAsc(UUID ownerId);

  /**
   * Reports whether an active company name already exists for an owner.
   *
   * @param ownerId owner id
   * @param name company name
   * @return {@code true} when present
   */
  boolean existsByOwnerIdAndNameAndArchivedFalse(UUID ownerId, String name);

  /**
   * Reports whether an active company name exists for an owner excluding a given id.
   *
   * @param ownerId owner id
   * @param name company name
   * @param id excluded company id
   * @return {@code true} when present
   */
  boolean existsByOwnerIdAndNameAndArchivedFalseAndIdNot(UUID ownerId, String name, UUID id);

  /**
   * Counts non-archived companies for an owner.
   *
   * @param ownerId owner id
   * @return company count
   */
  long countByOwnerIdAndArchivedFalse(UUID ownerId);
}
