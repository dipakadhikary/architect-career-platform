# MapStruct

## Standard

Entity ↔ DTO conversion uses MapStruct mapper interfaces registered as Spring beans.

## Compiler configuration

From `pom.xml` annotation processor args:

- `mapstruct.defaultComponentModel=spring`
- `mapstruct.unmappedTargetPolicy=ERROR`

Unmapped target properties fail compilation. Mappers must be complete.

## Mapper conventions

1. Declare `@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)`
2. Keep mappers in `com.acos.<feature>.mapper`
3. Use `@Mapping` for nested ids/associations
4. Use `@Named` helpers for reusable conversions (roles, tags, optional association ids, progress calculations)
5. Inject mappers into services; do not map in controllers

## Existing mappers

- `UserMapper`
- `KnowledgeNoteMapper`
- `LearningMapper`
- `PortfolioMapper`
- `CareerMapper`
- `DashboardMapper`

## Lombok note

Lombok is on the annotation processor path with `lombok-mapstruct-binding`, but application source does not use Lombok annotations. Entities/DTOs expose ordinary accessors/record components for MapStruct.

## Evidence

- `pom.xml` (MapStruct dependency + compiler args)
- `src/main/java/com/acos/*/mapper/*Mapper.java`
