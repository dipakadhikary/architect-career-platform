import type { ReactNode } from 'react';
import { Box, Button, Chip, Paper, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import type { TutorialSearchResult } from '../types/tutorial.types';

function SafeSnippet({ snippet }: { snippet: string }) {
  const parts = snippet.split(/(<\/?mark>)/i);
  const nodes: ReactNode[] = [];
  let highlight = false;
  for (const part of parts) {
    if (/^<mark>$/i.test(part)) {
      highlight = true;
      continue;
    }
    if (/^<\/mark>$/i.test(part)) {
      highlight = false;
      continue;
    }
    if (!part) continue;
    nodes.push(
      highlight ? (
        <Box
          key={`${nodes.length}-${part}`}
          component="mark"
          sx={{ bgcolor: 'warning.light', px: 0.25, borderRadius: 0.5 }}
        >
          {part}
        </Box>
      ) : (
        <Box key={`${nodes.length}-${part}`} component="span">
          {part}
        </Box>
      ),
    );
  }
  return <>{nodes}</>;
}

function contentRoute(result: TutorialSearchResult): string {
  const suffix = result.contentType === 'Concept' ? 'concept' : 'questions';
  return `/tutorials/${result.path}/${suffix}`;
}

export function TutorialSearchResults({ results }: { results: TutorialSearchResult[] }) {
  return (
    <Box
      sx={{
        display: 'grid',
        gap: 2,
        gridTemplateColumns: {
          xs: '1fr',
          sm: 'repeat(2, minmax(0, 1fr))',
          lg: 'repeat(3, minmax(0, 1fr))',
        },
      }}
    >
      {results.map((result) => (
        <Paper key={`${result.topicId}-${result.contentType}-${result.rank}`} variant="outlined" sx={{ p: 2 }}>
          <Stack spacing={1.25} height="100%">
            <Typography variant="h6" component="h3">
              {result.title}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Tutorials
              {result.breadcrumb.map((item) => ` / ${item.title}`).join('')}
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ flex: 1 }}>
              “
              <SafeSnippet snippet={result.snippet} />
              ”
            </Typography>
            <Chip size="small" label={`Content Type: ${result.contentType}`} sx={{ alignSelf: 'flex-start' }} />
            <Button
              component={RouterLink}
              to={contentRoute(result)}
              variant="contained"
              size="small"
              sx={{ alignSelf: 'flex-start' }}
            >
              Open Tutorial
            </Button>
          </Stack>
        </Paper>
      ))}
    </Box>
  );
}
