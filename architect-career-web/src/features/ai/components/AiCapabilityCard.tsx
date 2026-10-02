import { Box, Button, Stack, Typography } from '@mui/material';
import AutoAwesomeOutlinedIcon from '@mui/icons-material/AutoAwesomeOutlined';
import { useNavigate } from 'react-router-dom';
import type { AiCapabilityDefinition } from '../types/ai.types';
import { AiStatusChip } from './AiStatusChip';
import { AiCard } from './AiCard';

interface AiCapabilityCardProps {
  capability: AiCapabilityDefinition;
  available: boolean;
  href?: string;
}

export function AiCapabilityCard({ capability, available, href }: AiCapabilityCardProps) {
  const navigate = useNavigate();

  return (
    <AiCard
      title={capability.title}
      description={capability.description}
      actions={
        <AiStatusChip
          kind={
            capability.lifecycle === 'coming_soon'
              ? 'coming_soon'
              : available
                ? 'health'
                : 'disabled'
          }
          status={available ? 'AVAILABLE' : 'UNAVAILABLE'}
          label={
            capability.lifecycle === 'coming_soon'
              ? 'Coming soon'
              : available
                ? 'Ready'
                : 'Unavailable'
          }
        />
      }
    >
      <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary' }}>
          <AutoAwesomeOutlinedIcon fontSize="small" />
          <Typography variant="caption" sx={{ textTransform: 'capitalize' }}>
            {capability.domain}
          </Typography>
        </Box>
        {href ? (
          <Button size="small" onClick={() => navigate(href)}>
            Open
          </Button>
        ) : null}
      </Stack>
    </AiCard>
  );
}
