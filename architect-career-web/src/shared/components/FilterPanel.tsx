import type { ReactNode } from 'react';
import { Box, Button, Collapse, Paper, Stack, Typography } from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { useState } from 'react';

interface FilterPanelProps {
  children: ReactNode;
  title?: string;
  onClear?: () => void;
  defaultOpen?: boolean;
}

export function FilterPanel({
  children,
  title = 'Filters',
  onClear,
  defaultOpen = true,
}: FilterPanelProps) {
  const [open, setOpen] = useState(defaultOpen);

  return (
    <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
      <Stack
        direction="row"
        alignItems="center"
        justifyContent="space-between"
        sx={{ mb: open ? 2 : 0 }}
      >
        <Button
          startIcon={<FilterListIcon />}
          endIcon={open ? <ExpandLessIcon /> : <ExpandMoreIcon />}
          onClick={() => setOpen((value) => !value)}
          color="inherit"
        >
          {title}
        </Button>
        {onClear ? (
          <Button size="small" onClick={onClear}>
            Clear
          </Button>
        ) : null}
      </Stack>
      <Collapse in={open}>
        <Box
          sx={{
            display: 'grid',
            gap: 2,
            gridTemplateColumns: {
              xs: '1fr',
              sm: 'repeat(2, minmax(0, 1fr))',
              md: 'repeat(3, minmax(0, 1fr))',
              lg: 'repeat(4, minmax(0, 1fr))',
            },
          }}
        >
          {children}
        </Box>
        {!children ? (
          <Typography variant="body2" color="text.secondary">
            No filters available
          </Typography>
        ) : null}
      </Collapse>
    </Paper>
  );
}
