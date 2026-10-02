import { useMemo, useState } from 'react';
import {
  Box,
  Button,
  Chip,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  Tooltip,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { useNavigate } from 'react-router-dom';
import { moduleConfig } from '@/app/config/module.config';
import {
  ConfirmationDialog,
  DataTable,
  EmptyState,
  ErrorPanel,
  FilterPanel,
  LoadingOverlay,
  SearchBar,
  StatusChip,
  type DataTableColumn,
} from '@/shared/components';
import { useDebouncedValue } from '@/shared/hooks/useDebouncedValue';
import { useNotification } from '@/shared/hooks/useNotification';
import { formatDate } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import { formatEnumLabel } from '@/shared/utils/label';
import { ProjectFormDialog } from './ProjectFormDialog';
import { useProjectMutations, useProjectsQuery } from '../hooks/useProjects';
import type { ProjectFormValues } from '../schemas/portfolio.schemas';
import { toProjectRequest } from '../schemas/portfolio.schemas';
import type { PortfolioProject, ProjectStatus } from '../types/portfolio.types';

const PROJECT_STATUSES: ProjectStatus[] = ['DRAFT', 'PUBLISHED', 'ARCHIVED'];

export function ProjectsTab() {
  const navigate = useNavigate();
  const notification = useNotification();
  const { createMutation, updateMutation, deleteMutation } = useProjectMutations();

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<ProjectStatus | ''>('');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState<number>(moduleConfig.portfolio.defaultPageSize);
  const debouncedSearch = useDebouncedValue(search);

  const [formOpen, setFormOpen] = useState(false);
  const [editingProject, setEditingProject] = useState<PortfolioProject | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<PortfolioProject | null>(null);

  const query = useProjectsQuery({
    page,
    pageSize,
    search: debouncedSearch.trim() || undefined,
  });

  const filteredRows = useMemo(() => {
    const rows = query.data?.content ?? [];
    if (!statusFilter) return rows;
    return rows.filter((row) => row.status === statusFilter);
  }, [query.data?.content, statusFilter]);

  const columns: DataTableColumn<PortfolioProject>[] = [
    {
      id: 'title',
      label: 'Title',
      render: (row) => (
        <Box>
          <Typography variant="body2" fontWeight={600}>
            {row.title}
          </Typography>
          <Typography
            variant="caption"
            color="text.secondary"
            noWrap
            sx={{ maxWidth: 320, display: 'block' }}
          >
            {row.summary}
          </Typography>
        </Box>
      ),
    },
    {
      id: 'status',
      label: 'Status',
      width: 120,
      render: (row) => <StatusChip status={row.status} />,
    },
    {
      id: 'technologies',
      label: 'Technologies',
      render: (row) => (
        <Stack direction="row" spacing={0.5} flexWrap="wrap" useFlexGap>
          {row.technologies.slice(0, 4).map((tech) => (
            <Chip key={tech} label={tech} size="small" variant="outlined" />
          ))}
          {row.technologies.length > 4 ? (
            <Chip label={`+${row.technologies.length - 4}`} size="small" />
          ) : null}
        </Stack>
      ),
    },
    {
      id: 'dates',
      label: 'Timeline',
      width: 180,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {row.startDate || row.endDate
            ? `${formatDate(row.startDate)} – ${formatDate(row.endDate)}`
            : '—'}
        </Typography>
      ),
    },
    {
      id: 'updatedAt',
      label: 'Updated',
      width: 120,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {formatDate(row.updatedAt)}
        </Typography>
      ),
    },
    {
      id: 'actions',
      label: '',
      width: 96,
      align: 'right',
      render: (row) => (
        <Stack direction="row" spacing={0.5} justifyContent="flex-end">
          <Tooltip title="Edit">
            <IconButton
              size="small"
              aria-label="Edit project"
              onClick={(event) => {
                event.stopPropagation();
                setEditingProject(row);
                setFormOpen(true);
              }}
            >
              <EditOutlinedIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              aria-label="Delete project"
              color="error"
              onClick={(event) => {
                event.stopPropagation();
                setDeleteTarget(row);
              }}
            >
              <DeleteOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Stack>
      ),
    },
  ];

  const handleCreateOrUpdate = async (values: ProjectFormValues) => {
    const payload = toProjectRequest(values);

    try {
      if (editingProject) {
        await updateMutation.mutateAsync({ id: editingProject.id, payload });
        notification.success('Project updated');
      } else {
        await createMutation.mutateAsync(payload);
        notification.success('Project created');
      }
      setFormOpen(false);
      setEditingProject(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to save project'));
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteMutation.mutateAsync(deleteTarget.id);
      notification.success('Project deleted');
      setDeleteTarget(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to delete project'));
    }
  };

  const formLoading = createMutation.isPending || updateMutation.isPending;

  if (query.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(query.error, 'Unable to load projects')}
        onRetry={() => void query.refetch()}
      />
    );
  }

  return (
    <>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 2 }}>
        <Box sx={{ flex: 1 }}>
          <SearchBar
            value={search}
            onChange={(value) => {
              setSearch(value);
              setPage(0);
            }}
            placeholder="Search projects by title or summary…"
          />
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => {
            setEditingProject(null);
            setFormOpen(true);
          }}
          sx={{ alignSelf: { xs: 'stretch', sm: 'center' }, whiteSpace: 'nowrap' }}
        >
          New project
        </Button>
      </Stack>

      <FilterPanel
        onClear={() => {
          setStatusFilter('');
          setPage(0);
        }}
      >
        <FormControl size="small" fullWidth>
          <InputLabel id="project-status-filter">Status</InputLabel>
          <Select
            labelId="project-status-filter"
            label="Status"
            value={statusFilter}
            onChange={(event) => {
              setStatusFilter(event.target.value as ProjectStatus | '');
              setPage(0);
            }}
          >
            <MenuItem value="">All statuses</MenuItem>
            {PROJECT_STATUSES.map((status) => (
              <MenuItem key={status} value={status}>
                {formatEnumLabel(status)}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </FilterPanel>

      {query.isLoading ? <LoadingOverlay open /> : null}

      {!query.isLoading && filteredRows.length === 0 ? (
        <EmptyState
          title={debouncedSearch || statusFilter ? 'No matching projects' : 'No projects yet'}
          description={
            debouncedSearch || statusFilter
              ? 'Try adjusting your search or filters.'
              : 'Create your first portfolio project to showcase your work.'
          }
          actionLabel={debouncedSearch || statusFilter ? undefined : 'New project'}
          onAction={
            debouncedSearch || statusFilter
              ? undefined
              : () => {
                  setEditingProject(null);
                  setFormOpen(true);
                }
          }
        />
      ) : (
        <DataTable
          columns={columns}
          rows={filteredRows}
          rowKey={(row) => row.id}
          page={page}
          pageSize={pageSize}
          totalElements={statusFilter ? filteredRows.length : (query.data?.totalElements ?? 0)}
          onPageChange={setPage}
          onPageSizeChange={(size) => {
            setPageSize(size);
            setPage(0);
          }}
          onRowClick={(row) => navigate(`/portfolio/projects/${row.id}`)}
        />
      )}

      <ProjectFormDialog
        open={formOpen}
        project={editingProject}
        loading={formLoading}
        onClose={() => {
          if (!formLoading) {
            setFormOpen(false);
            setEditingProject(null);
          }
        }}
        onSubmit={handleCreateOrUpdate}
      />

      <ConfirmationDialog
        open={Boolean(deleteTarget)}
        title="Delete project"
        description={`Delete "${deleteTarget?.title}"? This action cannot be undone.`}
        confirmLabel="Delete"
        loading={deleteMutation.isPending}
        danger
        onCancel={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
      />
    </>
  );
}
