# Module Diagram

## Purpose

ACOS is a modular monolith. Modules are Java packages under `com.acos`, not separate Maven artifacts.

## Module map

```mermaid
flowchart TB
    APP[AcosApplication]

    subgraph Features
        AUTH[auth]
        KNOW[knowledge]
        LEARN[learning]
        PORT[portfolio]
        CAREER[career]
        DASH[dashboard]
    end

    subgraph Shared
        COMMON[common]
        CONFIG[config]
    end

    RESERVED[analytics<br/>package-info only]

    APP --> AUTH
    APP --> KNOW
    APP --> LEARN
    APP --> PORT
    APP --> CAREER
    APP --> DASH
    APP --> COMMON
    APP --> CONFIG

    AUTH --> COMMON
    KNOW --> COMMON
    LEARN --> COMMON
    PORT --> COMMON
    CAREER --> COMMON
    DASH --> COMMON

    AUTH --> CONFIG
    KNOW -.-> CONFIG
    LEARN -.-> CONFIG
    PORT -.-> CONFIG
    CAREER -.-> CONFIG
    DASH -.-> CONFIG
```

## Dependency rules reflected by the code

1. Feature modules depend on `common` for API envelope, exceptions, logging, and `BaseEntity`.
2. Feature modules do not expose JPA entities through controllers.
3. Features are largely isolated; they share identity through Auth JWT/`AcosUserDetails` and ownership ids.
4. `config` hosts platform OpenAPI and JPA auditing configuration.
5. `analytics` is reserved and currently empty of runtime code.

## Auth as the security module

```mermaid
flowchart LR
    AUTH[auth]
    AUTH --> SEC[security]
    AUTH --> TOK[token]
    AUTH --> CTRL[controller]
    AUTH --> SVC[service]
    AUTH --> REPO[repository]
    AUTH --> ENT[entity]
    FEAT[Other feature controllers] --> SEC
```

All protected feature controllers rely on the Auth security filter chain and principal type.

## Career internal modules

```mermaid
flowchart TB
    CAREER[career]
    CAREER --> CTRL[controller]
    CAREER --> SVC[service]
    CAREER --> REPO[repository]
    CAREER --> ENT[entity]
    CAREER --> DTO[dto]
    CAREER --> MAP[mapper]
    CAREER --> VAL[validator]
    CAREER --> ST[state]
    CAREER --> SPEC[specification]
    CAREER --> EV[event]
    CAREER --> CFG[config]
    CAREER --> EX[exception]

    SVC --> ST
    SVC --> SPEC
    SVC --> EV
    SVC --> REPO
    SVC --> MAP
    SVC --> VAL
```

## Package-by-feature template

Most implemented features follow:

```text
com.acos.<feature>
  config
  controller
  dto
  entity
  exception
  mapper
  repository
  service
  validator
```
