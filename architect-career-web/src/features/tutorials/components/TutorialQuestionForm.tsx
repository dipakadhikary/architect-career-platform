import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Stack, TextField } from '@mui/material';
import {
  tutorialQuestionSchema,
  type TutorialQuestionFormValues,
} from '../schemas/tutorial.schemas';

interface TutorialQuestionFormProps {
  defaultValues?: TutorialQuestionFormValues;
  onSubmit: (values: TutorialQuestionFormValues) => void;
  onRegisterSubmit?: (submit: () => void) => void;
}

const emptyDefaults: TutorialQuestionFormValues = { question: '', answer: '' };

export function TutorialQuestionForm({
  defaultValues = emptyDefaults,
  onSubmit,
  onRegisterSubmit,
}: TutorialQuestionFormProps) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TutorialQuestionFormValues>({
    resolver: zodResolver(tutorialQuestionSchema),
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
        name="question"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Question (Markdown)"
            required
            multiline
            minRows={4}
            error={Boolean(errors.question)}
            helperText={errors.question?.message}
            fullWidth
          />
        )}
      />
      <Controller
        name="answer"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Answer (Markdown)"
            required
            multiline
            minRows={4}
            error={Boolean(errors.answer)}
            helperText={errors.answer?.message}
            fullWidth
          />
        )}
      />
    </Stack>
  );
}
