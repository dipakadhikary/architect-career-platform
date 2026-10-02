package com.acos.career.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/** Offer associated with a job application. */
@Entity
@Table(name = "career_offers", schema = "acos")
public class Offer extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "application_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_career_offers_application_id"))
  private JobApplication application;

  @Column(name = "base_salary", nullable = false, precision = 12, scale = 2)
  private BigDecimal baseSalary;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "joining_bonus", precision = 12, scale = 2)
  private BigDecimal joiningBonus;

  @Column(name = "annual_bonus", precision = 12, scale = 2)
  private BigDecimal annualBonus;

  @Column(name = "stock_options", length = 500)
  private String stockOptions;

  @Column(name = "location", length = 200)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(name = "work_mode", length = 32)
  private WorkMode workMode;

  @Column(name = "joining_date")
  private LocalDate joiningDate;

  @Column(name = "notice_period_days")
  private Integer noticePeriodDays;

  @Enumerated(EnumType.STRING)
  @Column(name = "offer_status", nullable = false, length = 32)
  private OfferStatus offerStatus;

  @Column(name = "offer_expiry_date")
  private LocalDate offerExpiryDate;

  @Column(name = "notes", length = 2000)
  private String notes;

  @Column(name = "archived", nullable = false)
  private boolean archived;

  @Column(name = "archived_at")
  private Instant archivedAt;

  /** Creates an empty offer for JPA. */
  protected Offer() {}

  /**
   * Creates an offer for a job application, always starting as {@link OfferStatus#PENDING}.
   *
   * @param application owning application
   * @param baseSalary base salary
   * @param currency currency code
   */
  public Offer(JobApplication application, BigDecimal baseSalary, String currency) {
    this.application = Objects.requireNonNull(application, "application must not be null");
    this.baseSalary = Objects.requireNonNull(baseSalary, "baseSalary must not be null");
    this.currency = Objects.requireNonNull(currency, "currency must not be null");
    this.offerStatus = OfferStatus.PENDING;
    this.archived = false;
  }

  /**
   * Returns the owning job application.
   *
   * @return application
   */
  public JobApplication getApplication() {
    return application;
  }

  /**
   * Returns the base salary.
   *
   * @return base salary
   */
  public BigDecimal getBaseSalary() {
    return baseSalary;
  }

  /**
   * Updates the base salary.
   *
   * @param baseSalary new base salary
   */
  public void setBaseSalary(BigDecimal baseSalary) {
    this.baseSalary = Objects.requireNonNull(baseSalary, "baseSalary must not be null");
  }

  /**
   * Returns the currency code.
   *
   * @return currency
   */
  public String getCurrency() {
    return currency;
  }

  /**
   * Updates the currency code.
   *
   * @param currency new currency
   */
  public void setCurrency(String currency) {
    this.currency = Objects.requireNonNull(currency, "currency must not be null");
  }

  /**
   * Returns the optional joining bonus.
   *
   * @return joining bonus, may be {@code null}
   */
  public BigDecimal getJoiningBonus() {
    return joiningBonus;
  }

  /**
   * Updates the optional joining bonus.
   *
   * @param joiningBonus new joining bonus
   */
  public void setJoiningBonus(BigDecimal joiningBonus) {
    this.joiningBonus = joiningBonus;
  }

  /**
   * Returns the optional annual bonus.
   *
   * @return annual bonus, may be {@code null}
   */
  public BigDecimal getAnnualBonus() {
    return annualBonus;
  }

  /**
   * Updates the optional annual bonus.
   *
   * @param annualBonus new annual bonus
   */
  public void setAnnualBonus(BigDecimal annualBonus) {
    this.annualBonus = annualBonus;
  }

  /**
   * Returns the optional stock option details.
   *
   * @return stock options, may be {@code null}
   */
  public String getStockOptions() {
    return stockOptions;
  }

  /**
   * Updates the optional stock option details.
   *
   * @param stockOptions new stock options
   */
  public void setStockOptions(String stockOptions) {
    this.stockOptions = stockOptions;
  }

  /**
   * Returns the optional work location.
   *
   * @return location, may be {@code null}
   */
  public String getLocation() {
    return location;
  }

  /**
   * Updates the optional work location.
   *
   * @param location new location
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Returns the optional work mode.
   *
   * @return work mode, may be {@code null}
   */
  public WorkMode getWorkMode() {
    return workMode;
  }

  /**
   * Updates the optional work mode.
   *
   * @param workMode new work mode
   */
  public void setWorkMode(WorkMode workMode) {
    this.workMode = workMode;
  }

  /**
   * Returns the optional joining date.
   *
   * @return joining date, may be {@code null}
   */
  public LocalDate getJoiningDate() {
    return joiningDate;
  }

  /**
   * Updates the optional joining date.
   *
   * @param joiningDate new joining date
   */
  public void setJoiningDate(LocalDate joiningDate) {
    this.joiningDate = joiningDate;
  }

  /**
   * Returns the optional notice period in days.
   *
   * @return notice period days, may be {@code null}
   */
  public Integer getNoticePeriodDays() {
    return noticePeriodDays;
  }

  /**
   * Updates the optional notice period in days.
   *
   * @param noticePeriodDays new notice period days
   */
  public void setNoticePeriodDays(Integer noticePeriodDays) {
    this.noticePeriodDays = noticePeriodDays;
  }

  /**
   * Returns the offer status.
   *
   * @return offer status
   */
  public OfferStatus getOfferStatus() {
    return offerStatus;
  }

  /**
   * Updates the offer status.
   *
   * @param offerStatus new offer status
   */
  public void setOfferStatus(OfferStatus offerStatus) {
    this.offerStatus = Objects.requireNonNull(offerStatus, "offerStatus must not be null");
  }

  /**
   * Returns the optional offer expiry date.
   *
   * @return offer expiry date, may be {@code null}
   */
  public LocalDate getOfferExpiryDate() {
    return offerExpiryDate;
  }

  /**
   * Updates the optional offer expiry date.
   *
   * @param offerExpiryDate new offer expiry date
   */
  public void setOfferExpiryDate(LocalDate offerExpiryDate) {
    this.offerExpiryDate = offerExpiryDate;
  }

  /**
   * Returns optional notes.
   *
   * @return notes, may be {@code null}
   */
  public String getNotes() {
    return notes;
  }

  /**
   * Updates optional notes.
   *
   * @param notes new notes
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Reports whether this offer has been soft-deleted.
   *
   * @return {@code true} when archived
   */
  public boolean isArchived() {
    return archived;
  }

  /**
   * Returns the archival timestamp.
   *
   * @return archived-at instant, may be {@code null}
   */
  public Instant getArchivedAt() {
    return archivedAt;
  }

  /** Soft-deletes this offer. */
  public void archive() {
    this.archived = true;
    this.archivedAt = Instant.now();
  }
}
