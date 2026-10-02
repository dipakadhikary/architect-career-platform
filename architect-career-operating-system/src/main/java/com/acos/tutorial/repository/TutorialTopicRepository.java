package com.acos.tutorial.repository;

import com.acos.tutorial.entity.TutorialTopic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorialTopicRepository extends JpaRepository<TutorialTopic, UUID> {

  Optional<TutorialTopic> findByIdAndOwnerId(UUID id, UUID ownerId);

  Optional<TutorialTopic> findByOwnerIdAndPath(UUID ownerId, String path);

  List<TutorialTopic> findByOwnerIdOrderByPathAsc(UUID ownerId);

  List<TutorialTopic> findByOwnerIdAndParentIdOrderBySortOrderAsc(UUID ownerId, UUID parentId);

  @Query(
      """
      select coalesce(max(t.sortOrder), -1) from TutorialTopic t
      where t.ownerId = :ownerId
        and ((:parentId is null and t.parent is null) or t.parent.id = :parentId)
      """)
  int findMaxSortOrder(@Param("ownerId") UUID ownerId, @Param("parentId") UUID parentId);

  @Query(
      """
      select count(t) > 0 from TutorialTopic t
      where t.ownerId = :ownerId and t.path = :path
        and (:excludeId is null or t.id <> :excludeId)
      """)
  boolean existsPath(
      @Param("ownerId") UUID ownerId,
      @Param("path") String path,
      @Param("excludeId") UUID excludeId);

  @Query(
      """
      select t from TutorialTopic t
      where t.ownerId = :ownerId and t.path like concat(:pathPrefix, '/%')
      order by t.path asc
      """)
  List<TutorialTopic> findDescendantsByPathPrefix(
      @Param("ownerId") UUID ownerId, @Param("pathPrefix") String pathPrefix);
}
