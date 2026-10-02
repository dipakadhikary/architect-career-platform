import { useEffect, useState, type KeyboardEvent } from 'react';
import { Box, Button, Stack, TextField } from '@mui/material';
import SendOutlinedIcon from '@mui/icons-material/SendOutlined';
import { moduleConfig } from '@/app/config/module.config';

interface ChatInputProps {
  disabled?: boolean;
  loading?: boolean;
  placeholder?: string;
  prefill?: string;
  onSend: (message: string) => void;
}

export function ChatInput({
  disabled = false,
  loading = false,
  placeholder = 'Ask ACOS AI…',
  prefill,
  onSend,
}: ChatInputProps) {
  const [value, setValue] = useState('');

  useEffect(() => {
    if (prefill) {
      setValue(prefill);
    }
  }, [prefill]);

  const submit = () => {
    const trimmed = value.trim();
    if (!trimmed || disabled || loading) return;
    onSend(trimmed);
    setValue('');
  };

  const onKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      submit();
    }
  };

  return (
    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} alignItems="flex-end">
      <TextField
        value={value}
        onChange={(event) => setValue(event.target.value.slice(0, moduleConfig.ai.maxPromptLength))}
        onKeyDown={onKeyDown}
        placeholder={placeholder}
        fullWidth
        multiline
        minRows={2}
        maxRows={6}
        disabled={disabled || loading}
        inputProps={{ 'aria-label': 'Chat message' }}
      />
      <Box>
        <Button
          variant="contained"
          endIcon={<SendOutlinedIcon />}
          onClick={submit}
          disabled={disabled || loading || !value.trim()}
        >
          Send
        </Button>
      </Box>
    </Stack>
  );
}
