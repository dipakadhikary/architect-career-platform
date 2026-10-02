import { useCallback, useMemo, type SyntheticEvent } from 'react';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import { useSearchParams } from 'react-router-dom';
import { PageHeader } from '@/shared/components';
import {
  AchievementsTab,
  CertificationsTab,
  ExperienceTab,
  ProjectsTab,
  SkillsTab,
  TechnologiesTab,
} from '@/features/portfolio';
import type { PortfolioTab } from '@/features/portfolio';

const TAB_DEFINITIONS: Array<{ value: PortfolioTab; label: string }> = [
  { value: 'projects', label: 'Projects' },
  { value: 'skills', label: 'Skills' },
  { value: 'technologies', label: 'Technologies' },
  { value: 'certifications', label: 'Certifications' },
  { value: 'achievements', label: 'Achievements' },
  { value: 'experience', label: 'Experience' },
];

const DEFAULT_TAB: PortfolioTab = 'projects';

function isPortfolioTab(value: string | null): value is PortfolioTab {
  return TAB_DEFINITIONS.some((tab) => tab.value === value);
}

export function PortfolioPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const tabParam = searchParams.get('tab');
  const activeTab = isPortfolioTab(tabParam) ? tabParam : DEFAULT_TAB;

  const handleTabChange = useCallback(
    (_: SyntheticEvent, nextTab: PortfolioTab) => {
      setSearchParams(nextTab === DEFAULT_TAB ? {} : { tab: nextTab }, { replace: true });
    },
    [setSearchParams],
  );

  const tabContent = useMemo(() => {
    switch (activeTab) {
      case 'projects':
        return <ProjectsTab />;
      case 'skills':
        return <SkillsTab />;
      case 'technologies':
        return <TechnologiesTab />;
      case 'certifications':
        return <CertificationsTab />;
      case 'achievements':
        return <AchievementsTab />;
      case 'experience':
        return <ExperienceTab />;
      default:
        return <ProjectsTab />;
    }
  }, [activeTab]);

  return (
    <Box>
      <PageHeader
        title="Portfolio"
        description="Manage projects, skills, technologies, certifications, and career milestones."
      />

      <Paper variant="outlined" sx={{ mb: 3 }}>
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          variant="scrollable"
          scrollButtons="auto"
          aria-label="Portfolio sections"
        >
          {TAB_DEFINITIONS.map((tab) => (
            <Tab key={tab.value} value={tab.value} label={tab.label} />
          ))}
        </Tabs>
      </Paper>

      {tabContent}
    </Box>
  );
}
