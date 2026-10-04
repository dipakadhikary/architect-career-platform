import { Box, Button, IconButton, Paper, Stack, Tooltip, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import PersonOutlineIcon from '@mui/icons-material/PersonOutline';
import SmartToyOutlinedIcon from '@mui/icons-material/SmartToyOutlined';
import { MarkdownViewer } from '@/shared/components';
import type { ChatMessage as ChatMessageModel } from '../types/ai.types';
import { AiLoadingIndicator } from './AiLoadingIndicator';
import { copyTextToClipboard } from '../services/chatSession.service';
import { useNotification } from '@/shared/hooks/useNotification';

interface ChatMessageProps {
  message: ChatMessageModel;
  assistantLabel?: string;
}

export function labelFor(contentType: string): string {
  if (contentType === 'CONCEPT') return 'Concept';
  if (contentType === 'QUESTIONS_ANSWERS') return 'Questions & Answers';
  if (contentType === 'NOTE') return 'Note';
  return contentType;
}

export function ChatMessage({ message, assistantLabel = 'Assistant' }: ChatMessageProps) {
  const { success, error } = useNotification();
  const isUser = message.role === 'user';

  const handleCopy = async () => {
    try {
      await copyTextToClipboard(message.content);
      success('Copied');
    } catch {
      error('Unable to copy');
    }
  };

  return (
    <Stack
      direction="row"
      spacing={1.5}
      justifyContent={isUser ? 'flex-end' : 'flex-start'}
      sx={{ mb: 2 }}
    >
      {!isUser ? <SmartToyOutlinedIcon color="primary" sx={{ mt: 1 }} /> : null}
      <Paper
        variant="outlined"
        sx={{
          p: 1.75,
          maxWidth: { xs: '92%', md: '75%' },
          bgcolor: isUser ? 'action.selected' : 'background.paper',
        }}
      >
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 0.5 }}>
          <Typography variant="caption" color="text.secondary">
            {isUser ? 'You' : assistantLabel}
          </Typography>
          {message.status === 'complete' && message.content ? (
            <Tooltip title="Copy message">
              <IconButton size="small" onClick={handleCopy} aria-label="Copy message">
                <ContentCopyIcon fontSize="inherit" />
              </IconButton>
            </Tooltip>
          ) : null}
        </Stack>
        {message.status === 'pending' || message.status === 'streaming' ? (
          <AiLoadingIndicator compact label={message.streaming ? 'Thinking…' : 'Sending…'} />
        ) : null}
        {message.status === 'error' ? (
          <Typography variant="body2" color="error">
            {message.content || 'Something went wrong.'}
          </Typography>
        ) : null}
        {message.status === 'complete' ? (
          isUser ? (
            <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
              {message.content}
            </Typography>
          ) : (
            <Stack spacing={1.5}>
              {message.grounded ? (
                <Typography variant="caption" color="text.secondary">
                  Based on your ACOS knowledge
                </Typography>
              ) : null}
              <MarkdownViewer content={message.content} withSyntaxHighlight />
              {message.sources && message.sources.length > 0 ? (
                <Stack spacing={1}>
                  <Typography variant="caption" color="text.secondary">
                    Sources
                  </Typography>
                  {message.sources.map((source) => (
                    <Paper key={source.chunkId || source.contentId} variant="outlined" sx={{ p: 1.25 }}>
                      <Typography variant="subtitle2">{source.title}</Typography>
                      <Typography variant="caption" color="text.secondary" display="block">
                        {[source.path, labelFor(source.contentType), source.section]
                          .filter(Boolean)
                          .join(' / ')}
                      </Typography>
                      {source.url.startsWith('/') ? (
                        <Button component={RouterLink} to={source.url} size="small" sx={{ mt: 0.5, px: 0 }}>
                          Open source
                        </Button>
                      ) : null}
                    </Paper>
                  ))}
                </Stack>
              ) : null}
            </Stack>
          )
        ) : null}
        <Box sx={{ mt: 0.5 }}>
          <Typography variant="caption" color="text.disabled">
            {new Date(message.createdAt).toLocaleTimeString()}
          </Typography>
        </Box>
      </Paper>
      {isUser ? <PersonOutlineIcon color="action" sx={{ mt: 1 }} /> : null}
    </Stack>
  );
}
