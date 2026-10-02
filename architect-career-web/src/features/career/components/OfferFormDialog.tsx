import { FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { FormDialog } from '@/shared/components';
import { offerSchema, type OfferFormValues } from '@/features/career/schemas/career.schemas';
import type { OfferResponse } from '@/features/career/types/career.types';
import { formatEnumLabel } from '@/shared/utils/label';
import { toDateInputValue } from '@/shared/utils/date';

interface OfferFormDialogProps {
  open: boolean;
  offer?: OfferResponse | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: OfferFormValues) => void;
}

const defaultValues: OfferFormValues = {
  baseSalary: 0,
  currency: 'USD',
  joiningBonus: '',
  annualBonus: '',
  stockOptions: '',
  location: '',
  workMode: '',
  joiningDate: '',
  noticePeriodDays: '',
  offerStatus: 'PENDING',
  offerExpiryDate: '',
  notes: '',
};

export function OfferFormDialog({
  open,
  offer,
  loading = false,
  onClose,
  onSubmit,
}: OfferFormDialogProps) {
  const isCreate = !offer;
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<OfferFormValues>({
    resolver: zodResolver(offerSchema),
    defaultValues,
  });

  useEffect(() => {
    if (open) {
      reset(
        offer
          ? {
              baseSalary: offer.baseSalary,
              currency: offer.currency,
              joiningBonus: offer.joiningBonus ?? '',
              annualBonus: offer.annualBonus ?? '',
              stockOptions: offer.stockOptions ?? '',
              location: offer.location ?? '',
              workMode: offer.workMode ?? '',
              joiningDate: toDateInputValue(offer.joiningDate),
              noticePeriodDays: offer.noticePeriodDays ?? '',
              offerStatus: offer.offerStatus,
              offerExpiryDate: toDateInputValue(offer.offerExpiryDate),
              notes: offer.notes ?? '',
            }
          : { ...defaultValues, offerStatus: 'PENDING' },
      );
    }
  }, [open, offer, reset]);

  return (
    <FormDialog
      open={open}
      title={offer ? 'Edit Offer' : 'Add Offer'}
      loading={loading}
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
      maxWidth="md"
    >
      <Stack spacing={2} sx={{ pt: 1 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="baseSalary"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Base Salary"
                type="number"
                required
                fullWidth
                error={Boolean(errors.baseSalary)}
                helperText={errors.baseSalary?.message}
              />
            )}
          />
          <Controller
            name="currency"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Currency"
                required
                fullWidth
                inputProps={{ maxLength: 3 }}
                error={Boolean(errors.currency)}
                helperText={errors.currency?.message}
              />
            )}
          />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="joiningBonus"
            control={control}
            render={({ field }) => (
              <TextField {...field} label="Joining Bonus" type="number" fullWidth />
            )}
          />
          <Controller
            name="annualBonus"
            control={control}
            render={({ field }) => (
              <TextField {...field} label="Annual Bonus" type="number" fullWidth />
            )}
          />
        </Stack>
        <Controller
          name="stockOptions"
          control={control}
          render={({ field }) => <TextField {...field} label="Stock Options" fullWidth />}
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="location"
            control={control}
            render={({ field }) => <TextField {...field} label="Location" fullWidth />}
          />
          <Controller
            name="workMode"
            control={control}
            render={({ field }) => (
              <FormControl fullWidth>
                <InputLabel id="work-mode-label">Work Mode</InputLabel>
                <Select {...field} labelId="work-mode-label" label="Work Mode">
                  <MenuItem value="">None</MenuItem>
                  {(['ONSITE', 'REMOTE', 'HYBRID'] as const).map((mode) => (
                    <MenuItem key={mode} value={mode}>
                      {formatEnumLabel(mode)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            )}
          />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="joiningDate"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Joining Date"
                type="date"
                fullWidth
                slotProps={{ inputLabel: { shrink: true } }}
              />
            )}
          />
          <Controller
            name="offerExpiryDate"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Offer Expiry Date"
                type="date"
                fullWidth
                slotProps={{ inputLabel: { shrink: true } }}
              />
            )}
          />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="noticePeriodDays"
            control={control}
            render={({ field }) => (
              <TextField {...field} label="Notice Period (days)" type="number" fullWidth />
            )}
          />
          {!isCreate ? (
            <Controller
              name="offerStatus"
              control={control}
              render={({ field }) => (
                <FormControl fullWidth required>
                  <InputLabel id="offer-status-label">Status</InputLabel>
                  <Select {...field} labelId="offer-status-label" label="Status *">
                    {(['PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED', 'WITHDRAWN'] as const).map(
                      (status) => (
                        <MenuItem key={status} value={status}>
                          {formatEnumLabel(status)}
                        </MenuItem>
                      ),
                    )}
                  </Select>
                </FormControl>
              )}
            />
          ) : null}
        </Stack>
        <Controller
          name="notes"
          control={control}
          render={({ field }) => (
            <TextField {...field} label="Notes" fullWidth multiline minRows={2} />
          )}
        />
      </Stack>
    </FormDialog>
  );
}
