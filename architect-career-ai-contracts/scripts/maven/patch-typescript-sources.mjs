#!/usr/bin/env node
/**
 * Patches known OpenAPI Generator TypeScript defects for strict builds.
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');
const tsDir =
  process.env.ACOS_GENERATED_TYPESCRIPT ||
  path.join(root, 'target', 'generated', 'typescript');

if (!fs.existsSync(tsDir)) {
  console.log('[typescript-patch] No TypeScript sources to patch');
  process.exit(0);
}

let patched = 0;

for (const file of walk(tsDir)) {
  if (!file.endsWith('.ts') || file.includes(`${path.sep}node_modules${path.sep}`)) {
    continue;
  }
  const original = fs.readFileSync(file, 'utf8');
  let next = original;

  // OpenAPI 3.1 null schemas are incorrectly emitted as the type name `Null`
  next = next.replace(/\bNull\b/g, 'null');

  if (path.basename(file) === 'common.ts') {
    // Strip prior annotations; declaration emit is disabled to avoid axios unique-symbol TS2527.
    next = next.replace(
      /export const createRequestFunction = function \(axiosArgs: RequestArgs, globalAxios: AxiosInstance, BASE_PATH: string, configuration\?: Configuration\)(?::[^{]+)? \{/,
      'export const createRequestFunction = function (axiosArgs: RequestArgs, globalAxios: AxiosInstance, BASE_PATH: string, configuration?: Configuration) {',
    );
  }

  if (next !== original) {
    fs.writeFileSync(file, next, 'utf8');
    patched += 1;
    console.log(`[typescript-patch] Fixed ${path.relative(tsDir, file)}`);
  }
}

enforceTsconfig(path.join(tsDir, 'tsconfig.json'));
enforceTsconfig(path.join(tsDir, 'tsconfig.esm.json'));

console.log(`[typescript-patch] Patched ${patched} file(s)`);

function enforceTsconfig(tsconfigPath) {
  if (!fs.existsSync(tsconfigPath)) {
    return;
  }
  const tsconfig = JSON.parse(fs.readFileSync(tsconfigPath, 'utf8'));
  tsconfig.compilerOptions = {
    ...tsconfig.compilerOptions,
    strict: true,
    skipLibCheck: true,
    esModuleInterop: true,
    forceConsistentCasingInFileNames: true,
    // Avoid TS2527 from axios brand symbols when emitting .d.ts from generated common.ts
    declaration: false,
  };
  fs.writeFileSync(tsconfigPath, `${JSON.stringify(tsconfig, null, 2)}\n`, 'utf8');
  console.log(`[typescript-patch] Enforced strict options in ${path.basename(tsconfigPath)}`);
}

function* walk(dir) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      if (entry.name === 'node_modules' || entry.name === 'dist') {
        continue;
      }
      yield* walk(full);
    } else {
      yield full;
    }
  }
}
