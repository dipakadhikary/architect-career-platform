FROM node:22-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

ARG VITE_APP_NAME=ACOS
ARG VITE_APP_VERSION=0.1.0
ARG VITE_API_BASE_URL=
ARG VITE_ENABLE_QUERY_DEVTOOLS=false
ARG VITE_AI_PLATFORM_ENABLED=true

ENV VITE_APP_NAME=$VITE_APP_NAME \
    VITE_APP_VERSION=$VITE_APP_VERSION \
    VITE_API_BASE_URL=$VITE_API_BASE_URL \
    VITE_ENABLE_QUERY_DEVTOOLS=$VITE_ENABLE_QUERY_DEVTOOLS \
    VITE_AI_PLATFORM_ENABLED=$VITE_AI_PLATFORM_ENABLED

RUN npm run build

FROM nginx:1.27-alpine

RUN rm /etc/nginx/conf.d/default.conf

COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html

ENV API_UPSTREAM=http://host.docker.internal:8080

EXPOSE 80
