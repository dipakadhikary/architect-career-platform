import { Box, IconButton, Paper, Stack, Tooltip, Typography } from '@mui/material';
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
            <MarkdownViewer content={message.content} withSyntaxHighlight />
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
