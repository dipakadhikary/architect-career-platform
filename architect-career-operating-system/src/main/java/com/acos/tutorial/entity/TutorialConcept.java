package com.acos.tutorial.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.Objects;

/** Markdown concept content for a tutorial topic. */
@Entity
@Table(name = "tutorial_concepts", schema = "acos")
public class TutorialConcept extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "topic_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "fk_tutorial_concepts_topic_id"))
  private TutorialTopic topic;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  protected TutorialConcept() {}

  public TutorialConcept(TutorialTopic topic, String content) {
    this.topic = Objects.requireNonNull(topic, "topic");
    this.content = Objects.requireNonNull(content, "content");
  }

  public TutorialTopic getTopic() {
    return topic;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = Objects.requireNonNull(content, "content");
  }
}
