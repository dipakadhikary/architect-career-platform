import type {
  InterviewFormValues,
  OfferFormValues,
} from '@/features/career/schemas/career.schemas';
import { fromDateTimeLocalValue } from '@/shared/utils/date';

export function mapInterviewFormToRequest(values: InterviewFormValues) {
  const interviewDate = fromDateTimeLocalValue(values.interviewDate);
  if (!interviewDate) {
    throw new Error('Interview date is required');
  }

  return {
    interviewRound: values.interviewRound,
    interviewer: values.interviewer || undefined,
    interviewDate,
    durationMinutes: values.durationMinutes === '' ? undefined : Number(values.durationMinutes),
    status: values.status,
    rating: values.rating === '' ? undefined : Number(values.rating),
    feedback: values.feedback || undefined,
    questionsAsked: values.questionsAsked || undefined,
    strengths: values.strengths || undefined,
    weaknesses: values.weaknesses || undefined,
    improvementAreas: values.improvementAreas || undefined,
    candidateNotes: values.candidateNotes || undefined,
    confidenceRating: values.confidenceRating === '' ? undefined : Number(values.confidenceRating),
    interviewReminderDate: values.interviewReminderDate || undefined,
    locationOrLink: values.locationOrLink || undefined,
    notes: values.notes || undefined,
  };
}

export function mapOfferFormToRequest(values: OfferFormValues) {
  return {
    baseSalary: Number(values.baseSalary),
    currency: values.currency,
    joiningBonus: values.joiningBonus === '' ? undefined : Number(values.joiningBonus),
    annualBonus: values.annualBonus === '' ? undefined : Number(values.annualBonus),
    stockOptions: values.stockOptions || undefined,
    location: values.location || undefined,
    workMode: values.workMode || undefined,
    joiningDate: values.joiningDate || undefined,
    noticePeriodDays: values.noticePeriodDays === '' ? undefined : Number(values.noticePeriodDays),
    offerStatus: values.offerStatus,
    offerExpiryDate: values.offerExpiryDate || undefined,
    notes: values.notes || undefined,
  };
}
