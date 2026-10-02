#!/usr/bin/env node
/**
 * Produces a convenience copy of the AsyncAPI root for generators and docs.
 * Domain modules remain the editable source of truth.
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const source = path.join(root, 'asyncapi', 'ai-platform-events-v1.yaml');
const outDir = path.join(root, 'dist', 'asyncapi');
const outFile = path.join(outDir, 'ai-platform-events-v1.yaml');

if (!fs.existsSync(source)) {
  console.error('Missing asyncapi/ai-platform-events-v1.yaml');
  process.exit(1);
}

fs.mkdirSync(outDir, { recursive: true });
fs.copyFileSync(source, outFile);

// Also copy modular tree for offline consumers.
const modules = ['common', 'knowledge', 'learning', 'career', 'portfolio'];
for (const moduleName of modules) {
  const from = path.join(root, 'asyncapi', moduleName);
  const to = path.join(outDir, moduleName);
  fs.cpSync(from, to, { recursive: true });
}

console.log(`Prepared AsyncAPI artifacts under ${path.relative(root, outDir)}`);
