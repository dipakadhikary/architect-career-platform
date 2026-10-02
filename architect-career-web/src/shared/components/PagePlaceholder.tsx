import { Box, Typography } from '@mui/material';

interface PagePlaceholderProps {
  title: string;
  description?: string;
}

/**
 * Shell placeholder for routes that have no business implementation yet.
 */
export function PagePlaceholder({ title, description }: PagePlaceholderProps) {
  return (
    <Box
      sx={{
        py: { xs: 4, md: 6 },
        px: { xs: 1, md: 2 },
        maxWidth: 720,
      }}
    >
      <Typography variant="overline" color="secondary.main">
        ACOS Foundation
      </Typography>
      <Typography variant="h4" component="h1" sx={{ mt: 1, mb: 1.5 }}>
        {title}
      </Typography>
      {description ? (
        <Typography variant="body1" color="text.secondary">
          {description}
        </Typography>
      ) : null}
    </Box>
  );
}
