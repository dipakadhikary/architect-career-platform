import { describe, expect, it } from 'vitest';
import { createAcosConfiguration } from './configuration';
import { AcosApiClient } from './AcosApiClient';

describe('AcosApiClient', () => {
  it('wires domain services from configuration', () => {
    const client = new AcosApiClient({
      basePath: 'http://localhost:8080',
      accessToken: 'test-token',
      logger: { level: 'error' },
    });

    expect(client.knowledge).toBeDefined();
    expect(client.learning).toBeDefined();
    expect(client.career).toBeDefined();
    expect(client.portfolio).toBeDefined();
    expect(client.auth).toBeDefined();
    expect(client.dashboard).toBeDefined();
    expect(client.ai).toBeDefined();
  });

  it('normalizes trailing slash on base path', () => {
    const configuration = createAcosConfiguration({
      basePath: 'http://localhost:8080/',
    });
    expect(configuration.basePath).toBe('http://localhost:8080');
  });
});
