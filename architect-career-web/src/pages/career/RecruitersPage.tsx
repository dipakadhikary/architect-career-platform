import { useState } from 'react';
import { Button, IconButton, Stack } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import {
  ConfirmationDialog,
  DataTable,
  EmptyState,
  ErrorPanel,
  LoadingSpinner,
  PageHeader,
  StatusChip,
  type DataTableColumn,
} from '@/shared/components';
import {
  RecruiterFormDialog,
  useCompanies,
  useRecruiterMutations,
  useRecruiters,
} from '@/features/career';
import type { RecruiterFormValues } from '@/features/career/schemas/career.schemas';
import type { RecruiterResponse } from '@/features/career/types/career.types';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { formatDate } from '@/shared/utils/date';

function mapRecruiterFormToRequest(values: RecruiterFormValues) {
  return {
    companyId: values.companyId || undefined,
    fullName: values.fullName,
    email: values.email || undefined,
    phone: values.phone || undefined,
    linkedInUrl: values.linkedInUrl || undefined,
    lastContactDate: values.lastContactDate || undefined,
    nextFollowUpDate: values.nextFollowUpDate || undefined,
    status: values.status,
    notes: values.notes || undefined,
  };
}

export function RecruitersPage() {
  const notification = useNotification();
  const { data, isLoading, isError, error, refetch } = useRecruiters();
  const companiesQuery = useCompanies();
  const { create, update, remove } = useRecruiterMutations();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<RecruiterResponse | null>(null);
  const [deleting, setDeleting] = useState<RecruiterResponse | null>(null);

  const companies = companiesQuery.data ?? [];
  const companyNameById = new Map(companies.map((c) => [c.id, c.name]));
  const activeRecruiters = (data ?? []).filter((recruiter) => !recruiter.archived);

  const handleSubmit = async (values: RecruiterFormValues) => {
    try {
      const payload = mapRecruiterFormToRequest(values);
      if (editing) {
        await update.mutateAsync({ id: editing.id, payload });
        notification.success('Recruiter updated');
      } else {
        await create.mutateAsync(payload);
        notification.success('Recruiter created');
      }
      setDialogOpen(false);
      setEditing(null);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to save recruiter'));
    }
  };

  const handleDelete = async () => {
    if (!deleting) return;
    try {
      await remove.mutateAsync(deleting.id);
      notification.success('Recruiter archived');
      setDeleting(null);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to archive recruiter'));
    }
  };

  const columns: DataTableColumn<RecruiterResponse>[] = [
    { id: 'fullName', label: 'Name', render: (row) => row.fullName },
    {
      id: 'company',
      label: 'Company',
      render: (row) => (row.companyId ? (companyNameById.get(row.companyId) ?? '—') : '—'),
    },
    { id: 'email', label: 'Email', render: (row) => row.email ?? '—' },
    { id: 'phone', label: 'Phone', render: (row) => row.phone ?? '—' },
    {
      id: 'status',
      label: 'Status',
      render: (row) => <StatusChip status={row.status} />,
    },
    {
      id: 'nextFollowUpDate',
      label: 'Next Follow-up',
      render: (row) => formatDate(row.nextFollowUpDate),
    },
    {
      id: 'actions',
      label: 'Actions',
      align: 'right',
      render: (row) => (
        <Stack direction="row" justifyContent="flex-end">
          <IconButton
            size="small"
            aria-label="Edit recruiter"
            onClick={(event) => {
              event.stopPropagation();
              setEditing(row);
              setDialogOpen(true);
            }}
          >
            <EditOutlinedIcon fontSize="small" />
          </IconButton>
          <IconButton
            size="small"
            aria-label="Archive recruiter"
            onClick={(event) => {
              event.stopPropagation();
              setDeleting(row);
            }}
          >
            <DeleteOutlineIcon fontSize="small" />
          </IconButton>
        </Stack>
      ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Recruiters"
        description="Track recruiter contacts and follow-ups."
        actions={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => {
              setEditing(null);
              setDialogOpen(true);
            }}
          >
            Add Recruiter
          </Button>
        }
      />

      {isLoading ? <LoadingSpinner label="Loading recruiters…" /> : null}

      {isError ? (
        <ErrorPanel
          message={getErrorMessage(error, 'Failed to load recruiters')}
          onRetry={() => void refetch()}
        />
      ) : null}

      {!isLoading && !isError && activeRecruiters.length === 0 ? (
        <EmptyState
          title="No recruiters yet"
          description="Add recruiters you're in contact with."
          actionLabel="Add Recruiter"
          onAction={() => {
            setEditing(null);
            setDialogOpen(true);
          }}
        />
      ) : null}

      {!isLoading && !isError && activeRecruiters.length > 0 ? (
        <DataTable
          columns={columns}
          rows={activeRecruiters}
          rowKey={(row) => row.id}
          page={0}
          pageSize={activeRecruiters.length}
          totalElements={activeRecruiters.length}
          onPageChange={() => undefined}
          onPageSizeChange={() => undefined}
          emptyMessage="No recruiters found"
        />
      ) : null}

      <RecruiterFormDialog
        open={dialogOpen}
        recruiter={editing}
        companies={companies}
        loading={create.isPending || update.isPending}
        onClose={() => {
          setDialogOpen(false);
          setEditing(null);
        }}
        onSubmit={(values) => void handleSubmit(values)}
      />

      <ConfirmationDialog
        open={Boolean(deleting)}
        title="Archive Recruiter"
        description={`Archive "${deleting?.fullName}"? They will be removed from active lists.`}
        confirmLabel="Archive"
        danger
        loading={remove.isPending}
        onConfirm={() => void handleDelete()}
        onCancel={() => setDeleting(null)}
      />
    </>
  );
}
