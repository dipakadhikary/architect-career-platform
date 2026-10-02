import { Box, IconButton, List, ListItemButton, ListItemText, Typography } from '@mui/material';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import type { ChatMessage } from '../types/ai.types';

interface PromptHistoryProps {
  messages: ChatMessage[];
  onSelect: (content: string) => void;
  onClear: () => void;
}

export function PromptHistory({ messages, onSelect, onClear }: PromptHistoryProps) {
  const prompts = messages.filter((message) => message.role === 'user');

  return (
    <Box
      sx={{
        border: 1,
        borderColor: 'divider',
        borderRadius: 2,
        p: 1.5,
        height: '100%',
        bgcolor: 'background.paper',
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
        <Typography variant="subtitle2">Prompt history</Typography>
        <IconButton
          size="small"
          onClick={onClear}
          aria-label="Clear conversation"
          disabled={messages.length === 0}
        >
          <DeleteOutlineIcon fontSize="small" />
        </IconButton>
      </Box>
      {prompts.length === 0 ? (
        <Typography variant="body2" color="text.secondary">
          Your prompts will appear here.
        </Typography>
      ) : (
        <List dense disablePadding sx={{ maxHeight: 320, overflow: 'auto' }}>
          {prompts.map((prompt) => (
            <ListItemButton key={prompt.id} onClick={() => onSelect(prompt.content)}>
              <ListItemText
                primary={prompt.content}
                primaryTypographyProps={{ noWrap: true, variant: 'body2' }}
                secondary={new Date(prompt.createdAt).toLocaleString()}
              />
            </ListItemButton>
          ))}
        </List>
      )}
    </Box>
  );
}
