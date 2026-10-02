import { FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { FormDialog } from '@/shared/components';
import {
  interviewSchema,
  type InterviewFormValues,
} from '@/features/career/schemas/career.schemas';
import type { InterviewResponse } from '@/features/career/types/career.types';
import { formatEnumLabel } from '@/shared/utils/label';
import { toDateInputValue, toDateTimeLocalValue } from '@/shared/utils/date';

interface InterviewFormDialogProps {
  open: boolean;
  interview?: InterviewResponse | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: InterviewFormValues) => void;
}

const defaultValues: InterviewFormValues = {
  interviewRound: 'SCREENING',
  interviewer: '',
  interviewDate: '',
  durationMinutes: '',
  status: 'SCHEDULED',
  rating: '',
  feedback: '',
  questionsAsked: '',
  strengths: '',
  weaknesses: '',
  improvementAreas: '',
  candidateNotes: '',
  confidenceRating: '',
  interviewReminderDate: '',
  locationOrLink: '',
  notes: '',
};

export function InterviewFormDialog({
  open,
  interview,
  loading = false,
  onClose,
  onSubmit,
}: InterviewFormDialogProps) {
  const isCreate = !interview;
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<InterviewFormValues>({
    resolver: zodResolver(interviewSchema),
    defaultValues,
  });

  useEffect(() => {
    if (open) {
      reset(
        interview
          ? {
              interviewRound: interview.interviewRound,
              interviewer: interview.interviewer ?? '',
              interviewDate: toDateTimeLocalValue(interview.interviewDate),
              durationMinutes: interview.durationMinutes ?? '',
              status: interview.status,
              rating: interview.rating ?? '',
              feedback: interview.feedback ?? '',
              questionsAsked: interview.questionsAsked ?? '',
              strengths: interview.strengths ?? '',
              weaknesses: interview.weaknesses ?? '',
              improvementAreas: interview.improvementAreas ?? '',
              candidateNotes: interview.candidateNotes ?? '',
              confidenceRating: interview.confidenceRating ?? '',
              interviewReminderDate: toDateInputValue(interview.interviewReminderDate),
              locationOrLink: interview.locationOrLink ?? '',
              notes: interview.notes ?? '',
            }
          : { ...defaultValues, status: 'SCHEDULED' },
      );
    }
  }, [open, interview, reset]);

  return (
    <FormDialog
      open={open}
      title={interview ? 'Edit Interview' : 'Schedule Interview'}
      loading={loading}
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
      maxWidth="md"
    >
      <Stack spacing={2} sx={{ pt: 1 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="interviewRound"
            control={control}
            render={({ field }) => (
              <FormControl fullWidth required>
                <InputLabel id="interview-round-label">Round</InputLabel>
                <Select {...field} labelId="interview-round-label" label="Round *">
                  {(['SCREENING', 'TECHNICAL', 'MANAGER', 'HR', 'FINAL', 'OTHER'] as const).map(
                    (round) => (
                      <MenuItem key={round} value={round}>
                        {formatEnumLabel(round)}
                      </MenuItem>
                    ),
                  )}
                </Select>
              </FormControl>
            )}
          />
          {!isCreate ? (
            <Controller
              name="status"
              control={control}
              render={({ field }) => (
                <FormControl fullWidth required>
                  <InputLabel id="interview-status-label">Status</InputLabel>
                  <Select {...field} labelId="interview-status-label" label="Status *">
                    {(
                      ['SCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW', 'RESCHEDULED'] as const
                    ).map((status) => (
                      <MenuItem key={status} value={status}>
                        {formatEnumLabel(status)}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              )}
            />
          ) : null}
        </Stack>
        <Controller
          name="interviewer"
          control={control}
          render={({ field }) => <TextField {...field} label="Interviewer" fullWidth />}
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="interviewDate"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Interview Date & Time"
                type="datetime-local"
                required
                fullWidth
                slotProps={{ inputLabel: { shrink: true } }}
                error={Boolean(errors.interviewDate)}
                helperText={errors.interviewDate?.message}
                onChange={(event) => {
                  field.onChange(event.target.value);
                }}
              />
            )}
          />
          <Controller
            name="durationMinutes"
            control={control}
            render={({ field }) => (
              <TextField {...field} label="Duration (minutes)" type="number" fullWidth />
            )}
          />
        </Stack>
        <Controller
          name="locationOrLink"
          control={control}
          render={({ field }) => <TextField {...field} label="Location or Link" fullWidth />}
        />
        <Controller
          name="interviewReminderDate"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Reminder Date"
              type="date"
              fullWidth
              slotProps={{ inputLabel: { shrink: true } }}
            />
          )}
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Controller
            name="rating"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Rating (1-5)"
                type="number"
                fullWidth
                inputProps={{ min: 1, max: 5 }}
              />
            )}
          />
          <Controller
            name="confidenceRating"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Confidence (1-5)"
                type="number"
                fullWidth
                inputProps={{ min: 1, max: 5 }}
              />
            )}
          />
        </Stack>
        <Controller
          name="feedback"
          control={control}
          render={({ field }) => (
            <TextField {...field} label="Feedback" fullWidth multiline minRows={2} />
          )}
        />
        <Controller
          name="questionsAsked"
          control={control}
          render={({ field }) => (
            <TextField {...field} label="Questions Asked" fullWidth multiline minRows={2} />
          )}
        />
        <Controller
          name="strengths"
          control={control}
          render={({ field }) => (
            <TextField {...field} label="Strengths" fullWidth multiline minRows={2} />
          )}
        />
        <Controller
          name="weaknesses"
          control={control}
          render={({ field }) => (
            <TextField {...field} label="Weaknesses" fullWidth multiline minRows={2} />
          )}
        />
        <Controller
          name="improvementAreas"
          control={control}
          render={({ field }) => (
            <TextField {...field} label="Improvement Areas" fullWidth multiline minRows={2} />
          )}
        />
        <Controller
          name="candidateNotes"
          control={control}
          render={({ field }) => (
            <TextField {...field} label="Candidate Notes" fullWidth multiline minRows={2} />
          )}
        />
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
