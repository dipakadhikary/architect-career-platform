import { useState } from 'react';
import {
  Box,
  Button,
  IconButton,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import {
  ConfirmationDialog,
  EmptyState,
  ErrorPanel,
  LoadingSpinner,
  StatusChip,
} from '@/shared/components';
import { OfferFormDialog } from '@/features/career/components/OfferFormDialog';
import { mapOfferFormToRequest } from '@/features/career/utils/formMappers';
import { useOfferMutations, useOffers } from '@/features/career/hooks/career.hooks';
import type { OfferFormValues } from '@/features/career/schemas/career.schemas';
import type { OfferResponse } from '@/features/career/types/career.types';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { formatDate } from '@/shared/utils/date';
import { formatEnumLabel } from '@/shared/utils/label';

interface OffersSectionProps {
  applicationId: string;
}

function formatSalary(amount: number, currency: string): string {
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency,
    maximumFractionDigits: 0,
  }).format(amount);
}

export function OffersSection({ applicationId }: OffersSectionProps) {
  const notification = useNotification();
  const { data, isLoading, isError, error, refetch } = useOffers(applicationId);
  const { create, update, remove } = useOfferMutations(applicationId);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<OfferResponse | null>(null);
  const [deleting, setDeleting] = useState<OfferResponse | null>(null);

  const handleSubmit = async (values: OfferFormValues) => {
    try {
      const payload = mapOfferFormToRequest(values);
      if (editing) {
        await update.mutateAsync({ offerId: editing.id, payload });
        notification.success('Offer updated');
      } else {
        await create.mutateAsync(payload);
        notification.success('Offer added');
      }
      setDialogOpen(false);
      setEditing(null);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to save offer'));
    }
  };

  const handleDelete = async () => {
    if (!deleting) return;
    try {
      await remove.mutateAsync(deleting.id);
      notification.success('Offer deleted');
      setDeleting(null);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to delete offer'));
    }
  };

  if (isLoading) {
    return <LoadingSpinner label="Loading offers…" />;
  }

  if (isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(error, 'Failed to load offers')}
        onRetry={() => void refetch()}
      />
    );
  }

  const offers = data ?? [];

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h6">Offers</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => {
            setEditing(null);
            setDialogOpen(true);
          }}
        >
          Add Offer
        </Button>
      </Stack>

      {offers.length === 0 ? (
        <EmptyState
          title="No offers yet"
          description="Record an offer when you receive one for this application."
          actionLabel="Add Offer"
          onAction={() => {
            setEditing(null);
            setDialogOpen(true);
          }}
        />
      ) : (
        <TableContainer component={Paper} variant="outlined">
          <Table size="medium">
            <TableHead>
              <TableRow>
                <TableCell>Base Salary</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Work Mode</TableCell>
                <TableCell>Joining Date</TableCell>
                <TableCell>Expiry</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {offers.map((offer) => (
                <TableRow key={offer.id} hover>
                  <TableCell>{formatSalary(offer.baseSalary, offer.currency)}</TableCell>
                  <TableCell>
                    <StatusChip status={offer.offerStatus} />
                  </TableCell>
                  <TableCell>{offer.workMode ? formatEnumLabel(offer.workMode) : '—'}</TableCell>
                  <TableCell>{formatDate(offer.joiningDate)}</TableCell>
                  <TableCell>{formatDate(offer.offerExpiryDate)}</TableCell>
                  <TableCell align="right">
                    <IconButton
                      size="small"
                      aria-label="Edit offer"
                      onClick={() => {
                        setEditing(offer);
                        setDialogOpen(true);
                      }}
                    >
                      <EditOutlinedIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                      size="small"
                      aria-label="Delete offer"
                      onClick={() => setDeleting(offer)}
                    >
                      <DeleteOutlineIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      <OfferFormDialog
        open={dialogOpen}
        offer={editing}
        loading={create.isPending || update.isPending}
        onClose={() => {
          setDialogOpen(false);
          setEditing(null);
        }}
        onSubmit={(values) => void handleSubmit(values)}
      />

      <ConfirmationDialog
        open={Boolean(deleting)}
        title="Delete Offer"
        description="This will permanently remove the offer record."
        confirmLabel="Delete"
        danger
        loading={remove.isPending}
        onConfirm={() => void handleDelete()}
        onCancel={() => setDeleting(null)}
      />
    </Box>
  );
}
