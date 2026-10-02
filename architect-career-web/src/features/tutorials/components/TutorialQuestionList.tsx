import { useState } from 'react';
import { Box, Button, Divider, Paper, Stack, Typography } from '@mui/material';
import { MarkdownViewer } from '@/shared/components';
import type { TutorialQuestionResponse } from '../types/tutorial.types';

export function TutorialQuestionList({ questions }: { questions: TutorialQuestionResponse[] }) {
  // Answers are hidden by default; each question toggles independently.
  const [revealedIds, setRevealedIds] = useState<ReadonlySet<string>>(() => new Set());

  if (!questions.length) {
    return (
      <Typography color="text.secondary">No questions yet for this tutorial topic.</Typography>
    );
  }

  return (
    <Stack spacing={2}>
      {questions.map((item, index) => {
        const answerVisible = revealedIds.has(item.id);
        return (
          <Paper key={item.id} variant="outlined" sx={{ p: 2 }}>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
              Question {index + 1}
            </Typography>
            <MarkdownViewer content={item.question} />
            <Box sx={{ mt: 1.5 }}>
              <Button
                size="small"
                variant="outlined"
                aria-expanded={answerVisible}
                onClick={() =>
                  setRevealedIds((current) => {
                    const next = new Set(current);
                    if (next.has(item.id)) {
                      next.delete(item.id);
                    } else {
                      next.add(item.id);
                    }
                    return next;
                  })
                }
              >
                {answerVisible ? 'Hide Answer' : 'Show Answer'}
              </Button>
            </Box>
            {answerVisible ? (
              <>
                <Divider sx={{ my: 1.5 }} />
                <Typography variant="subtitle2" gutterBottom>
                  Answer
                </Typography>
                <MarkdownViewer content={item.answer} />
              </>
            ) : null}
          </Paper>
        );
      })}
    </Stack>
  );
}
