#!/usr/bin/env node
/**
 * Generates language-neutral JSON Schema snapshots of AsyncAPI message payloads
 * for Java / Python / TypeScript event model consumption.
 *
 * Full AsyncAPI multi-language codegen varies by generator maturity; this repository
 * publishes stable payload schemas that consumers can generate models from.
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import yaml from 'js-yaml';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const outRoot = path.join(root, 'generated', 'asyncapi');

const sources = [
  ['common', 'asyncapi/common/common-events.yaml'],
  ['knowledge', 'asyncapi/knowledge/knowledge-events.yaml'],
  ['learning', 'asyncapi/learning/learning-events.yaml'],
  ['career', 'asyncapi/career/career-events.yaml'],
  ['portfolio', 'asyncapi/portfolio/portfolio-events.yaml'],
];

fs.rmSync(outRoot, { recursive: true, force: true });
fs.mkdirSync(outRoot, { recursive: true });

const catalog = [];

for (const [domain, relative] of sources) {
  const doc = yaml.load(fs.readFileSync(path.join(root, relative), 'utf8'));
  const schemas = doc?.components?.schemas || {};
  const messages = doc?.components?.messages || {};
  const domainDir = path.join(outRoot, domain, 'schemas');
  fs.mkdirSync(domainDir, { recursive: true });

  for (const [name, schema] of Object.entries(schemas)) {
    const filePath = path.join(domainDir, `${name}.schema.json`);
    fs.writeFileSync(
      filePath,
      `${JSON.stringify(
        {
          $schema: 'https://json-schema.org/draft/2020-12/schema',
          $id: `https://acos.local/schemas/ai/events/${domain}/${name}.json`,
          title: name,
          ...schema,
        },
        null,
        2,
      )}\n`,
      'utf8',
    );
    catalog.push({
      domain,
      kind: 'schema',
      name,
      path: path.relative(root, filePath).replaceAll('\\', '/'),
    });
  }

  const messageDir = path.join(outRoot, domain, 'messages');
  fs.mkdirSync(messageDir, { recursive: true });
  for (const [name, message] of Object.entries(messages)) {
    const filePath = path.join(messageDir, `${name}.json`);
    fs.writeFileSync(filePath, `${JSON.stringify(message, null, 2)}\n`, 'utf8');
    catalog.push({
      domain,
      kind: 'message',
      name,
      path: path.relative(root, filePath).replaceAll('\\', '/'),
    });
  }
}

fs.writeFileSync(
  path.join(outRoot, 'catalog.json'),
  `${JSON.stringify({ generatedAt: new Date().toISOString(), artifacts: catalog }, null, 2)}\n`,
  'utf8',
);

fs.writeFileSync(
  path.join(outRoot, 'README.md'),
  [
    '# Generated AsyncAPI artifacts',
    '',
    'JSON Schema snapshots of event payloads and message definitions.',
    'Use these as inputs for language-specific model generation in consuming repositories.',
    '',
    'Do not edit manually. Regenerate with `npm run generate:events`.',
    '',
  ].join('\n'),
  'utf8',
);

console.log(`Generated ${catalog.length} AsyncAPI artifacts under generated/asyncapi`);
