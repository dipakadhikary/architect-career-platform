import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import {
  Box,
  Button,
  IconButton,
  LinearProgress,
  MenuItem,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material';
import { useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { moduleConfig } from '@/app/config/module.config';
import {
  ConfirmationDialog,
  DataTable,
  EmptyState,
  ErrorPanel,
  FilterPanel,
  FormDialog,
  PageHeader,
  SearchBar,
  StatusChip,
  type DataTableColumn,
} from '@/shared/components';
import { useDebouncedValue } from '@/shared/hooks/useDebouncedValue';
import { formatDate } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import { formatEnumLabel } from '@/shared/utils/label';
import {
  PlanForm,
  toPlanFormValues,
  toPlanRequest,
  type PlanFormHandle,
} from '@/features/learning/components/PlanForm';
import {
  useCreatePlan,
  useDeletePlan,
  useLearningPlans,
  useUpdatePlan,
} from '@/features/learning/hooks';
import {
  LEARNING_PLAN_STATUSES,
  type LearningPlanStatus,
  type LearningPlanSummary,
} from '@/features/learning/types/learning.types';
import type { LearningPlanFormValues } from '@/features/learning/schemas/learning.schemas';

export function LearningPlansPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState<number>(moduleConfig.learning.defaultPageSize);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<LearningPlanStatus | ''>('');
  const [createOpen, setCreateOpen] = useState(false);
  const [editPlan, setEditPlan] = useState<LearningPlanSummary | null>(null);
  const [deletePlan, setDeletePlan] = useState<LearningPlanSummary | null>(null);

  const createFormRef = useRef<PlanFormHandle>(null);
  const editFormRef = useRef<PlanFormHandle>(null);

  const debouncedSearch = useDebouncedValue(search);

  const { data, isLoading, isError, error, refetch } = useLearningPlans({ page, size: pageSize });
  const createPlan = useCreatePlan();
  const updatePlan = useUpdatePlan();
  const removePlan = useDeletePlan();

  const filteredRows = useMemo(() => {
    const rows = data?.content ?? [];
    const query = debouncedSearch.trim().toLowerCase();

    return rows.filter((plan) => {
      const matchesStatus = !statusFilter || plan.status === statusFilter;
      const matchesSearch =
        !query ||
        plan.title.toLowerCase().includes(query) ||
        (plan.description?.toLowerCase().includes(query) ?? false);
      return matchesStatus && matchesSearch;
    });
  }, [data?.content, debouncedSearch, statusFilter]);

  const hasActiveFilters = Boolean(debouncedSearch.trim() || statusFilter);

  const columns: DataTableColumn<LearningPlanSummary>[] = [
    {
      id: 'title',
      label: 'Title',
      render: (row) => (
        <Box>
          <Typography variant="subtitle2">{row.title}</Typography>
          {row.description ? (
            <Typography
              variant="caption"
              color="text.secondary"
              noWrap
              sx={{ maxWidth: 360, display: 'block' }}
            >
              {row.description}
            </Typography>
          ) : null}
        </Box>
      ),
    },
    {
      id: 'status',
      label: 'Status',
      width: 130,
      render: (row) => <StatusChip status={row.status} />,
    },
    {
      id: 'progress',
      label: 'Progress',
      width: 180,
      render: (row) => (
        <Box sx={{ minWidth: 140 }}>
          <Stack direction="row" justifyContent="space-between" sx={{ mb: 0.5 }}>
            <Typography variant="caption" color="text.secondary">
              {row.completedTopics}/{row.totalTopics} topics
            </Typography>
            <Typography variant="caption" fontWeight={600}>
              {row.progressPercent}%
            </Typography>
          </Stack>
          <LinearProgress
            variant="determinate"
            value={Math.max(0, Math.min(100, row.progressPercent))}
            sx={{ height: 6, borderRadius: 999 }}
          />
        </Box>
      ),
    },
    {
      id: 'targetDate',
      label: 'Target date',
      width: 130,
      render: (row) => formatDate(row.targetDate),
    },
    {
      id: 'updatedAt',
      label: 'Updated',
      width: 130,
      render: (row) => formatDate(row.updatedAt),
    },
    {
      id: 'actions',
      label: '',
      width: 100,
      align: 'right',
      render: (row) => (
        <Stack direction="row" spacing={0.5} justifyContent="flex-end">
          <Tooltip title="Edit">
            <IconButton
              size="small"
              aria-label="Edit plan"
              onClick={(event) => {
                event.stopPropagation();
                setEditPlan(row);
              }}
            >
              <EditOutlinedIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              color="error"
              aria-label="Delete plan"
              onClick={(event) => {
                event.stopPropagation();
                setDeletePlan(row);
              }}
            >
              <DeleteOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Stack>
      ),
    },
  ];

  const handleCreate = (values: LearningPlanFormValues) => {
    createPlan.mutate(toPlanRequest(values), {
      onSuccess: () => setCreateOpen(false),
    });
  };

  const handleUpdate = (values: LearningPlanFormValues) => {
    if (!editPlan) return;
    updatePlan.mutate(
      { planId: editPlan.id, payload: toPlanRequest(values) },
      { onSuccess: () => setEditPlan(null) },
    );
  };

  if (isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(error, 'Failed to load learning plans')}
        onRetry={() => void refetch()}
      />
    );
  }

  return (
    <>
      <PageHeader
        title="Learning plans"
        description="Organize milestones and topics to track your professional development."
        actions={
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setCreateOpen(true)}>
            New plan
          </Button>
        }
      />

      <Stack spacing={2} sx={{ mb: 2 }}>
        <SearchBar
          value={search}
          onChange={setSearch}
          placeholder="Search plans by title or description"
        />
        <FilterPanel
          onClear={() => {
            setSearch('');
            setStatusFilter('');
          }}
        >
          <TextField
            select
            label="Status"
            size="small"
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value as LearningPlanStatus | '')}
            fullWidth
          >
            <MenuItem value="">All statuses</MenuItem>
            {LEARNING_PLAN_STATUSES.map((status) => (
              <MenuItem key={status} value={status}>
                {formatEnumLabel(status)}
              </MenuItem>
            ))}
          </TextField>
        </FilterPanel>
      </Stack>

      {!isLoading && (data?.content.length ?? 0) === 0 ? (
        <EmptyState
          title="No learning plans yet"
          description="Create your first plan to start tracking milestones and topics."
          actionLabel="Create plan"
          onAction={() => setCreateOpen(true)}
        />
      ) : (
        <DataTable
          columns={columns}
          rows={filteredRows}
          rowKey={(row) => row.id}
          page={page}
          pageSize={pageSize}
          totalElements={hasActiveFilters ? filteredRows.length : (data?.totalElements ?? 0)}
          onPageChange={setPage}
          onPageSizeChange={(size) => {
            setPageSize(size);
            setPage(0);
          }}
          loading={isLoading}
          emptyMessage={
            hasActiveFilters ? 'No plans match your search or filters.' : 'No records found'
          }
          onRowClick={(row) => navigate(`/learning/${row.id}`)}
        />
      )}

      <FormDialog
        open={createOpen}
        title="Create learning plan"
        submitLabel="Create"
        loading={createPlan.isPending}
        onClose={() => setCreateOpen(false)}
        onSubmit={() => createFormRef.current?.submit()}
      >
        <PlanForm ref={createFormRef} onSubmit={handleCreate} />
      </FormDialog>

      <FormDialog
        open={Boolean(editPlan)}
        title="Edit learning plan"
        submitLabel="Save changes"
        loading={updatePlan.isPending}
        onClose={() => setEditPlan(null)}
        onSubmit={() => editFormRef.current?.submit()}
      >
        {editPlan ? (
          <PlanForm
            ref={editFormRef}
            defaultValues={toPlanFormValues(editPlan)}
            onSubmit={handleUpdate}
          />
        ) : null}
      </FormDialog>

      <ConfirmationDialog
        open={Boolean(deletePlan)}
        title="Delete learning plan"
        description={`Delete "${deletePlan?.title}"? This will remove all milestones and topics.`}
        confirmLabel="Delete"
        danger
        loading={removePlan.isPending}
        onCancel={() => setDeletePlan(null)}
        onConfirm={() => {
          if (deletePlan) {
            removePlan.mutate(deletePlan.id, { onSuccess: () => setDeletePlan(null) });
          }
        }}
      />
    </>
  );
}
