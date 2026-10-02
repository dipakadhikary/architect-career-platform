package com.acos.tutorial.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

/** Hierarchical tutorial topic owned by a platform user. */
@Entity
@Table(name = "tutorial_topics", schema = "acos")
public class TutorialTopic extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_tutorial_topics_parent_id"))
  private TutorialTopic parent;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "slug", nullable = false, length = 200)
  private String slug;

  @Column(name = "path", nullable = false, length = 2000)
  private String path;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  protected TutorialTopic() {}

  public TutorialTopic(
      UUID ownerId, TutorialTopic parent, String title, String slug, String path, int sortOrder) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
    this.parent = parent;
    this.title = Objects.requireNonNull(title, "title");
    this.slug = Objects.requireNonNull(slug, "slug");
    this.path = Objects.requireNonNull(path, "path");
    this.sortOrder = sortOrder;
  }

  public UUID getOwnerId() {
    return ownerId;
  }

  public TutorialTopic getParent() {
    return parent;
  }

  public void setParent(TutorialTopic parent) {
    this.parent = parent;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = Objects.requireNonNull(title, "title");
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = Objects.requireNonNull(slug, "slug");
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = Objects.requireNonNull(path, "path");
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
  }
}
