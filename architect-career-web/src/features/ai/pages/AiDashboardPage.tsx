import AutoAwesomeOutlinedIcon from '@mui/icons-material/AutoAwesomeOutlined';
import SpeedOutlinedIcon from '@mui/icons-material/SpeedOutlined';
import ToggleOnOutlinedIcon from '@mui/icons-material/PowerSettingsNew';
import { Box, Grid, Paper, Stack, Typography } from '@mui/material';
import { PageHeader, StatisticsCard } from '@/shared/components';
import { AiCapabilityCard, AiStatusChip, AiUnavailableBanner } from '@/features/ai/components';
import { useAiAvailability } from '@/features/ai/hooks/useAiHealth';
import { getCapabilitiesByDomain } from '@/features/ai/services/aiCapabilities';
import type { AiDomain } from '@/features/ai/types/ai.types';

const AI_DOMAINS: AiDomain[] = ['knowledge', 'learning', 'career', 'portfolio', 'chat'];

const DOMAIN_LABELS: Record<AiDomain, string> = {
  knowledge: 'Knowledge AI',
  learning: 'Learning AI',
  career: 'Career AI',
  portfolio: 'Portfolio AI',
  chat: 'Ask ACOS AI',
};

const DOMAIN_PATHS: Record<AiDomain, string> = {
  knowledge: '/ai/knowledge',
  learning: '/ai/learning',
  career: '/ai/career',
  portfolio: '/ai/portfolio',
  chat: '/ai/ask',
};

export function AiDashboardPage() {
  const {
    toggleEnabled,
    health,
    latencyMs,
    operational,
    canInvoke,
    unavailableReason,
    isLoading,
    refetch,
  } = useAiAvailability();

  const featureStatus = !toggleEnabled
    ? 'Disabled'
    : isLoading
      ? 'Checking…'
      : operational
        ? 'Operational'
        : 'Unavailable';

  return (
    <Box>
      <PageHeader
        title="AI Platform"
        description="Integrated AI capabilities across knowledge, learning, career, portfolio, and chat."
      />

      {!canInvoke && unavailableReason ? (
        <AiUnavailableBanner message={unavailableReason} onRetry={() => void refetch()} />
      ) : null}

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <StatisticsCard
            label="Feature toggle"
            value={toggleEnabled ? 'Enabled' : 'Disabled'}
            helperText="VITE_AI_PLATFORM_ENABLED"
            icon={<ToggleOnOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
            <Typography variant="overline" color="text.secondary">
              Health status
            </Typography>
            <Stack direction="row" alignItems="center" spacing={1} sx={{ mt: 1 }}>
              {toggleEnabled ? (
                <AiStatusChip
                  kind="health"
                  status={health?.status ?? 'UNAVAILABLE'}
                  label={isLoading ? 'Checking…' : health ? undefined : 'Unknown'}
                />
              ) : (
                <AiStatusChip kind="disabled" label="Disabled" />
              )}
            </Stack>
            {health?.message ? (
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                {health.message}
              </Typography>
            ) : null}
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <StatisticsCard
            label="Last latency"
            value={latencyMs != null ? `${latencyMs} ms` : '—'}
            helperText="Health check response time"
            icon={<SpeedOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <StatisticsCard
            label="Platform enabled"
            value={health?.enabled ? 'Yes' : health ? 'No' : '—'}
            helperText="Reported by integration layer"
            icon={<AutoAwesomeOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <StatisticsCard
            label="Feature status"
            value={featureStatus}
            helperText={
              health?.checkedAt
                ? `Checked ${new Date(health.checkedAt).toLocaleString()}`
                : 'Awaiting health check'
            }
          />
        </Grid>
      </Grid>

      <Paper variant="outlined" sx={{ p: 2.5, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          Current model
        </Typography>
        <Typography variant="body1" sx={{ mb: 0.5 }}>
          —
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Available when platform reports it.
        </Typography>
      </Paper>

      <Typography variant="h6" sx={{ mb: 2 }}>
        AI domains
      </Typography>
      <Grid container spacing={2}>
        {AI_DOMAINS.map((domain) => {
          const capabilities = getCapabilitiesByDomain(domain);
          const representative = capabilities[0];
          if (!representative) return null;

          return (
            <Grid key={domain} size={{ xs: 12, sm: 6, lg: 4 }}>
              <AiCapabilityCard
                capability={{
                  ...representative,
                  title: DOMAIN_LABELS[domain],
                  description: `${capabilities.length} capabilities · ${representative.description}`,
                }}
                available={canInvoke}
                href={DOMAIN_PATHS[domain]}
              />
            </Grid>
          );
        })}
      </Grid>
    </Box>
  );
}
