import { useMemo, useState } from 'react';
import { Box, Button, IconButton, Link, Stack, Tooltip, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import {
  ConfirmationDialog,
  DataTable,
  EmptyState,
  ErrorPanel,
  LoadingOverlay,
  type DataTableColumn,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { formatDate } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import { CertificationFormDialog } from './CertificationFormDialog';
import { useCertificationMutations, useCertificationsQuery } from '../hooks/useCertifications';
import type { CertificationFormValues } from '../schemas/portfolio.schemas';
import { toCertificationRequest } from '../schemas/portfolio.schemas';
import type { Certification } from '../types/portfolio.types';

export function CertificationsTab() {
  const notification = useNotification();
  const query = useCertificationsQuery();
  const { createMutation, updateMutation, deleteMutation } = useCertificationMutations();

  const [formOpen, setFormOpen] = useState(false);
  const [editingCertification, setEditingCertification] = useState<Certification | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Certification | null>(null);

  const rows = useMemo(() => query.data ?? [], [query.data]);

  const columns: DataTableColumn<Certification>[] = [
    {
      id: 'name',
      label: 'Certification',
      render: (row) => (
        <Box>
          <Typography variant="body2" fontWeight={600}>
            {row.name}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            {row.issuer}
          </Typography>
        </Box>
      ),
    },
    {
      id: 'issuedOn',
      label: 'Issued',
      width: 120,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {formatDate(row.issuedOn)}
        </Typography>
      ),
    },
    {
      id: 'expiresOn',
      label: 'Expires',
      width: 120,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {formatDate(row.expiresOn)}
        </Typography>
      ),
    },
    {
      id: 'credential',
      label: 'Credential',
      render: (row) =>
        row.credentialUrl ? (
          <Link href={row.credentialUrl} target="_blank" rel="noopener noreferrer" variant="body2">
            {row.credentialId ?? 'View credential'}
          </Link>
        ) : (
          <Typography variant="body2" color="text.secondary">
            {row.credentialId ?? '—'}
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
              aria-label="Edit certification"
              onClick={() => {
                setEditingCertification(row);
                setFormOpen(true);
              }}
            >
              <EditOutlinedIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              aria-label="Delete certification"
              color="error"
              onClick={() => setDeleteTarget(row)}
            >
              <DeleteOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Stack>
      ),
    },
  ];

  const handleSubmit = async (values: CertificationFormValues) => {
    try {
      if (editingCertification) {
        await updateMutation.mutateAsync({
          id: editingCertification.id,
          payload: toCertificationRequest(values),
        });
        notification.success('Certification updated');
      } else {
        await createMutation.mutateAsync(toCertificationRequest(values));
        notification.success('Certification created');
      }
      setFormOpen(false);
      setEditingCertification(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to save certification'));
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteMutation.mutateAsync(deleteTarget.id);
      notification.success('Certification deleted');
      setDeleteTarget(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to delete certification'));
    }
  };

  const formLoading = createMutation.isPending || updateMutation.isPending;

  if (query.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(query.error, 'Unable to load certifications')}
        onRetry={() => void query.refetch()}
      />
    );
  }

  return (
    <>
      <Stack direction="row" justifyContent="flex-end" sx={{ mb: 2 }}>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => {
            setEditingCertification(null);
            setFormOpen(true);
          }}
        >
          New certification
        </Button>
      </Stack>

      {query.isLoading ? <LoadingOverlay open /> : null}

      {!query.isLoading && rows.length === 0 ? (
        <EmptyState
          title="No certifications yet"
          description="Record professional certifications and credentials."
          actionLabel="New certification"
          onAction={() => {
            setEditingCertification(null);
            setFormOpen(true);
          }}
        />
      ) : (
        <DataTable
          columns={columns}
          rows={rows}
          rowKey={(row) => row.id}
          page={0}
          pageSize={rows.length || 10}
          totalElements={rows.length}
          onPageChange={() => undefined}
          onPageSizeChange={() => undefined}
        />
      )}

      <CertificationFormDialog
        open={formOpen}
        certification={editingCertification}
        loading={formLoading}
        onClose={() => {
          if (!formLoading) {
            setFormOpen(false);
            setEditingCertification(null);
          }
        }}
        onSubmit={handleSubmit}
      />

      <ConfirmationDialog
        open={Boolean(deleteTarget)}
        title="Delete certification"
        description={`Delete "${deleteTarget?.name}"? This action cannot be undone.`}
        confirmLabel="Delete"
        loading={deleteMutation.isPending}
        danger
        onCancel={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
      />
    </>
  );
}
