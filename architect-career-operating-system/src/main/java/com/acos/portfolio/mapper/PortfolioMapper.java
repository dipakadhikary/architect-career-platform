package com.acos.portfolio.mapper;

import com.acos.portfolio.dto.AchievementResponse;
import com.acos.portfolio.dto.CertificationResponse;
import com.acos.portfolio.dto.PortfolioProjectPageResponse;
import com.acos.portfolio.dto.PortfolioProjectResponse;
import com.acos.portfolio.dto.SkillResponse;
import com.acos.portfolio.dto.TechnologyResponse;
import com.acos.portfolio.entity.Achievement;
import com.acos.portfolio.entity.Certification;
import com.acos.portfolio.entity.PortfolioProject;
import com.acos.portfolio.entity.Skill;
import com.acos.portfolio.entity.Technology;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

/** MapStruct mappings between portfolio domain objects and DTOs. */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PortfolioMapper {

  /**
   * Maps a technology entity to a response DTO.
   *
   * @param technology technology entity
   * @return technology response
   */
  TechnologyResponse toTechnologyResponse(Technology technology);

  /**
   * Maps a project entity to a response DTO including sorted technology names.
   *
   * @param project project with technologies initialized
   * @return project response
   */
  @Mapping(target = "technologies", source = "technologies", qualifiedByName = "toTechnologyNames")
  PortfolioProjectResponse toProjectResponse(PortfolioProject project);

  /**
   * Maps a Spring Data page of projects.
   *
   * @param page page of projects
   * @return page response
   */
  default PortfolioProjectPageResponse toPageResponse(Page<PortfolioProject> page) {
    List<PortfolioProjectResponse> content =
        page.getContent().stream().map(this::toProjectResponse).toList();
    return new PortfolioProjectPageResponse(
        content,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  /**
   * Maps a skill entity to a response DTO.
   *
   * @param skill skill entity
   * @return skill response
   */
  SkillResponse toSkillResponse(Skill skill);

  /**
   * Maps an achievement entity to a response DTO.
   *
   * @param achievement achievement entity
   * @return achievement response
   */
  AchievementResponse toAchievementResponse(Achievement achievement);

  /**
   * Maps a certification entity to a response DTO.
   *
   * @param certification certification entity
   * @return certification response
   */
  CertificationResponse toCertificationResponse(Certification certification);

  /**
   * Maps technologies to sorted unique names.
   *
   * @param technologies technology set
   * @return sorted technology names
   */
  @Named("toTechnologyNames")
  default List<String> toTechnologyNames(Set<Technology> technologies) {
    if (technologies == null || technologies.isEmpty()) {
      return List.of();
    }
    return technologies.stream()
        .map(Technology::getName)
        .sorted(Comparator.naturalOrder())
        .toList();
  }
}
