import { Chip } from '@mui/material';
import type { AiPlatformHealthStatus } from '../types/ai.types';
import { describeHealthStatus } from '../services/aiAvailability';

type ChipColor = 'default' | 'success' | 'warning' | 'error' | 'info';

const COLORS: Record<AiPlatformHealthStatus | 'DISABLED' | 'PLANNED', ChipColor> = {
  AVAILABLE: 'success',
  DEGRADED: 'warning',
  UNAVAILABLE: 'error',
  DISABLED: 'default',
  PLANNED: 'info',
};

interface AiStatusChipProps {
  status?: AiPlatformHealthStatus;
  label?: string;
  kind?: 'health' | 'disabled' | 'planned' | 'coming_soon';
  size?: 'small' | 'medium';
}

export function AiStatusChip({
  status,
  label,
  kind = 'health',
  size = 'small',
}: AiStatusChipProps) {
  if (kind === 'disabled') {
    return (
      <Chip size={size} color={COLORS.DISABLED} label={label ?? 'Disabled'} variant="outlined" />
    );
  }
  if (kind === 'planned' || kind === 'coming_soon') {
    return (
      <Chip
        size={size}
        color={COLORS.PLANNED}
        label={label ?? (kind === 'coming_soon' ? 'Coming soon' : 'Planned')}
        variant="outlined"
      />
    );
  }

  const resolved = status ?? 'UNAVAILABLE';
  return (
    <Chip
      size={size}
      color={COLORS[resolved]}
      label={label ?? describeHealthStatus(resolved)}
      variant="outlined"
    />
  );
}
