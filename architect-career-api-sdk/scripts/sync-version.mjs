#!/usr/bin/env node
/**
 * Synchronizes package versions with the OpenAPI info.version when it is a valid SemVer.
 *
 * When the backend reports a non-SemVer label (e.g. "development" before build-info is published),
 * the repository VERSION file remains the packaging source of truth and is aligned with the
 * Java Business Platform Maven version (currently 0.0.1).
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import yaml from 'js-yaml';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');

const SEMVER =
  /^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$/;

function readOpenApiVersion() {
  const yamlPath = path.join(root, 'openapi', 'acos-api.yaml');
  const jsonPath = path.join(root, 'openapi', 'acos-api.json');
  if (fs.existsSync(yamlPath)) {
    const doc = yaml.load(fs.readFileSync(yamlPath, 'utf8'));
    return doc?.info?.version;
  }
  if (fs.existsSync(jsonPath)) {
    const doc = JSON.parse(fs.readFileSync(jsonPath, 'utf8'));
    return doc?.info?.version;
  }
  return undefined;
}

function readFileVersion() {
  const versionPath = path.join(root, 'VERSION');
  return fs.readFileSync(versionPath, 'utf8').trim();
}

function writeJsonVersion(filePath, version) {
  if (!fs.existsSync(filePath)) {
    return;
  }
  const json = JSON.parse(fs.readFileSync(filePath, 'utf8'));
  json.version = version;
  fs.writeFileSync(filePath, `${JSON.stringify(json, null, 2)}\n`, 'utf8');
}

function patchOpenApiTools(version) {
  const filePath = path.join(root, 'openapitools.json');
  const json = JSON.parse(fs.readFileSync(filePath, 'utf8'));
  const generators = json['generator-cli']?.generators || {};
  for (const generator of Object.values(generators)) {
    const props = generator.additionalProperties || {};
    if ('npmVersion' in props) {
      props.npmVersion = version;
    }
    if ('artifactVersion' in props) {
      props.artifactVersion = version;
    }
  }
  fs.writeFileSync(filePath, `${JSON.stringify(json, null, 2)}\n`, 'utf8');
}

function patchPomVersions(version) {
  const pomFiles = [
    path.join(root, 'java', 'pom.xml'),
    path.join(root, 'java', 'custom', 'pom.xml'),
    path.join(root, 'java', 'generated', 'pom.xml'),
  ];

  for (const pomPath of pomFiles) {
    if (!fs.existsSync(pomPath)) {
      continue;
    }
    let pom = fs.readFileSync(pomPath, 'utf8');
    pom = pom.replace(
      /(<artifactId>architect-career-api-sdk(?:-parent|-custom)?<\/artifactId>\s*<version>)[^<]+(<\/version>)/g,
      `$1${version}$2`,
    );
    pom = pom.replace(
      /(<parent>[\s\S]*?<artifactId>architect-career-api-sdk-parent<\/artifactId>\s*<version>)[^<]+(<\/version>)/,
      `$1${version}$2`,
    );
    fs.writeFileSync(pomPath, pom, 'utf8');
  }
}

const openApiVersion = readOpenApiVersion();
const fileVersion = readFileVersion();

let sdkVersion = fileVersion;
if (openApiVersion && SEMVER.test(openApiVersion)) {
  sdkVersion = openApiVersion;
  fs.writeFileSync(path.join(root, 'VERSION'), `${sdkVersion}\n`, 'utf8');
  console.log(`SDK version aligned to OpenAPI SemVer: ${sdkVersion}`);
} else {
  console.log(
    `OpenAPI version "${openApiVersion ?? '(missing)'}" is not SemVer; keeping VERSION=${sdkVersion}`,
  );
}

writeJsonVersion(path.join(root, 'package.json'), sdkVersion);
writeJsonVersion(path.join(root, 'typescript', 'package.json'), sdkVersion);
patchOpenApiTools(sdkVersion);
patchPomVersions(sdkVersion);

console.log(`Synchronized packaging version: ${sdkVersion}`);
