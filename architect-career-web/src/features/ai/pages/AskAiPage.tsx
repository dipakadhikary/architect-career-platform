import { useState } from 'react';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  List,
  ListItemButton,
  ListItemText,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import DriveFileRenameOutlineIcon from '@mui/icons-material/DriveFileRenameOutline';
import { ConfirmationDialog, PageHeader } from '@/shared/components';
import { ChatInput, ChatMessage } from '@/features/ai/components';
import { useAskAi } from '@/features/ai/hooks/useAskAi';
import { useAiAvailability } from '@/features/ai/hooks/useAiHealth';

export function AskAiPage() {
  const { canInvoke } = useAiAvailability();
  const chat = useAskAi();
  const [renameTarget, setRenameTarget] = useState<string | null>(null);
  const [renameTitle, setRenameTitle] = useState('');
  const [deleteTarget, setDeleteTarget] = useState<string | null>(null);

  const openRename = (id: string, title: string) => {
    setRenameTarget(id);
    setRenameTitle(title);
  };

  return (
    <Stack spacing={2}>
      <PageHeader
        title="Ask ACOS AI"
        description="Ask a technical question. When ACOS knowledge is relevant, the answer includes sources that open the original note or tutorial."
      />
      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems="stretch">
        <Box
          component="nav"
          aria-label="Conversations"
          sx={{ width: { xs: '100%', md: 280 }, flexShrink: 0 }}
        >
          <Button
            fullWidth
            variant="outlined"
            startIcon={<AddIcon />}
            disabled={chat.creating || chat.asking}
            onClick={() => void chat.startNew()}
          >
            New conversation
          </Button>
          {chat.listError ? (
            <Typography variant="body2" color="error" sx={{ mt: 1 }}>
              {chat.listError}
            </Typography>
          ) : null}
          <List dense>
            {chat.conversations.map((conversation) => (
              <ListItemButton
                key={conversation.id}
                selected={conversation.id === chat.activeId}
                onClick={() => void chat.openConversation(conversation.id)}
              >
                <ListItemText
                  primary={conversation.title}
                  secondary={new Date(conversation.updatedAt).toLocaleString()}
                />
                <IconButton
                  size="small"
                  aria-label={`Rename ${conversation.title}`}
                  onClick={(event) => {
                    event.stopPropagation();
                    openRename(conversation.id, conversation.title);
                  }}
                >
                  <DriveFileRenameOutlineIcon fontSize="small" />
                </IconButton>
                <IconButton
                  size="small"
                  aria-label={`Delete ${conversation.title}`}
                  onClick={(event) => {
                    event.stopPropagation();
                    setDeleteTarget(conversation.id);
                  }}
                >
                  <DeleteOutlineIcon fontSize="small" />
                </IconButton>
              </ListItemButton>
            ))}
          </List>
        </Box>
        <Stack spacing={2} sx={{ flex: 1, minWidth: 0 }}>
          {chat.loadingHistory ? (
            <Typography variant="body2" color="text.secondary">
              Loading conversation…
            </Typography>
          ) : null}
          <Stack>
            {chat.messages.map((message) => (
              <ChatMessage key={message.id} message={message} assistantLabel="ACOS AI" />
            ))}
          </Stack>
          {chat.failed ? (
            <Button variant="outlined" onClick={() => void chat.retry()} disabled={chat.asking}>
              Retry
            </Button>
          ) : null}
          <ChatInput
            submitLabel="Ask"
            placeholder="Explain the Factory Design Pattern"
            loading={chat.asking}
            disabled={!canInvoke || chat.creating}
            onSend={(question) => void chat.ask(question)}
          />
        </Stack>
      </Stack>
      <Dialog open={renameTarget !== null} onClose={() => setRenameTarget(null)} fullWidth>
        <DialogTitle>Rename conversation</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            fullWidth
            margin="dense"
            label="Title"
            value={renameTitle}
            onChange={(event) => setRenameTitle(event.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRenameTarget(null)}>Cancel</Button>
          <Button
            disabled={chat.renaming || !renameTitle.trim()}
            onClick={() => {
              if (!renameTarget) return;
              void chat.rename(renameTarget, renameTitle).then(() => setRenameTarget(null));
            }}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
      <ConfirmationDialog
        open={deleteTarget !== null}
        title="Delete conversation"
        description="This permanently removes the conversation and its messages."
        confirmLabel="Delete"
        danger
        loading={chat.deleting}
        onCancel={() => setDeleteTarget(null)}
        onConfirm={() => {
          if (!deleteTarget) return;
          void chat.remove(deleteTarget).then(() => setDeleteTarget(null));
        }}
      />
    </Stack>
  );
}
