import { useState } from 'react';
import AutoAwesomeOutlinedIcon from '@mui/icons-material/AutoAwesomeOutlined';
import {
  Alert,
  Button,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Link,
  Menu,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { MarkdownViewer } from '@/shared/components';
import { ApiClientError } from '@/shared/api/types';
import { getErrorMessage } from '@/shared/utils/error';
import { knowledgeApi } from '@/features/knowledge/api/knowledge.api';
import { toKnowledgeNoteRequest } from '@/features/knowledge/schemas/knowledge.schemas';
import type { KnowledgeNoteResponse } from '@/features/knowledge/types/knowledge.types';
import { aiApi } from '../api/ai.api';
import type { AuthoringOperation, AuthoringProposal } from '../types/ai.types';

const OPERATIONS: { id: AuthoringOperation; label: string }[] = [
  { id: 'GENERATE', label: 'Generate' },
  { id: 'IMPROVE', label: 'Improve' },
  { id: 'REWRITE', label: 'Rewrite' },
  { id: 'SUMMARIZE', label: 'Summarize' },
  { id: 'EXPAND', label: 'Expand' },
  { id: 'GENERATE_QA', label: 'Generate Q&A' },
  { id: 'GENERATE_EXAMPLES', label: 'Generate examples' },
  { id: 'GENERATE_EXPLANATION', label: 'Explain' },
  { id: 'GENERATE_OBJECTIVES', label: 'Learning objectives' },
  { id: 'GENERATE_PREREQUISITES', label: 'Prerequisites' },
  { id: 'SUGGEST_STRUCTURE', label: 'Suggest structure' },
  { id: 'GENERATE_CODE', label: 'Code example' },
];

const COMPARE = new Set<AuthoringOperation>(['IMPROVE', 'REWRITE', 'EXPAND']);

interface AiAuthoringPanelProps {
  note: KnowledgeNoteResponse;
  onSaved: () => void;
}

export function AiAuthoringPanel({ note, onSaved }: AiAuthoringPanelProps) {
  const [menu, setMenu] = useState<HTMLElement | null>(null);
  const [operation, setOperation] = useState<AuthoringOperation | null>(null);
  const [instructions, setInstructions] = useState('');
  const [proposal, setProposal] = useState<AuthoringProposal | null>(null);
  const [draft, setDraft] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [conflict, setConflict] = useState(false);

  const close = () => {
    setOperation(null);
    setProposal(null);
    setDraft('');
    setError(null);
    setConflict(false);
    setInstructions('');
  };

  const generate = async (selected: AuthoringOperation, nextInstructions: string) => {
    setBusy(true);
    setError(null);
    setConflict(false);
    try {
      const created = await aiApi.generateProposal({
        operation: selected,
        topic: note.title,
        instructions: nextInstructions,
        contentId: note.id,
        useKnowledge: true,
      });
      setProposal(created);
      setDraft(created.content);
    } catch (cause) {
      setError(getErrorMessage(cause, 'AI authoring could not create a draft.'));
    } finally {
      setBusy(false);
    }
  };

  const accept = async () => {
    if (!proposal) return;
    setBusy(true);
    setError(null);
    try {
      const edited = await aiApi.editProposal(proposal.proposalId, draft);
      const accepted = await aiApi.acceptProposal(edited.proposalId);
      setProposal(accepted);
      setDraft(accepted.content);
    } catch (cause) {
      setError(getErrorMessage(cause, 'The draft could not be accepted.'));
    } finally {
      setBusy(false);
    }
  };

  const save = async () => {
    if (!proposal || proposal.status !== 'APPROVED') return;
    setBusy(true);
    setError(null);
    try {
      await knowledgeApi.update(note.id, {
        ...toKnowledgeNoteRequest(
          {
            title: note.title,
            summary: note.summary,
            content: draft,
            categoryName: note.category?.name ?? '',
            tagNames: note.tags,
          },
          note.version,
        ),
        content: draft,
      });
      onSaved();
      close();
    } catch (cause) {
      if (cause instanceof ApiClientError && (cause.status === 409 || cause.code === 'VERSION_CONFLICT')) {
        setConflict(true);
        onSaved();
      }
      setError(getErrorMessage(cause, 'The note could not be saved.'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <Button
        startIcon={<AutoAwesomeOutlinedIcon />}
        variant="outlined"
        onClick={(event) => setMenu(event.currentTarget)}
      >
        AI Assist
      </Button>
      <Menu anchorEl={menu} open={Boolean(menu)} onClose={() => setMenu(null)}>
        {OPERATIONS.map((item) => (
          <MenuItem
            key={item.id}
            onClick={() => {
              setMenu(null);
              setOperation(item.id);
              setProposal(null);
              setError(null);
            }}
          >
            {item.label}
          </MenuItem>
        ))}
      </Menu>
      <Dialog open={operation !== null} onClose={busy ? undefined : close} fullWidth maxWidth="md">
        <DialogTitle>AI generated draft</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            <Stack direction="row" spacing={1}>
              <Chip label="AI generated draft" color="warning" size="small" />
              <Chip label="Not published" size="small" />
            </Stack>
            {!proposal ? (
              <TextField
                label="Instructions"
                value={instructions}
                onChange={(event) => setInstructions(event.target.value)}
                fullWidth
                multiline
                minRows={2}
              />
            ) : null}
            {error ? <Alert severity="error">{error}</Alert> : null}
            {conflict ? (
              <Alert severity="warning">
                This note changed after the draft was created. Reload the latest content and reconcile it
                manually.
              </Alert>
            ) : null}
            {proposal ? (
              <>
                {proposal.warnings.map((warning) => (
                  <Alert key={warning} severity="info">
                    {warning}
                  </Alert>
                ))}
                {proposal.sources.map((source) =>
                  source.url.startsWith('/') ? (
                    <Link key={source.chunkId} component={RouterLink} to={source.url}>
                      {source.title}
                    </Link>
                  ) : null,
                )}
                {operation && COMPARE.has(operation) ? (
                  <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <Stack spacing={1} sx={{ flex: 1 }}>
                      <Typography variant="subtitle2">Current content</Typography>
                      <MarkdownViewer content={note.content} />
                    </Stack>
                    <Stack spacing={1} sx={{ flex: 1 }}>
                      <Typography variant="subtitle2">AI proposal</Typography>
                      <MarkdownViewer content={draft} />
                    </Stack>
                  </Stack>
                ) : (
                  <MarkdownViewer content={draft} />
                )}
                <TextField
                  label="Edit draft"
                  value={draft}
                  onChange={(event) => {
                    setDraft(event.target.value);
                    if (proposal.status === 'APPROVED') {
                      setProposal({ ...proposal, status: 'EDITING' });
                    }
                  }}
                  fullWidth
                  multiline
                  minRows={6}
                />
              </>
            ) : null}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={close} disabled={busy}>
            Close
          </Button>
          {!proposal && operation ? (
            <Button variant="contained" disabled={busy} onClick={() => void generate(operation, instructions)}>
              Generate
            </Button>
          ) : null}
          {proposal ? (
            <>
              <Button
                disabled={busy}
                onClick={() => void aiApi.rejectProposal(proposal.proposalId).then(close)}
              >
                Reject
              </Button>
              <Button
                disabled={busy}
                onClick={() =>
                  void aiApi
                    .regenerateProposal(proposal.proposalId, instructions)
                    .then((created) => {
                      setProposal(created);
                      setDraft(created.content);
                    })
                }
              >
                Regenerate
              </Button>
              <Button disabled={busy || proposal.status === 'APPROVED'} onClick={() => void accept()}>
                Accept
              </Button>
              <Button
                variant="contained"
                disabled={busy || proposal.status !== 'APPROVED'}
                onClick={() => void save()}
              >
                Save to ACOS
              </Button>
            </>
          ) : null}
        </DialogActions>
      </Dialog>
    </>
  );
}
