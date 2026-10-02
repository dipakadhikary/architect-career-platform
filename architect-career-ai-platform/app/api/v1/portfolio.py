"""Contract-aligned Portfolio AI endpoints (workflow orchestration)."""

from __future__ import annotations

from acos_ai_contracts.models.portfolio_review_request import PortfolioReviewRequest
from acos_ai_contracts.models.portfolio_review_response import PortfolioReviewResponse
from acos_ai_contracts.models.skill_gap_request import SkillGapRequest
from acos_ai_contracts.models.skill_gap_response import SkillGapResponse
from fastapi import APIRouter, Depends

from app.api.dependencies import get_agentic_service, get_optional_principal
from app.orchestration.enterprise.facades import PipelinedAgenticFacade
from app.shared.security.authentication import AuthenticatedPrincipal

router = APIRouter(tags=["Portfolio AI"])


@router.post(
    "/api/v1/ai/portfolio/review",
    response_model=PortfolioReviewResponse,
    response_model_by_alias=True,
    summary="Review portfolio",
)
async def review_portfolio(
    body: PortfolioReviewRequest,
    service: PipelinedAgenticFacade = Depends(get_agentic_service),
    principal: AuthenticatedPrincipal | None = Depends(get_optional_principal),
) -> PortfolioReviewResponse:
    output = await service.run_workflow(
        "portfolio_review",
        {
            "user_id": body.user_id,
            "project_ids": list(body.project_ids or []),
            "target_role": body.target_role,
        },
        principal=principal,
    )
    return PortfolioReviewResponse(
        summary=str(output["summary"]),
        strengths=output.get("strengths"),
        improvements=output.get("improvements"),
        score=output.get("score"),
    )


@router.post(
    "/api/v1/ai/portfolio/skill-gap/analyze",
    response_model=SkillGapResponse,
    response_model_by_alias=True,
    summary="Analyze skill gaps",
)
async def analyze_skill_gap(
    body: SkillGapRequest,
    service: PipelinedAgenticFacade = Depends(get_agentic_service),
    principal: AuthenticatedPrincipal | None = Depends(get_optional_principal),
) -> SkillGapResponse:
    output = await service.run_workflow(
        "skill_gap",
        {
            "user_id": body.user_id,
            "target_role": body.target_role,
            "current_skills": list(body.current_skills or []),
            "project_technologies": list(body.project_technologies or []),
        },
        principal=principal,
    )
    return SkillGapResponse(
        summary=str(output["summary"]),
        missingSkills=output.get("missing_skills"),
        recommendedActions=output.get("recommended_actions"),
    )
