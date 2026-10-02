import { useMemo } from 'react';
import type { TimelineItem } from '@/shared/components';
import { formatDate } from '@/shared/utils/date';
import { formatEnumLabel } from '@/shared/utils/label';
import { useAchievementsQuery } from './useAchievements';
import { useCertificationsQuery } from './useCertifications';
import { useProjectsQuery } from './useProjects';

function resolveProjectDate(startDate: string | null, endDate: string | null): string | null {
  return endDate ?? startDate;
}

export function useExperienceTimeline() {
  const projectsQuery = useProjectsQuery({
    page: 0,
    pageSize: 100,
    sort: 'startDate,desc',
  });
  const certificationsQuery = useCertificationsQuery();
  const achievementsQuery = useAchievementsQuery();

  const isLoading =
    projectsQuery.isLoading || certificationsQuery.isLoading || achievementsQuery.isLoading;

  const isError = projectsQuery.isError || certificationsQuery.isError || achievementsQuery.isError;

  const error = projectsQuery.error ?? certificationsQuery.error ?? achievementsQuery.error;

  const refetch = () => {
    void projectsQuery.refetch();
    void certificationsQuery.refetch();
    void achievementsQuery.refetch();
  };

  const items: TimelineItem[] = useMemo(() => {
    const events: Array<TimelineItem & { sortDate: string }> = [];

    for (const project of projectsQuery.data?.content ?? []) {
      const date = resolveProjectDate(project.startDate, project.endDate);
      if (!date) continue;

      const dateRange =
        project.startDate && project.endDate
          ? `${formatDate(project.startDate)} – ${formatDate(project.endDate)}`
          : formatDate(date);

      events.push({
        id: `project-${project.id}`,
        title: project.title,
        description: project.summary,
        meta: `Project · ${dateRange} · ${formatEnumLabel(project.status)}`,
        sortDate: date,
        completed: project.status === 'PUBLISHED',
      });
    }

    for (const certification of certificationsQuery.data ?? []) {
      events.push({
        id: `certification-${certification.id}`,
        title: certification.name,
        description: certification.issuer,
        meta: `Certification · ${formatDate(certification.issuedOn)}`,
        sortDate: certification.issuedOn,
        completed: true,
      });
    }

    for (const achievement of achievementsQuery.data ?? []) {
      events.push({
        id: `achievement-${achievement.id}`,
        title: achievement.title,
        description: achievement.description,
        meta: `Achievement · ${formatDate(achievement.achievedOn)}${
          achievement.organization ? ` · ${achievement.organization}` : ''
        }`,
        sortDate: achievement.achievedOn,
        completed: true,
      });
    }

    return events
      .sort((left, right) => right.sortDate.localeCompare(left.sortDate))
      .map(({ sortDate: _sortDate, ...item }) => item);
  }, [projectsQuery.data, certificationsQuery.data, achievementsQuery.data]);

  return { items, isLoading, isError, error, refetch };
}
