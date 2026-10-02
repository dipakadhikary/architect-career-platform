#!/usr/bin/env node
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import SwaggerParser from '@apidevtools/swagger-parser';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');

const specs = [
  'openapi/ai-platform-v1.yaml',
  'openapi/knowledge/knowledge-api.yaml',
  'openapi/learning/learning-api.yaml',
  'openapi/career/career-api.yaml',
  'openapi/portfolio/portfolio-api.yaml',
  'openapi/chat/chat-api.yaml',
];

let failed = false;

for (const relative of specs) {
  const filePath = path.join(root, relative);
  process.stdout.write(`Validating ${relative} ... `);
  try {
    const api = await SwaggerParser.validate(filePath);
    const pathCount = Object.keys(api.paths || {}).length;
    console.log(`ok (${pathCount} paths)`);
  } catch (error) {
    failed = true;
    console.log('failed');
    console.error(error.message);
  }
}

process.exit(failed ? 1 : 0);
