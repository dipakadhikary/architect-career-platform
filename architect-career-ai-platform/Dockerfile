FROM python:3.13-slim AS base

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    APP_HOME=/app

WORKDIR ${APP_HOME}

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY pyproject.toml README.md ./
COPY third_party/acos_ai_contracts ./third_party/acos_ai_contracts
COPY app ./app

RUN pip install --upgrade pip \
    && pip install . \
    && python -c "import pathlib, shutil, app; shutil.copytree(pathlib.Path('app'), pathlib.Path(app.__file__).resolve().parent, dirs_exist_ok=True, ignore=shutil.ignore_patterns('__pycache__', '*.pyc'))" \
    && rm -rf app \
    && useradd --create-home --uid 10001 --shell /usr/sbin/nologin acos \
    && chown -R acos:acos ${APP_HOME}

USER acos

EXPOSE 8090

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD curl -fsS http://127.0.0.1:8090/health || exit 1

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8090"]
