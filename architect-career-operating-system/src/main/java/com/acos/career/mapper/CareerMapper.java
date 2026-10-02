package com.acos.career.mapper;

import com.acos.career.dto.ApplicationStatusHistoryResponse;
import com.acos.career.dto.CompanyResponse;
import com.acos.career.dto.CompanySummaryResponse;
import com.acos.career.dto.InterviewResponse;
import com.acos.career.dto.JobApplicationPageResponse;
import com.acos.career.dto.JobApplicationResponse;
import com.acos.career.dto.OfferResponse;
import com.acos.career.dto.RecruiterResponse;
import com.acos.career.dto.RecruiterSummaryResponse;
import com.acos.career.entity.ApplicationStatusHistory;
import com.acos.career.entity.Company;
import com.acos.career.entity.Interview;
import com.acos.career.entity.JobApplication;
import com.acos.career.entity.Offer;
import com.acos.career.entity.Recruiter;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

/** MapStruct mappings between career domain objects and DTOs. */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CareerMapper {

  /**
   * Maps a company entity to a response DTO.
   *
   * @param company company entity
   * @return company response
   */
  CompanyResponse toCompanyResponse(Company company);

  /**
   * Maps a company entity to a summary DTO.
   *
   * @param company company entity
   * @return company summary
   */
  CompanySummaryResponse toCompanySummary(Company company);

  /**
   * Maps a recruiter entity to a response DTO.
   *
   * @param recruiter recruiter entity
   * @return recruiter response
   */
  @Mapping(target = "companyId", source = "company", qualifiedByName = "companyId")
  RecruiterResponse toRecruiterResponse(Recruiter recruiter);

  /**
   * Maps a recruiter entity to a summary DTO.
   *
   * @param recruiter recruiter entity
   * @return recruiter summary
   */
  RecruiterSummaryResponse toRecruiterSummary(Recruiter recruiter);

  /**
   * Maps a job application entity to a response DTO.
   *
   * @param application application with company (and optional recruiter) initialized
   * @return application response
   */
  JobApplicationResponse toJobApplicationResponse(JobApplication application);

  /**
   * Maps a Spring Data page of applications.
   *
   * @param page page of applications
   * @return page response
   */
  default JobApplicationPageResponse toPageResponse(Page<JobApplication> page) {
    List<JobApplicationResponse> content =
        page.getContent().stream().map(this::toJobApplicationResponse).toList();
    return new JobApplicationPageResponse(
        content,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  /**
   * Maps an interview entity to a response DTO.
   *
   * @param interview interview entity
   * @return interview response
   */
  @Mapping(target = "applicationId", source = "application.id")
  InterviewResponse toInterviewResponse(Interview interview);

  /**
   * Maps an offer entity to a response DTO.
   *
   * @param offer offer entity
   * @return offer response
   */
  @Mapping(target = "applicationId", source = "application.id")
  OfferResponse toOfferResponse(Offer offer);

  /**
   * Maps a status history entity to a response DTO.
   *
   * @param history status history entity
   * @return status history response
   */
  ApplicationStatusHistoryResponse toHistoryResponse(ApplicationStatusHistory history);

  /**
   * Extracts a company id from an optional company association.
   *
   * @param company company, may be {@code null}
   * @return company id, or {@code null}
   */
  @Named("companyId")
  default UUID companyId(Company company) {
    return company == null ? null : company.getId();
  }
}
