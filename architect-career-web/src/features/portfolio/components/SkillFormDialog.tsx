import { useEffect } from 'react';
import {
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
import { skillFormSchema, type SkillFormValues } from '../schemas/portfolio.schemas';
import { toSkillFormValues } from '../schemas/portfolio.form-mappers';
import type { ProficiencyLevel, Skill } from '../types/portfolio.types';

const PROFICIENCY_LEVELS: ProficiencyLevel[] = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'];

interface SkillFormDialogProps {
  open: boolean;
  skill?: Skill | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: SkillFormValues) => void;
}

export function SkillFormDialog({
  open,
  skill,
  loading = false,
  onClose,
  onSubmit,
}: SkillFormDialogProps) {
  const {
    control,
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<SkillFormValues>({
    resolver: zodResolver(skillFormSchema),
    defaultValues: toSkillFormValues(skill),
  });

  useEffect(() => {
    if (open) {
      reset(toSkillFormValues(skill));
    }
  }, [open, skill, reset]);

  return (
    <FormDialog
      open={open}
      title={skill ? 'Edit skill' : 'New skill'}
      loading={loading}
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
    >
      <Stack spacing={2.5} sx={{ pt: 1 }}>
        <TextField
          label="Name"
          fullWidth
          error={Boolean(errors.name)}
          helperText={errors.name?.message}
          {...register('name')}
        />
        <Controller
          name="proficiencyLevel"
          control={control}
          render={({ field }) => (
            <FormControl fullWidth error={Boolean(errors.proficiencyLevel)}>
              <InputLabel id="skill-proficiency-label">Proficiency</InputLabel>
              <Select {...field} labelId="skill-proficiency-label" label="Proficiency">
                {PROFICIENCY_LEVELS.map((level) => (
                  <MenuItem key={level} value={level}>
                    {formatEnumLabel(level)}
                  </MenuItem>
                ))}
              </Select>
              {errors.proficiencyLevel ? (
                <FormHelperText>{errors.proficiencyLevel.message}</FormHelperText>
              ) : null}
            </FormControl>
          )}
        />
        <TextField
          label="Years of experience"
          type="number"
          fullWidth
          inputProps={{ min: 0, step: 0.5 }}
          error={Boolean(errors.yearsOfExperience)}
          helperText={errors.yearsOfExperience?.message}
          {...register('yearsOfExperience')}
        />
        <TextField
          label="Description"
          fullWidth
          multiline
          minRows={3}
          error={Boolean(errors.description)}
          helperText={errors.description?.message}
          {...register('description')}
        />
      </Stack>
    </FormDialog>
  );
}
