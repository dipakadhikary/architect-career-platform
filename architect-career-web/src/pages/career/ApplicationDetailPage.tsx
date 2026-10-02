import { useParams } from 'react-router-dom';
import { ApplicationDetailView } from '@/features/career';

export function ApplicationDetailPage() {
  const { applicationId = '' } = useParams<{ applicationId: string }>();

  return <ApplicationDetailView applicationId={applicationId} />;
}
