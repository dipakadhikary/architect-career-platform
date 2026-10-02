package com.acos.portfolio.repository;

import com.acos.portfolio.entity.Certification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link Certification}. */
public interface CertificationRepository extends JpaRepository<Certification, UUID> {

  /**
   * Finds a certification by id and owner.
   *
   * @param id certification id
   * @param ownerId owner id
   * @return matching certification, if present
   */
  Optional<Certification> findByIdAndOwnerId(UUID id, UUID ownerId);

  /**
   * Lists certifications for an owner ordered by issue date descending.
   *
   * @param ownerId owner id
   * @return certifications
   */
  List<Certification> findByOwnerIdOrderByIssuedOnDesc(UUID ownerId);
}
