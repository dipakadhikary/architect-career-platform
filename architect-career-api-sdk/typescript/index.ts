/**
 * Public entry for the ACOS TypeScript SDK.
 *
 * Prefer the custom service wrappers for application code.
 * Import from `@acos/api-sdk/generated` only when you need raw generated clients.
 */
export * from './custom';
export type { ConfigurationParameters, Configuration } from './generated/configuration';
export { Configuration as GeneratedConfiguration } from './generated/configuration';
export { BASE_PATH } from './generated/base';
