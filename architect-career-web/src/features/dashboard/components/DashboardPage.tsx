import {
  Box,
  Button,
  Grid,
  List,
  ListItemButton,
  ListItemText,
  Paper,
  Stack,
  Typography,
} from '@mui/material';
import SchoolOutlinedIcon from '@mui/icons-material/SchoolOutlined';
import WorkOutlineOutlinedIcon from '@mui/icons-material/WorkOutlineOutlined';
import FolderOpenOutlinedIcon from '@mui/icons-material/FolderOpenOutlined';
import MenuBookOutlinedIcon from '@mui/icons-material/MenuBookOutlined';
import EventAvailableOutlinedIcon from '@mui/icons-material/EventAvailableOutlined';
import AssignmentOutlinedIcon from '@mui/icons-material/AssignmentOutlined';
import { useNavigate } from 'react-router-dom';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { useTheme } from '@mui/material/styles';
import { useDashboardOverview } from '@/features/dashboard/hooks/useDashboardOverview';
import {
  EmptyState,
  ErrorPanel,
  LoadingSpinner,
  PageHeader,
  ProgressCard,
  StatisticsCard,
} from '@/shared/components';
import { formatDate, formatDateTime } from '@/shared/utils/date';
import { formatEnumLabel } from '@/shared/utils/label';
import { getErrorMessage } from '@/shared/utils/error';
import { useAuth } from '@/shared/hooks/useAuth';

const QUICK_LINKS = [
  { label: 'Learning plans', path: '/learning', icon: <SchoolOutlinedIcon /> },
  { label: 'Career tracker', path: '/career', icon: <WorkOutlineOutlinedIcon /> },
  { label: 'Portfolio', path: '/portfolio', icon: <FolderOpenOutlinedIcon /> },
  { label: 'Knowledge base', path: '/knowledge', icon: <MenuBookOutlinedIcon /> },
] as const;

const CHART_COLORS = ['#0B3D2E', '#1F6B52', '#C4A35A', '#6FCFB0', '#9A7D3A', '#175CD3', '#B42318'];

