import { Chip } from '@mui/material';
import { formatEnumLabel } from '@/shared/utils/label';

type ChipColor = 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';

const STATUS_COLORS: Record<string, ChipColor> = {
  DRAFT: 'default',
  ACTIVE: 'success',
  COMPLETED: 'success',
  ARCHIVED: 'default',
  PUBLISHED: 'success',
  NOT_STARTED: 'default',
  IN_PROGRESS: 'info',
  APPLIED: 'info',
  SCREENING: 'info',
  TECHNICAL_INTERVIEW: 'warning',
  MANAGER_INTERVIEW: 'warning',
  HR_INTERVIEW: 'warning',
  OFFER: 'secondary',
  ACCEPTED: 'success',
  DECLINED: 'error',
  REJECTED: 'error',
  WITHDRAWN: 'default',
  PENDING: 'warning',
  EXPIRED: 'default',
  SCHEDULED: 'info',
  CANCELLED: 'default',
  NO_SHOW: 'error',
  RESCHEDULED: 'warning',
  INACTIVE: 'default',
  DO_NOT_CONTACT: 'error',
  BEGINNER: 'default',
  INTERMEDIATE: 'info',
  ADVANCED: 'warning',
  EXPERT: 'success',
};

interface StatusChipProps {
  status: string;
  size?: 'small' | 'medium';
}

export function StatusChip({ status, size = 'small' }: StatusChipProps) {
  return (
    <Chip
      label={formatEnumLabel(status)}
      color={STATUS_COLORS[status] ?? 'default'}
      size={size}
      variant="outlined"
    />
  );
}
