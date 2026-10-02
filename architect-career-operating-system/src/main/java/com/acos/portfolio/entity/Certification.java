package com.acos.portfolio.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/** User-owned professional certification. */
@Entity
@Table(name = "portfolio_certifications", schema = "acos")
public class Certification extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "name", nullable = false, length = 200)
  private String name;

  @Column(name = "issuer", nullable = false, length = 200)
  private String issuer;

  @Column(name = "credential_id", length = 200)
  private String credentialId;

  @Column(name = "credential_url", length = 500)
  private String credentialUrl;

  @Column(name = "issued_on", nullable = false)
  private LocalDate issuedOn;

  @Column(name = "expires_on")
  private LocalDate expiresOn;

  /** Creates an empty certification for JPA. */
  protected Certification() {}

  /**
   * Creates a certification for an owner.
   *
   * @param ownerId owning user id
   * @param name certification name
   * @param issuer issuing organization
   * @param issuedOn issue date
   */
  public Certification(UUID ownerId, String name, String issuer, LocalDate issuedOn) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.issuer = Objects.requireNonNull(issuer, "issuer must not be null");
    this.issuedOn = Objects.requireNonNull(issuedOn, "issuedOn must not be null");
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
   * Returns the certification name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Updates the certification name.
   *
   * @param name new name
   */
  public void setName(String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  /**
   * Returns the issuer.
   *
   * @return issuer
   */
  public String getIssuer() {
    return issuer;
  }

  /**
   * Updates the issuer.
   *
   * @param issuer new issuer
   */
  public void setIssuer(String issuer) {
    this.issuer = Objects.requireNonNull(issuer, "issuer must not be null");
  }

  /**
   * Returns the optional credential id.
   *
   * @return credential id, may be {@code null}
   */
  public String getCredentialId() {
    return credentialId;
  }

  /**
   * Updates the optional credential id.
   *
   * @param credentialId new credential id
   */
  public void setCredentialId(String credentialId) {
    this.credentialId = credentialId;
  }

  /**
   * Returns the optional credential URL.
   *
   * @return credential URL, may be {@code null}
   */
  public String getCredentialUrl() {
    return credentialUrl;
  }

  /**
   * Updates the optional credential URL.
   *
   * @param credentialUrl new credential URL
   */
  public void setCredentialUrl(String credentialUrl) {
    this.credentialUrl = credentialUrl;
  }

  /**
   * Returns the issue date.
   *
   * @return issued on date
   */
  public LocalDate getIssuedOn() {
    return issuedOn;
  }

  /**
   * Updates the issue date.
   *
   * @param issuedOn new issue date
   */
  public void setIssuedOn(LocalDate issuedOn) {
    this.issuedOn = Objects.requireNonNull(issuedOn, "issuedOn must not be null");
  }

  /**
   * Returns the optional expiry date.
   *
   * @return expires on date, may be {@code null}
   */
  public LocalDate getExpiresOn() {
    return expiresOn;
  }

  /**
   * Updates the optional expiry date.
   *
   * @param expiresOn new expiry date
   */
  public void setExpiresOn(LocalDate expiresOn) {
    this.expiresOn = expiresOn;
  }
}
