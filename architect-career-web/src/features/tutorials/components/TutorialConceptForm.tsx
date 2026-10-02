import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Stack, TextField } from '@mui/material';
import {
  tutorialConceptSchema,
  type TutorialConceptFormValues,
} from '../schemas/tutorial.schemas';

interface TutorialConceptFormProps {
  defaultValues?: TutorialConceptFormValues;
  onSubmit: (values: TutorialConceptFormValues) => void;
  onRegisterSubmit?: (submit: () => void) => void;
}

const emptyDefaults: TutorialConceptFormValues = { content: '' };

export function TutorialConceptForm({
  defaultValues = emptyDefaults,
  onSubmit,
  onRegisterSubmit,
}: TutorialConceptFormProps) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TutorialConceptFormValues>({
    resolver: zodResolver(tutorialConceptSchema),
    defaultValues,
  });

  useEffect(() => {
    reset(defaultValues);
  }, [defaultValues, reset]);

  useEffect(() => {
    onRegisterSubmit?.(handleSubmit(onSubmit));
  }, [handleSubmit, onRegisterSubmit, onSubmit]);

  return (
    <Stack spacing={2} component="form" noValidate>
      <Controller
        name="content"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Concept (Markdown)"
            required
            multiline
            minRows={12}
            error={Boolean(errors.content)}
            helperText={errors.content?.message}
            fullWidth
          />
        )}
      />
    </Stack>
  );
}
