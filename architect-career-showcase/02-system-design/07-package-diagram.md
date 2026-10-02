# Package Diagram

## Platform packages

```mermaid
flowchart TB
    subgraph com_acos["com.acos"]
        APP[AcosApplication]
        AUTH[auth]
        KNOW[knowledge]
        LEARN[learning]
        PORT[portfolio]
        CAREER[career]
        DASH[dashboard]
        COMMON[common]
        CONFIG[config]
        ANALYTICS[analytics]
    end

    APP --> AUTH
    APP --> KNOW
    APP --> LEARN
    APP --> PORT
    APP --> CAREER
    APP --> DASH
    APP --> COMMON
    APP --> CONFIG
```

## Canonical feature package template

```mermaid
flowchart LR
    subgraph feature["com.acos.&lt;feature&gt;"]
        CTRL[controller]
        SVC[service]
        REPO[repository]
        ENT[entity]
        DTO[dto]
        MAP[mapper]
        VAL[validator]
        EX[exception]
        CFG[config]
    end

    CTRL --> SVC
    CTRL --> DTO
    SVC --> VAL
    SVC --> MAP
    SVC --> REPO
    SVC --> EX
    MAP --> DTO
    MAP --> ENT
    REPO --> ENT
    CFG --> VAL
```

## Auth packages

```mermaid
flowchart TB
    subgraph auth["com.acos.auth"]
        A_CTRL[controller]
        A_SVC[service]
        A_REPO[repository]
        A_ENT[entity]
        A_DTO[dto]
        A_MAP[mapper]
        A_VAL[validator / validation]
        A_SEC[security]
        A_TOK[token]
        A_CFG[config]
        A_EX[exception]
    end

    A_CTRL --> A_SVC
    A_SVC --> A_REPO
    A_SVC --> A_TOK
    A_SVC --> A_MAP
    A_SVC --> A_VAL
    A_CFG --> A_SEC
    A_SEC --> A_TOK
```

## Career packages

```mermaid
flowchart TB
    subgraph career["com.acos.career"]
        C_CTRL[controller]
        C_SVC[service]
        C_REPO[repository]
        C_ENT[entity]
        C_DTO[dto]
        C_MAP[mapper]
        C_VAL[validator]
        C_EX[exception]
        C_CFG[config]
        C_STATE[state]
        C_SPEC[specification]
        C_EVT[event]
    end

    C_CTRL --> C_SVC
    C_SVC --> C_REPO
    C_SVC --> C_MAP
    C_SVC --> C_VAL
    C_SVC --> C_STATE
    C_SVC --> C_SPEC
    C_SVC --> C_EVT
    C_SVC --> C_EX
    C_REPO --> C_ENT
    C_SPEC --> C_ENT
```

## Common packages

```mermaid
flowchart TB
    subgraph common["com.acos.common"]
        API[api]
        EXC[exception]
        HANDLER[handler]
        LOG[logging]
        PERS[persistence]
    end

    HANDLER --> API
    HANDLER --> EXC
    API --> LOG
    PERS --> ENT_BASE[BaseEntity]
```

## Package dependency direction

```mermaid
flowchart BT
    CTRL[controller] --> SVC[service]
    SVC --> REPO[repository]
    SVC --> COMMON[common]
    CTRL --> COMMON
    REPO --> PERS[(JPA / PostgreSQL)]
    AUTH_SEC[auth.security] --> CTRL
```

Controllers depend on service interfaces and shared API types. Services depend on repositories, mappers, validators, and (in career) state/specification/event collaborators.
