package com.acos.career.repository;

import com.acos.career.entity.ApplicationStatusHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link ApplicationStatusHistory}. */
public interface ApplicationStatusHistoryRepository
    extends JpaRepository<ApplicationStatusHistory, UUID> {

  /**
   * Lists status history entries for an application ordered chronologically.
   *
   * @param applicationId application id
   * @return history entries ordered by change time ascending
   */
  List<ApplicationStatusHistory> findByApplicationIdOrderByChangedAtAsc(UUID applicationId);
}
