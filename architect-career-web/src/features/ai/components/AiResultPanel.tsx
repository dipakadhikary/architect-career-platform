import type { ReactNode } from 'react';
import { Alert, Box, Button, IconButton, Stack, Tooltip, Typography } from '@mui/material';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import DownloadOutlinedIcon from '@mui/icons-material/DownloadOutlined';
import RefreshOutlinedIcon from '@mui/icons-material/RefreshOutlined';
import { MarkdownViewer } from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { copyTextToClipboard, downloadTextFile } from '../services/chatSession.service';
import { AiLoadingIndicator } from './AiLoadingIndicator';

interface AiResultPanelProps {
  title?: string;
  content?: string | null;
  loading?: boolean;
  error?: string | null;
  emptyMessage?: string;
  onRetry?: () => void;
  onRegenerate?: () => void;
  downloadFilename?: string;
  children?: ReactNode;
}

export function AiResultPanel({
  title = 'AI result',
  content,
  loading = false,
  error = null,
  emptyMessage = 'Run a capability to see results here.',
  onRetry,
  onRegenerate,
  downloadFilename = 'acos-ai-result.md',
  children,
}: AiResultPanelProps) {
  const { success, error: notifyError } = useNotification();

  const handleCopy = async () => {
    if (!content) return;
    try {
      await copyTextToClipboard(content);
      success('Copied to clipboard');
    } catch {
      notifyError('Unable to copy');
    }
  };

  const handleDownload = () => {
    if (!content) return;
    downloadTextFile(downloadFilename, content);
    success('Download started');
  };

  return (
    <Box
      sx={{
        border: 1,
        borderColor: 'divider',
        borderRadius: 2,
        p: 2,
        bgcolor: 'background.paper',
        minHeight: 180,
      }}
    >
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1.5 }}>
        <Typography variant="subtitle1" fontWeight={600}>
          {title}
        </Typography>
        <Stack direction="row" spacing={0.5}>
          {onRegenerate ? (
            <Tooltip title="Regenerate">
              <span>
                <IconButton
                  size="small"
                  onClick={onRegenerate}
                  disabled={loading}
                  aria-label="Regenerate"
                >
                  <RefreshOutlinedIcon fontSize="small" />
                </IconButton>
              </span>
            </Tooltip>
          ) : null}
          <Tooltip title="Copy">
            <span>
              <IconButton
                size="small"
                onClick={handleCopy}
                disabled={!content}
                aria-label="Copy result"
              >
                <ContentCopyIcon fontSize="small" />
              </IconButton>
            </span>
          </Tooltip>
          <Tooltip title="Download">
            <span>
              <IconButton
                size="small"
                onClick={handleDownload}
                disabled={!content}
                aria-label="Download result"
              >
                <DownloadOutlinedIcon fontSize="small" />
              </IconButton>
            </span>
          </Tooltip>
        </Stack>
      </Stack>

      {loading ? <AiLoadingIndicator /> : null}
      {!loading && error ? (
        <Alert
          severity="info"
          action={
            onRetry ? (
              <Button color="inherit" size="small" onClick={onRetry}>
                Retry
              </Button>
            ) : undefined
          }
        >
          {error}
        </Alert>
      ) : null}
      {!loading && !error && content ? (
        <MarkdownViewer content={content} withSyntaxHighlight />
      ) : null}
      {!loading && !error && !content && children ? children : null}
      {!loading && !error && !content && !children ? (
        <Typography variant="body2" color="text.secondary">
          {emptyMessage}
        </Typography>
      ) : null}
    </Box>
  );
}
