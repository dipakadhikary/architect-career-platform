import { useState } from 'react';
import { Box, Button, Chip, Link, Paper, Stack, Typography } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import {
  ConfirmationDialog,
  ErrorPanel,
  LoadingOverlay,
  MarkdownViewer,
  PageHeader,
  StatusChip,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { formatDate, formatDateTime } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import { ProjectFormDialog, useProjectMutations, useProjectQuery } from '@/features/portfolio';
import type { ProjectFormValues } from '@/features/portfolio';
import { toProjectRequest } from '@/features/portfolio';

export function ProjectDetailPage() {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate = useNavigate();
  const notification = useNotification();
  const query = useProjectQuery(projectId);
  const { updateMutation, deleteMutation } = useProjectMutations();

  const [formOpen, setFormOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);

  const project = query.data;

  const handleUpdate = async (values: ProjectFormValues) => {
    if (!project) return;
    try {
      await updateMutation.mutateAsync({
        id: project.id,
        payload: toProjectRequest(values),
      });
      notification.success('Project updated');
      setFormOpen(false);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to update project'));
    }
  };

  const handleDelete = async () => {
    if (!project) return;
    try {
      await deleteMutation.mutateAsync(project.id);
      notification.success('Project deleted');
      navigate('/portfolio');
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to delete project'));
    }
  };

  if (query.isLoading) {
    return <LoadingOverlay open label="Loading project…" />;
  }

  if (query.isError || !project) {
    return (
      <Box>
        <Button component={RouterLink} to="/portfolio" startIcon={<ArrowBackIcon />} sx={{ mb: 2 }}>
          Back to portfolio
        </Button>
        <ErrorPanel
          message={getErrorMessage(query.error, 'Project not found')}
          onRetry={() => void query.refetch()}
        />
      </Box>
    );
  }

  const formLoading = updateMutation.isPending;

  return (
    <Box>
      <Button component={RouterLink} to="/portfolio" startIcon={<ArrowBackIcon />} sx={{ mb: 2 }}>
        Back to portfolio
      </Button>

      <PageHeader
        title={project.title}
        description={project.summary}
        actions={
          <Stack direction="row" spacing={1}>
            <Button
              variant="outlined"
              startIcon={<EditOutlinedIcon />}
              onClick={() => setFormOpen(true)}
            >
              Edit
            </Button>
            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteOutlineIcon />}
              onClick={() => setDeleteOpen(true)}
            >
              Delete
            </Button>
          </Stack>
        }
      />

      <Stack spacing={3}>
        <Paper variant="outlined" sx={{ p: 3 }}>
          <Stack
            direction="row"
            spacing={1}
            alignItems="center"
            flexWrap="wrap"
            useFlexGap
            sx={{ mb: 2 }}
          >
            <StatusChip status={project.status} size="medium" />
            {project.technologies.map((tech) => (
              <Chip key={tech} label={tech} size="small" variant="outlined" />
            ))}
          </Stack>

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={3} sx={{ mb: 3 }}>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block">
                Timeline
              </Typography>
              <Typography variant="body2">
                {project.startDate || project.endDate
                  ? `${formatDate(project.startDate)} – ${formatDate(project.endDate)}`
                  : '—'}
              </Typography>
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block">
                Created
              </Typography>
              <Typography variant="body2">{formatDateTime(project.createdAt)}</Typography>
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block">
                Updated
              </Typography>
              <Typography variant="body2">{formatDateTime(project.updatedAt)}</Typography>
            </Box>
          </Stack>

          {(project.repositoryUrl || project.liveUrl) && (
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 3 }}>
              {project.repositoryUrl ? (
                <Link
                  href={project.repositoryUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  variant="body2"
                  sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
                >
                  Repository
                  <OpenInNewIcon sx={{ fontSize: 16 }} />
                </Link>
              ) : null}
              {project.liveUrl ? (
                <Link
                  href={project.liveUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  variant="body2"
                  sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
                >
                  Live demo
                  <OpenInNewIcon sx={{ fontSize: 16 }} />
                </Link>
              ) : null}
            </Stack>
          )}

          <Typography variant="h6" gutterBottom>
            Description
          </Typography>
          <MarkdownViewer content={project.description} />
        </Paper>
      </Stack>

      <ProjectFormDialog
        open={formOpen}
        project={project}
        loading={formLoading}
        onClose={() => {
          if (!formLoading) setFormOpen(false);
        }}
        onSubmit={handleUpdate}
      />

      <ConfirmationDialog
        open={deleteOpen}
        title="Delete project"
        description={`Delete "${project.title}"? This action cannot be undone.`}
        confirmLabel="Delete"
        loading={deleteMutation.isPending}
        danger
        onCancel={() => setDeleteOpen(false)}
        onConfirm={handleDelete}
      />
    </Box>
  );
}