export function DashboardPage() {
  const navigate = useNavigate();
  const theme = useTheme();
  const { user } = useAuth();
  const {
    metrics,
    career,
    recentNotes,
    learningPlans,
    projects,
    applications,
    isLoading,
    isError,
    error,
    refetch,
  } = useDashboardOverview();

  if (isLoading) {
    return <LoadingSpinner label="Loading dashboard…" />;
  }

  if (isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(error)}
        onRetry={refetch}
        title="Dashboard unavailable"
      />
    );
  }

  const statusChartData =
    career?.applicationsByStatus.map((item) => ({
      name: formatEnumLabel(item.status),
      value: item.count,
      status: item.status,
    })) ?? [];

  const learningChartData = learningPlans.map((plan) => ({
    name: plan.title.length > 18 ? `${plan.title.slice(0, 18)}…` : plan.title,
    progress: plan.progressPercent,
  }));

  const upcomingTasks = [
    ...learningPlans
      .filter((plan) => plan.targetDate && plan.status === 'ACTIVE')
      .map((plan) => ({
        id: `plan-${plan.id}`,
        title: plan.title,
        meta: `Learning target · ${formatDate(plan.targetDate)}`,
        path: `/learning/${plan.id}`,
      })),
    ...(career
      ? [
          {
            id: 'interviews',
            title: `${career.upcomingInterviews} upcoming interview${career.upcomingInterviews === 1 ? '' : 's'}`,
            meta: 'Career pipeline',
            path: '/career/applications',
          },
          {
            id: 'offers',
            title: `${career.pendingOffers} pending offer${career.pendingOffers === 1 ? '' : 's'}`,
            meta: 'Review and decide',
            path: '/career/applications',
          },
        ].filter((item) => !item.title.startsWith('0 '))
      : []),
  ].slice(0, 6);

  const recentActivities = [
    ...applications.map((app) => ({
      id: `app-${app.id}`,
      title: app.title,
      meta: `${app.company.name} · ${formatEnumLabel(app.status)} · ${formatDateTime(app.updatedAt)}`,
      path: `/career/applications/${app.id}`,
    })),
    ...recentNotes.map((note) => ({
      id: `note-${note.id}`,
      title: note.title,
      meta: `Knowledge · ${formatDateTime(note.updatedAt)}`,
      path: `/knowledge/${note.id}`,
    })),
    ...projects.map((project) => ({
      id: `project-${project.id}`,
      title: project.title,
      meta: `Portfolio · ${formatEnumLabel(project.status)} · ${formatDateTime(project.updatedAt)}`,
      path: `/portfolio/projects/${project.id}`,
    })),
  ].slice(0, 8);

  const firstName = user?.firstName ?? 'Architect';

  return (
    <Box>
      <PageHeader
        title={`Welcome back, ${firstName}`}
        description={metrics?.welcomeMessage ?? 'Your Architect Career Operating System overview.'}
        actions={
          <Button variant="outlined" onClick={() => navigate('/career/applications')}>
            View applications
          </Button>
        }
      />

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <StatisticsCard
            label="Profile"
            value={`${metrics?.profileCompletion ?? 0}%`}
            helperText="Completion"
            icon={<AssignmentOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <StatisticsCard
            label="Learning"
            value={metrics?.activeLearningPlans ?? 0}
            helperText="Active plans"
            icon={<SchoolOutlinedIcon />}
            onClick={() => navigate('/learning')}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <StatisticsCard
            label="Completed"
            value={metrics?.completedCourses ?? 0}
            helperText="Courses"
            icon={<SchoolOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <StatisticsCard
            label="Portfolio"
            value={metrics?.portfolioProjects ?? 0}
            helperText="Projects"
            icon={<FolderOpenOutlinedIcon />}
            onClick={() => navigate('/portfolio')}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <StatisticsCard
            label="Applications"
            value={metrics?.jobApplications ?? career?.totalActiveApplications ?? 0}
            helperText="Active"
            icon={<WorkOutlineOutlinedIcon />}
            onClick={() => navigate('/career/applications')}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <StatisticsCard
            label="Interviews"
            value={metrics?.upcomingInterviews ?? career?.upcomingInterviews ?? 0}
            helperText="Upcoming"
            icon={<EventAvailableOutlinedIcon />}
            onClick={() => navigate('/career')}
          />
        </Grid>
      </Grid>

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, lg: 7 }}>
          <Paper variant="outlined" sx={{ p: 2.5, height: '100%', minHeight: 320 }}>
            <Typography variant="h6" gutterBottom>
              Learning progress
            </Typography>
            {learningChartData.length === 0 ? (
              <EmptyState
                title="No learning plans yet"
                description="Create a plan to track progress here."
                actionLabel="Open learning"
                onAction={() => navigate('/learning')}
              />
            ) : (
              <ResponsiveContainer width="100%" height={260}>
                <BarChart
                  data={learningChartData}
                  margin={{ top: 8, right: 8, left: 0, bottom: 8 }}
                >
                  <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />
                  <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                  <YAxis domain={[0, 100]} tick={{ fontSize: 12 }} />
                  <Tooltip />
                  <Bar dataKey="progress" fill={theme.palette.primary.main} radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, lg: 5 }}>
          <Paper variant="outlined" sx={{ p: 2.5, height: '100%', minHeight: 320 }}>
            <Typography variant="h6" gutterBottom>
              Career pipeline
            </Typography>
            {statusChartData.length === 0 ? (
              <EmptyState
                title="No application data"
                description="Track applications to see status distribution."
                actionLabel="Open career"
                onAction={() => navigate('/career')}
              />
            ) : (
              <ResponsiveContainer width="100%" height={260}>
                <PieChart>
                  <Pie
                    data={statusChartData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={90}
                    label
                  >
                    {statusChartData.map((entry, index) => (
                      <Cell key={entry.status} fill={CHART_COLORS[index % CHART_COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>
      </Grid>

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, md: 6, lg: 4 }}>
          <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              Career summary
            </Typography>
            <Stack spacing={1.5}>
              <Typography variant="body2">
                Active applications: <strong>{career?.totalActiveApplications ?? 0}</strong>
              </Typography>
              <Typography variant="body2">
                Companies: <strong>{career?.totalCompanies ?? 0}</strong>
              </Typography>
              <Typography variant="body2">
                Acceptance ratio:{' '}
                <strong>
                  {career ? `${Math.round((career.acceptanceRatio ?? 0) * 100)}%` : '—'}
                </strong>
              </Typography>
              <Typography variant="body2">
                Avg interview rating:{' '}
                <strong>
                  {career?.averageInterviewRating != null
                    ? career.averageInterviewRating.toFixed(1)
                    : '—'}
                </strong>
              </Typography>
              <Button size="small" onClick={() => navigate('/career')}>
                Open career dashboard
              </Button>
            </Stack>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, md: 6, lg: 4 }}>
          <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              Knowledge summary
            </Typography>
            {recentNotes.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                No recent notes. Capture architecture insights in Knowledge.
              </Typography>
            ) : (
              <List dense disablePadding>
                {recentNotes.map((note) => (
                  <ListItemButton key={note.id} onClick={() => navigate(`/knowledge/${note.id}`)}>
                    <ListItemText
                      primary={note.title}
                      secondary={note.category?.name ?? note.summary}
                    />
                  </ListItemButton>
                ))}
              </List>
            )}
            <Button size="small" sx={{ mt: 1 }} onClick={() => navigate('/knowledge')}>
              Browse knowledge
            </Button>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, lg: 4 }}>
          <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              Quick navigation
            </Typography>
            <Stack spacing={1}>
              {QUICK_LINKS.map((link) => (
                <Button
                  key={link.path}
                  variant="outlined"
                  startIcon={link.icon}
                  onClick={() => navigate(link.path)}
                  sx={{ justifyContent: 'flex-start' }}
                >
                  {link.label}
                </Button>
              ))}
            </Stack>
          </Paper>
        </Grid>
      </Grid>

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              Upcoming tasks
            </Typography>
            {upcomingTasks.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                You are clear for now. New deadlines and interviews will appear here.
              </Typography>
            ) : (
              <List dense disablePadding>
                {upcomingTasks.map((task) => (
                  <ListItemButton key={task.id} onClick={() => navigate(task.path)}>
                    <ListItemText primary={task.title} secondary={task.meta} />
                  </ListItemButton>
                ))}
              </List>
            )}
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              Recent activity
            </Typography>
            {recentActivities.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                Activity across career, knowledge, and portfolio will show up here.
              </Typography>
            ) : (
              <List dense disablePadding>
                {recentActivities.map((item) => (
                  <ListItemButton key={item.id} onClick={() => navigate(item.path)}>
                    <ListItemText primary={item.title} secondary={item.meta} />
                  </ListItemButton>
                ))}
              </List>
            )}
          </Paper>
        </Grid>
      </Grid>

      {learningPlans.length > 0 ? (
        <Box sx={{ mb: 2 }}>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Active learning plans
          </Typography>
          <Grid container spacing={2}>
            {learningPlans.map((plan) => (
              <Grid key={plan.id} size={{ xs: 12, md: 6, lg: 4 }}>
                <Box
                  onClick={() => navigate(`/learning/${plan.id}`)}
                  sx={{ cursor: 'pointer', height: '100%' }}
                >
                  <ProgressCard
                    title={plan.title}
                    progress={plan.progressPercent}
                    subtitle={formatEnumLabel(plan.status)}
                    detail={`${plan.completedTopics}/${plan.totalTopics} topics · target ${formatDate(plan.targetDate)}`}
                  />
                </Box>
              </Grid>
            ))}
          </Grid>
        </Box>
      ) : null}
    </Box>
  );
}
