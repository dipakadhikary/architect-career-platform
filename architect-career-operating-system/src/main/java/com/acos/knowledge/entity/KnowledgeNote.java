package com.acos.knowledge.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.BatchSize;

/** Markdown knowledge note owned by a platform user. */
@Entity
@Table(name = "knowledge_notes", schema = "acos")
public class KnowledgeNote extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "category_id",
      foreignKey = @ForeignKey(name = "fk_knowledge_notes_category_id"))
  private Category category;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "summary", nullable = false, length = 500)
  private String summary;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @ManyToMany(fetch = FetchType.LAZY)
  @BatchSize(size = 25)
  @JoinTable(
      name = "knowledge_note_tags",
      schema = "acos",
      joinColumns =
          @JoinColumn(
              name = "note_id",
              nullable = false,
              foreignKey = @ForeignKey(name = "fk_knowledge_note_tags_note_id")),
      inverseJoinColumns =
          @JoinColumn(
              name = "tag_id",
              nullable = false,
              foreignKey = @ForeignKey(name = "fk_knowledge_note_tags_tag_id")))
  private final Set<Tag> tags = new HashSet<>();

  /** Creates an empty note for JPA. */
  protected KnowledgeNote() {}

  /**
   * Creates a knowledge note.
   *
   * @param ownerId owning user id
   * @param title note title
   * @param summary short summary
   * @param content markdown body
   */
  public KnowledgeNote(UUID ownerId, String title, String summary, String content) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.title = Objects.requireNonNull(title, "title must not be null");
    this.summary = Objects.requireNonNull(summary, "summary must not be null");
    this.content = Objects.requireNonNull(content, "content must not be null");
  }

  /**
   * Returns the owning user id.
   *
   * @return owner id
   */
  public UUID getOwnerId() {
    return ownerId;
  }

  /**
   * Returns the optional category.
   *
   * @return category, may be {@code null}
   */
  public Category getCategory() {
    return category;
  }

  /**
   * Assigns the category.
   *
   * @param category category, may be {@code null}
   */
  public void setCategory(Category category) {
    this.category = category;
  }

  /**
   * Returns the title.
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Updates the title.
   *
   * @param title new title
   */
  public void setTitle(String title) {
    this.title = Objects.requireNonNull(title, "title must not be null");
  }

  /**
   * Returns the summary.
   *
   * @return summary
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Updates the summary.
   *
   * @param summary new summary
   */
  public void setSummary(String summary) {
    this.summary = Objects.requireNonNull(summary, "summary must not be null");
  }

  /**
   * Returns the markdown content.
   *
   * @return markdown body
   */
  public String getContent() {
    return content;
  }

  /**
   * Updates the markdown content.
   *
   * @param content new markdown body
   */
  public void setContent(String content) {
    this.content = Objects.requireNonNull(content, "content must not be null");
  }

  /**
   * Returns an unmodifiable view of assigned tags.
   *
   * @return tags
   */
  public Set<Tag> getTags() {
    return Collections.unmodifiableSet(tags);
  }

  /**
   * Replaces assigned tags.
   *
   * @param tags new tags
   */
  public void replaceTags(Set<Tag> tags) {
    Objects.requireNonNull(tags, "tags must not be null");
    this.tags.clear();
    this.tags.addAll(tags);
  }
}
