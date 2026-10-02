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
  type DataTableColumn,
} from '@/shared/components';
import { CompanyFormDialog, useCompanies, useCompanyMutations } from '@/features/career';
import type { CompanyFormValues } from '@/features/career/schemas/career.schemas';
import type { CompanyResponse } from '@/features/career/types/career.types';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { formatDate } from '@/shared/utils/date';

function mapCompanyFormToRequest(values: CompanyFormValues) {
  return {
    name: values.name,
    website: values.website || undefined,
    industry: values.industry || undefined,
    location: values.location || undefined,
    notes: values.notes || undefined,
  };
}

export function CompaniesPage() {
  const notification = useNotification();
  const { data, isLoading, isError, error, refetch } = useCompanies();
  const { create, update, remove } = useCompanyMutations();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<CompanyResponse | null>(null);
  const [deleting, setDeleting] = useState<CompanyResponse | null>(null);

  const activeCompanies = (data ?? []).filter((company) => !company.archived);

  const handleSubmit = async (values: CompanyFormValues) => {
    try {
      const payload = mapCompanyFormToRequest(values);
      if (editing) {
        await update.mutateAsync({ id: editing.id, payload });
        notification.success('Company updated');
      } else {
        await create.mutateAsync(payload);
        notification.success('Company created');
      }
      setDialogOpen(false);
      setEditing(null);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to save company'));
    }
  };

  const handleDelete = async () => {
    if (!deleting) return;
    try {
      await remove.mutateAsync(deleting.id);
      notification.success('Company archived');
      setDeleting(null);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to archive company'));
    }
  };

  const columns: DataTableColumn<CompanyResponse>[] = [
    { id: 'name', label: 'Name', render: (row) => row.name },
    { id: 'industry', label: 'Industry', render: (row) => row.industry ?? '—' },
    { id: 'location', label: 'Location', render: (row) => row.location ?? '—' },
    {
      id: 'website',
      label: 'Website',
      render: (row) =>
        row.website ? (
          <a href={row.website} target="_blank" rel="noreferrer">
            {row.website}
          </a>
        ) : (
          '—'
        ),
    },
    { id: 'createdAt', label: 'Added', render: (row) => formatDate(row.createdAt) },
    {
      id: 'actions',
      label: 'Actions',
      align: 'right',
      render: (row) => (
        <Stack direction="row" justifyContent="flex-end">
          <IconButton
            size="small"
            aria-label="Edit company"
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
            aria-label="Archive company"
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
        title="Companies"
        description="Manage target companies for your job search."
        actions={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => {
              setEditing(null);
              setDialogOpen(true);
            }}
          >
            Add Company
          </Button>
        }
      />

      {isLoading ? <LoadingSpinner label="Loading companies…" /> : null}

      {isError ? (
        <ErrorPanel
          message={getErrorMessage(error, 'Failed to load companies')}
          onRetry={() => void refetch()}
        />
      ) : null}

      {!isLoading && !isError && activeCompanies.length === 0 ? (
        <EmptyState
          title="No companies yet"
          description="Add companies you're targeting or applying to."
          actionLabel="Add Company"
          onAction={() => {
            setEditing(null);
            setDialogOpen(true);
          }}
        />
      ) : null}

      {!isLoading && !isError && activeCompanies.length > 0 ? (
        <DataTable
          columns={columns}
          rows={activeCompanies}
          rowKey={(row) => row.id}
          page={0}
          pageSize={activeCompanies.length}
          totalElements={activeCompanies.length}
          onPageChange={() => undefined}
          onPageSizeChange={() => undefined}
          emptyMessage="No companies found"
        />
      ) : null}

      <CompanyFormDialog
        open={dialogOpen}
        company={editing}
        loading={create.isPending || update.isPending}
        onClose={() => {
          setDialogOpen(false);
          setEditing(null);
        }}
        onSubmit={(values) => void handleSubmit(values)}
      />

      <ConfirmationDialog
        open={Boolean(deleting)}
        title="Archive Company"
        description={`Archive "${deleting?.name}"? It will be removed from active lists.`}
        confirmLabel="Archive"
        danger
        loading={remove.isPending}
        onConfirm={() => void handleDelete()}
        onCancel={() => setDeleting(null)}
      />
    </>
  );
}
