#!/usr/bin/env node
/**
 * Maven validate phase: strict OpenAPI validation.
 * Fails on invalid syntax, unresolved $ref, invalid schemas, and duplicate operationIds.
 * Does not modify contract sources.
 */
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import SwaggerParser from '@apidevtools/swagger-parser';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');

const specs = [
  'openapi/knowledge/knowledge-api.yaml',
  'openapi/learning/learning-api.yaml',
  'openapi/career/career-api.yaml',
  'openapi/portfolio/portfolio-api.yaml',
  'openapi/chat/chat-api.yaml',
  'openapi/ai-platform-v1.yaml',
];

let failed = false;
let aggregateApi = null;

for (const relative of specs) {
  const filePath = path.join(root, relative);
  process.stdout.write(`[openapi] Validating ${relative} ... `);
  try {
    const api = await SwaggerParser.validate(filePath);
    if (relative.endsWith('ai-platform-v1.yaml')) {
      aggregateApi = api;
    }

    const localIds = new Map();
    collectOperationIds(relative, api, localIds);

    let fileFailed = false;
    for (const [operationId, locations] of localIds.entries()) {
      if (locations.length > 1) {
        fileFailed = true;
        failed = true;
        console.log('FAILED');
        console.error(
          `  Duplicate operationId "${operationId}" within file: ${locations.join(', ')}`,
        );
      }
    }

    if (!fileFailed) {
      const pathCount = Object.keys(api.paths || {}).length;
      console.log(`ok${pathCount ? ` (${pathCount} paths)` : ''}`);
    }
  } catch (error) {
    failed = true;
    console.log('FAILED');
    console.error(`  ${error.message}`);
  }
}

if (aggregateApi) {
  const globalIds = new Map();
  collectOperationIds('openapi/ai-platform-v1.yaml', aggregateApi, globalIds);
  for (const [operationId, locations] of globalIds.entries()) {
    if (locations.length > 1) {
      failed = true;
      console.error(
        `[openapi] Duplicate operationId "${operationId}" in composed API: ${locations.join(', ')}`,
      );
    }
  }
}

if (failed) {
  console.error('[openapi] Validation failed. Fix contract issues before generation.');
  process.exit(1);
}

console.log('[openapi] All OpenAPI specifications are valid.');

function collectOperationIds(source, api, registry) {
  for (const [pathKey, pathItem] of Object.entries(api.paths || {})) {
    if (!pathItem || typeof pathItem !== 'object') {
      continue;
    }
    for (const method of ['get', 'post', 'put', 'patch', 'delete', 'head', 'options', 'trace']) {
      const operation = pathItem[method];
      if (!operation?.operationId) {
        continue;
      }
      const entry = `${source}:${method.toUpperCase()} ${pathKey}`;
      const list = registry.get(operation.operationId) || [];
      list.push(entry);
      registry.set(operation.operationId, list);
    }
  }
}
