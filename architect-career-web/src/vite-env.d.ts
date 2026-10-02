/// <reference types="vite/client" />
/// <reference types="vite-plugin-pwa/client" />
/// <reference types="vite-plugin-pwa/react" />

interface ImportMetaEnv {
  readonly VITE_APP_NAME: string;
  readonly VITE_APP_VERSION: string;
  readonly VITE_API_BASE_URL: string;
  readonly VITE_API_PROXY_TARGET: string;
  readonly VITE_ENABLE_QUERY_DEVTOOLS: string;
  readonly VITE_AI_PLATFORM_ENABLED: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
