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
