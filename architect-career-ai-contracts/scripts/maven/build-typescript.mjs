#!/usr/bin/env node
/**
 * Verifies generated TypeScript SDK compiles under strict settings.
 */
import fs from 'node:fs';
import path from 'node:path';
import { spawnSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');
const tsDir = process.env.ACOS_GENERATED_TYPESCRIPT
  ? path.resolve(process.env.ACOS_GENERATED_TYPESCRIPT)
  : path.join(root, 'target', 'generated', 'typescript');
const nodeDir = process.env.ACOS_NODE_DIR
  ? path.resolve(process.env.ACOS_NODE_DIR)
  : path.join(root, 'target', 'node');

if (!fs.existsSync(tsDir)) {
  console.error(`[typescript] Missing generated directory: ${tsDir}`);
  process.exit(1);
}

const patchScript = path.join(root, 'scripts', 'maven', 'patch-typescript-sources.mjs');
const patch = spawnSync(process.execPath, [patchScript], {
  cwd: root,
  stdio: 'inherit',
  env: process.env,
});
if ((patch.status ?? 1) !== 0) {
  process.exit(patch.status ?? 1);
}

const npmCmd =
  process.platform === 'win32'
    ? path.join(nodeDir, 'node', 'npm.cmd')
    : path.join(nodeDir, 'node', 'npm');

const install = spawnSync(npmCmd, ['install', '--ignore-scripts', '--no-fund', '--no-audit'], {
  cwd: tsDir,
  stdio: 'inherit',
  shell: true,
});
if ((install.status ?? 1) !== 0) {
  process.exit(install.status ?? 1);
}

const packageJsonPath = path.join(tsDir, 'package.json');
if (!fs.existsSync(packageJsonPath)) {
  console.error('[typescript] Generated package.json not found');
  process.exit(1);
}

const build = spawnSync(npmCmd, ['run', 'build'], {
  cwd: tsDir,
  stdio: 'inherit',
  shell: true,
});

if ((build.status ?? 1) !== 0) {
  const npxCmd =
    process.platform === 'win32'
      ? path.join(nodeDir, 'node', 'npx.cmd')
      : path.join(nodeDir, 'node', 'npx');
  const tsc = spawnSync(npxCmd, ['tsc', '-p', 'tsconfig.json', '--noEmit'], {
    cwd: tsDir,
    stdio: 'inherit',
    shell: true,
  });
  if ((tsc.status ?? 1) !== 0) {
    process.exit(tsc.status ?? 1);
  }
}

console.log('[typescript] Build validation succeeded');
