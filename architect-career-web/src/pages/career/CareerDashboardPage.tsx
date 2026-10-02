import { PageHeader } from '@/shared/components';
import { CareerDashboardView } from '@/features/career';

export function CareerDashboardPage() {
  return (
    <>
      <PageHeader
        title="Career Dashboard"
        description="Overview of your job search pipeline, interviews, and offers."
      />
      <CareerDashboardView />
    </>
  );
}
