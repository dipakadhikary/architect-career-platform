#!/usr/bin/env python3
"""Generate ACOS tutorial backend package."""
from __future__ import annotations
from pathlib import Path

BASE = Path(r"D:\architect-career-system\architect-career-operating-system\src\main\java\com\acos\tutorial")
TEST = Path(r"D:\architect-career-system\architect-career-operating-system\src\test\java\com\acos\tutorial")


def w(rel: str, content: str, test: bool = False) -> None:
    root = TEST if test else BASE
    path = root / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.strip() + "\n", encoding="utf-8")
    print("wrote", path.relative_to(root.parent.parent.parent.parent.parent))


# --- entities ---
w(
    "entity/TutorialTopic.java",
    r'''
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
  @JoinColumn(
      name = "parent_id",
      foreignKey = @ForeignKey(name = "fk_tutorial_topics_parent_id"))
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
''',
)

w(
    "entity/TutorialConcept.java",
    r'''
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
''',
)

w(
    "entity/TutorialQuestion.java",
    r'''
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

/** Markdown Q&amp;A item for a tutorial topic. */
@Entity
@Table(name = "tutorial_questions", schema = "acos")
public class TutorialQuestion extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "topic_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_tutorial_questions_topic_id"))
  private TutorialTopic topic;

  @Column(name = "question", nullable = false, columnDefinition = "TEXT")
  private String question;

  @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
  private String answer;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  protected TutorialQuestion() {}

  public TutorialQuestion(TutorialTopic topic, String question, String answer, int sortOrder) {
    this.topic = Objects.requireNonNull(topic, "topic");
    this.question = Objects.requireNonNull(question, "question");
    this.answer = Objects.requireNonNull(answer, "answer");
    this.sortOrder = sortOrder;
  }

  public TutorialTopic getTopic() {
    return topic;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = Objects.requireNonNull(question, "question");
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = Objects.requireNonNull(answer, "answer");
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
  }
}
''',
)

print("entities done")
