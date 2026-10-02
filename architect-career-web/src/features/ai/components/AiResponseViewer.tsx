import { Box, Button, Stack, Typography } from '@mui/material';
import { MarkdownViewer } from '@/shared/components';

interface AiResponseViewerProps {
  content: string;
  format?: string;
  onCopy?: () => void;
  onDownload?: () => void;
}

export function AiResponseViewer({
  content,
  format = 'markdown',
  onCopy,
  onDownload,
}: AiResponseViewerProps) {
  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
        <Typography variant="caption" color="text.secondary" sx={{ textTransform: 'uppercase' }}>
          {format}
        </Typography>
        <Stack direction="row" spacing={1}>
          {onCopy ? (
            <Button size="small" onClick={onCopy}>
              Copy
            </Button>
          ) : null}
          {onDownload ? (
            <Button size="small" onClick={onDownload}>
              Download
            </Button>
          ) : null}
        </Stack>
      </Stack>
      <MarkdownViewer content={content} withSyntaxHighlight />
    </Box>
  );
}
