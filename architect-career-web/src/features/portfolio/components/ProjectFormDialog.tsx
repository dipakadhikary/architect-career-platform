import { useEffect } from 'react';
import {
  Autocomplete,
  Chip,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
} from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { Controller, useForm } from 'react-hook-form';
import { FormDialog } from '@/shared/components';
import { formatEnumLabel } from '@/shared/utils/label';
import { projectFormSchema, type ProjectFormValues } from '../schemas/portfolio.schemas';
import { toProjectFormValues } from '../schemas/portfolio.form-mappers';
import type { PortfolioProject, ProjectStatus } from '../types/portfolio.types';
import { useTechnologiesQuery } from '../hooks/useTechnologies';

const PROJECT_STATUSES: ProjectStatus[] = ['DRAFT', 'PUBLISHED', 'ARCHIVED'];

interface ProjectFormDialogProps {
  open: boolean;
  project?: PortfolioProject | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: ProjectFormValues) => void;
}

export function ProjectFormDialog({
  open,
  project,
  loading = false,
  onClose,
  onSubmit,
}: ProjectFormDialogProps) {
  const { data: technologies = [] } = useTechnologiesQuery();
  const technologyOptions = technologies.map((item) => item.name);

  const {
    control,
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ProjectFormValues>({
    resolver: zodResolver(projectFormSchema),
    defaultValues: toProjectFormValues(project),
  });

  useEffect(() => {
    if (open) {
      reset(toProjectFormValues(project));
    }
  }, [open, project, reset]);

  return (
    <FormDialog
      open={open}
      title={project ? 'Edit project' : 'New project'}
      loading={loading}
      maxWidth="md"
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
    >
      <Stack spacing={2.5} sx={{ pt: 1 }}>
        <TextField
          label="Title"
          fullWidth
          error={Boolean(errors.title)}
          helperText={errors.title?.message}
          {...register('title')}
        />
        <TextField
          label="Summary"
          fullWidth
          multiline
          minRows={2}
          error={Boolean(errors.summary)}
          helperText={errors.summary?.message}
          {...register('summary')}
        />
        <TextField
          label="Description"
          fullWidth
          multiline
          minRows={4}
          error={Boolean(errors.description)}
          helperText={errors.description?.message}
          {...register('description')}
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Repository URL"
            fullWidth
            error={Boolean(errors.repositoryUrl)}
            helperText={errors.repositoryUrl?.message}
            {...register('repositoryUrl')}
          />
          <TextField
            label="Live URL"
            fullWidth
            error={Boolean(errors.liveUrl)}
            helperText={errors.liveUrl?.message}
            {...register('liveUrl')}
          />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="status"
            control={control}
            render={({ field }) => (
              <FormControl fullWidth error={Boolean(errors.status)}>
                <InputLabel id="project-status-label">Status</InputLabel>
                <Select {...field} labelId="project-status-label" label="Status">
                  {PROJECT_STATUSES.map((status) => (
                    <MenuItem key={status} value={status}>
                      {formatEnumLabel(status)}
                    </MenuItem>
                  ))}
                </Select>
                {errors.status ? <FormHelperText>{errors.status.message}</FormHelperText> : null}
              </FormControl>
            )}
          />
          <TextField
            label="Start date"
            type="date"
            fullWidth
            slotProps={{ inputLabel: { shrink: true } }}
            error={Boolean(errors.startDate)}
            helperText={errors.startDate?.message}
            {...register('startDate')}
          />
          <TextField
            label="End date"
            type="date"
            fullWidth
            slotProps={{ inputLabel: { shrink: true } }}
            error={Boolean(errors.endDate)}
            helperText={errors.endDate?.message}
            {...register('endDate')}
          />
        </Stack>
        <Controller
          name="technologyNames"
          control={control}
          render={({ field }) => (
            <Autocomplete
              multiple
              freeSolo
              options={technologyOptions}
              value={field.value}
              onChange={(_, value) => field.onChange(value)}
              renderTags={(value, getTagProps) =>
                value.map((option, index) => {
                  const { key, ...tagProps } = getTagProps({ index });
                  return <Chip key={key} label={option} size="small" {...tagProps} />;
                })
              }
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Technologies"
                  placeholder="Add technologies"
                  error={Boolean(errors.technologyNames)}
                  helperText={errors.technologyNames?.message}
                />
              )}
            />
          )}
        />
      </Stack>
    </FormDialog>
  );
}
