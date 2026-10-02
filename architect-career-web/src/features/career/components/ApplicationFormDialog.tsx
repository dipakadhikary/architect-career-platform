import { FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { FormDialog } from '@/shared/components';
import {
  applicationSchema,
  type ApplicationFormValues,
} from '@/features/career/schemas/career.schemas';
import type {
  CompanyResponse,
  JobApplicationResponse,
  RecruiterResponse,
} from '@/features/career/types/career.types';
import { toDateInputValue } from '@/shared/utils/date';

interface ApplicationFormDialogProps {
  open: boolean;
  application?: JobApplicationResponse | null;
  companies: CompanyResponse[];
  recruiters: RecruiterResponse[];
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: ApplicationFormValues) => void;
}

const defaultValues: ApplicationFormValues = {
  companyId: '',
  recruiterId: '',
  title: '',
  jobDescription: '',
  source: '',
  salaryExpectation: '',
  currency: 'USD',
  resumeVersion: '',
  appliedOn: new Date().toISOString().slice(0, 10),
  location: '',
  jobUrl: '',
  notes: '',
};

export function ApplicationFormDialog({
  open,
  application,
  companies,
  recruiters,
  loading = false,
  onClose,
  onSubmit,
}: ApplicationFormDialogProps) {
  const {
    control,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<ApplicationFormValues>({
    resolver: zodResolver(applicationSchema),
    defaultValues,
  });

  const selectedCompanyId = watch('companyId');
  const filteredRecruiters = recruiters.filter(
    (recruiter) => !recruiter.companyId || recruiter.companyId === selectedCompanyId,
  );

  useEffect(() => {
    if (open) {
      reset(
        application
          ? {
              companyId: application.company.id,
              recruiterId: application.recruiter?.id ?? '',
              title: application.title,
              jobDescription: application.jobDescription ?? '',
              source: application.source ?? '',
              salaryExpectation: application.salaryExpectation ?? '',
              currency: application.currency ?? 'USD',
              resumeVersion: application.resumeVersion ?? '',
              appliedOn: toDateInputValue(application.appliedOn),
              location: application.location ?? '',
              jobUrl: application.jobUrl ?? '',
              notes: application.notes ?? '',
            }
          : defaultValues,
      );
    }
  }, [open, application, reset]);

  return (
    <FormDialog
      open={open}
      title={application ? 'Edit Application' : 'New Application'}
      loading={loading}
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
      maxWidth="md"
    >
      <Stack spacing={2} sx={{ pt: 1 }}>
        <Controller
          name="companyId"
          control={control}
          render={({ field }) => (
            <FormControl fullWidth required error={Boolean(errors.companyId)}>
              <InputLabel id="application-company-label">Company</InputLabel>
              <Select {...field} labelId="application-company-label" label="Company *">
                {companies.map((company) => (
                  <MenuItem key={company.id} value={company.id}>
                    {company.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}
        />
        <Controller
          name="recruiterId"
          control={control}
          render={({ field }) => (
            <FormControl fullWidth>
              <InputLabel id="application-recruiter-label">Recruiter</InputLabel>
              <Select {...field} labelId="application-recruiter-label" label="Recruiter">
                <MenuItem value="">None</MenuItem>
                {filteredRecruiters.map((recruiter) => (
                  <MenuItem key={recruiter.id} value={recruiter.id}>
                    {recruiter.fullName}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}
        />
        <Controller
          name="title"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Job Title"
              required
              fullWidth
              error={Boolean(errors.title)}
              helperText={errors.title?.message}
            />
          )}
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="appliedOn"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Applied On"
                type="date"
                required
                fullWidth
                slotProps={{ inputLabel: { shrink: true } }}
                error={Boolean(errors.appliedOn)}
                helperText={errors.appliedOn?.message}
              />
            )}
          />
          <Controller
            name="source"
            control={control}
            render={({ field }) => <TextField {...field} label="Source" fullWidth />}
          />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="salaryExpectation"
            control={control}
            render={({ field }) => (
              <TextField {...field} label="Salary Expectation" type="number" fullWidth />
            )}
          />
          <Controller
            name="currency"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Currency"
                fullWidth
                inputProps={{ maxLength: 3 }}
                error={Boolean(errors.currency)}
                helperText={errors.currency?.message}
              />
            )}
          />
        </Stack>
        <Controller
          name="location"
          control={control}
          render={({ field }) => <TextField {...field} label="Location" fullWidth />}
        />
        <Controller
          name="jobUrl"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Job URL"
              fullWidth
              error={Boolean(errors.jobUrl)}
              helperText={errors.jobUrl?.message}
            />
          )}
        />
        <Controller
          name="resumeVersion"
          control={control}
          render={({ field }) => <TextField {...field} label="Resume Version" fullWidth />}
        />
        <Controller
          name="jobDescription"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Job Description"
              fullWidth
              multiline
              minRows={4}
              error={Boolean(errors.jobDescription)}
              helperText={errors.jobDescription?.message}
            />
          )}
        />
        <Controller
          name="notes"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Notes"
              fullWidth
              multiline
              minRows={2}
              error={Boolean(errors.notes)}
              helperText={errors.notes?.message}
            />
          )}
        />
      </Stack>
    </FormDialog>
  );
}
