import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  List,
  ListItemButton,
  ListItemText,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {
  templateApi,
  type MessageTemplate,
  type NotificationChannel,
} from '@/features/notifications/api/templates.api';
import { getErrorMessage } from '@/shared/utils/error';

function escapeHtml(value: string) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}

function fill(source: string, variables: Record<string, string>, html: boolean) {
  return Object.entries(variables).reduce((text, [key, value]) => {
    const safe = html ? escapeHtml(value) : value;
    return text.replaceAll(`{{${key}}}`, safe);
  }, source);
}

function sampleVariables(sampleData: string): Record<string, string> {
  try {
    const parsed = JSON.parse(sampleData) as Record<string, unknown>;
    return Object.fromEntries(
      Object.entries(parsed).map(([key, value]) => [key, value == null ? '' : String(value)]),
    );
  } catch {
    return {};
  }
}

export function TemplateDesignerPage() {
  const [templates, setTemplates] = useState<MessageTemplate[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [draft, setDraft] = useState<MessageTemplate | null>(null);
  const [creating, setCreating] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    let active = true;
    templateApi
      .list()
      .then((items) => {
        if (!active) return;
        setTemplates(items);
        if (items[0]) {
          setSelectedId(items[0].id);
          setDraft(items[0]);
        }
      })
      .catch((cause: unknown) => {
        if (active) setError(getErrorMessage(cause, 'Unable to load templates'));
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, []);

  const variables = useMemo(() => sampleVariables(draft?.sampleData ?? '{}'), [draft?.sampleData]);
  const htmlPreview = fill(draft?.htmlBody ?? '', variables, true);
  const textPreview = fill(draft?.textBody ?? '', variables, false);

  function selectTemplate(template: MessageTemplate) {
    setCreating(false);
    setSaved(false);
    setSelectedId(template.id);
    setDraft(template);
  }

  function startCreate() {
    setCreating(true);
    setSelectedId(null);
    setSaved(false);
    setDraft({
      id: '',
      code: 'NEW_TEMPLATE',
      channel: 'EMAIL',
      name: 'New template',
      description: '',
      subject: 'ACOS',
      htmlBody:
        '<div style="font-family:Georgia,serif;padding:24px;background:#0B3D2E;color:#E8F0EC;">New design</div>',
      textBody: 'Message {{name}}',
      sampleData: '{"name":"Ada"}',
      enabled: true,
      version: 0,
    });
  }

  async function save() {
    if (!draft) return;
    setSaving(true);
    setError(null);
    setSaved(false);
    try {
      const payload = {
        code: draft.code,
        channel: draft.channel,
        name: draft.name,
        description: draft.description ?? '',
        subject: draft.subject ?? '',
        htmlBody: draft.htmlBody ?? '',
        textBody: draft.textBody,
        sampleData: draft.sampleData,
        enabled: draft.enabled,
        version: draft.version,
      };
      const savedTemplate = creating
        ? await templateApi.create(payload)
        : await templateApi.update(draft.id, payload);
      setTemplates((current) => {
        const rest = current.filter((item) => item.id !== savedTemplate.id);
        return [...rest, savedTemplate].sort((left, right) =>
          `${left.channel}${left.code}`.localeCompare(`${right.channel}${right.code}`),
        );
      });
      setCreating(false);
      setSelectedId(savedTemplate.id);
      setDraft(savedTemplate);
      setSaved(true);
    } catch (cause: unknown) {
      setError(getErrorMessage(cause, 'Unable to save template'));
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return <Typography>Loading templates…</Typography>;
  }

  return (
    <Stack spacing={2}>
      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" component="h1">
            Message templates
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Email and SMS designs stored in the database. The preview uses the sample data.
          </Typography>
        </Box>
        <Button variant="outlined" onClick={startCreate}>
          New template
        </Button>
      </Stack>
      {error ? <Alert severity="error">{error}</Alert> : null}
      {saved ? <Alert severity="success">Template saved.</Alert> : null}
      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ alignItems: 'stretch' }}>
        <List sx={{ width: { md: 280 }, border: 1, borderColor: 'divider', borderRadius: 1 }}>
          {templates.map((template) => (
            <ListItemButton
              key={template.id}
              selected={template.id === selectedId}
              onClick={() => selectTemplate(template)}
            >
              <ListItemText primary={template.name} secondary={template.code} />
              <Chip size="small" label={template.channel} />
            </ListItemButton>
          ))}
        </List>
        {draft ? (
          <Stack spacing={2} sx={{ flex: 1, minWidth: 0 }}>
            <Typography variant="h6" component="h2">
              Design
            </Typography>
            {draft.channel === 'SMS' ? (
              <Box
                role="region"
                aria-label="SMS design"
                sx={{
                  maxWidth: 360,
                  p: 2,
                  borderRadius: 2,
                  bgcolor: '#E7F6EF',
                  color: '#1c2b24',
                  whiteSpace: 'pre-wrap',
                }}
              >
                {textPreview}
              </Box>
            ) : (
              <Box
                component="iframe"
                title="Template design"
                sandbox=""
                srcDoc={htmlPreview}
                sx={{
                  width: '100%',
                  height: 420,
                  border: 1,
                  borderColor: 'divider',
                  bgcolor: '#fff',
                }}
              />
            )}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                label="Name"
                value={draft.name}
                onChange={(event) => setDraft({ ...draft, name: event.target.value })}
                fullWidth
              />
              {creating ? (
                <TextField
                  label="Code"
                  value={draft.code}
                  onChange={(event) =>
                    setDraft({ ...draft, code: event.target.value.toUpperCase() })
                  }
                  fullWidth
                />
              ) : null}
              {creating ? (
                <TextField
                  select
                  label="Channel"
                  value={draft.channel}
                  onChange={(event) =>
                    setDraft({ ...draft, channel: event.target.value as NotificationChannel })
                  }
                  fullWidth
                >
                  <MenuItem value="EMAIL">Email</MenuItem>
                  <MenuItem value="SMS">SMS</MenuItem>
                </TextField>
              ) : null}
            </Stack>
            {draft.channel === 'EMAIL' ? (
              <TextField
                label="Subject"
                value={draft.subject ?? ''}
                onChange={(event) => setDraft({ ...draft, subject: event.target.value })}
                fullWidth
              />
            ) : null}
            {draft.channel === 'EMAIL' ? (
              <TextField
                label="HTML design"
                value={draft.htmlBody ?? ''}
                onChange={(event) => setDraft({ ...draft, htmlBody: event.target.value })}
                fullWidth
                multiline
                minRows={8}
              />
            ) : null}
            <TextField
              label={draft.channel === 'SMS' ? 'SMS text' : 'Plain text'}
              value={draft.textBody}
              onChange={(event) => setDraft({ ...draft, textBody: event.target.value })}
              fullWidth
              multiline
              minRows={4}
            />
            <TextField
              label="Sample data"
              value={draft.sampleData}
              onChange={(event) => setDraft({ ...draft, sampleData: event.target.value })}
              fullWidth
              multiline
              minRows={2}
              helperText="JSON object. Keys replace {{placeholders}} in the design."
            />
            <Button variant="contained" onClick={() => void save()} disabled={saving}>
              {saving ? 'Saving…' : 'Save template'}
            </Button>
          </Stack>
        ) : null}
      </Stack>
    </Stack>
  );
}
