import { Navigate, type RouteObject } from 'react-router-dom';
import { AppLayout } from '@/layouts/AppLayout';
import { AuthLayout } from '@/layouts/AuthLayout';
import { ProtectedRoute } from '@/app/router/ProtectedRoute';
import { GuestRoute } from '@/app/router/GuestRoute';
import { RootLayout } from '@/app/router/RootLayout';
import { lazyNamed, withSuspense } from '@/app/router/lazyRoute';

const LoginPage = lazyNamed(() => import('@/pages/auth/LoginPage'), 'LoginPage');
const RegisterPage = lazyNamed(() => import('@/pages/auth/RegisterPage'), 'RegisterPage');
const ForgotUserIdPage = lazyNamed(() => import('@/pages/auth/ForgotUserIdPage'), 'ForgotUserIdPage');
const ForgotPasswordPage = lazyNamed(
  () => import('@/pages/auth/ForgotPasswordPage'),
  'ForgotPasswordPage',
);
const ResetPasswordPage = lazyNamed(
  () => import('@/pages/auth/ResetPasswordPage'),
  'ResetPasswordPage',
);
const UnauthorizedPage = lazyNamed(() => import('@/pages/UnauthorizedPage'), 'UnauthorizedPage');
const NotFoundPage = lazyNamed(() => import('@/pages/NotFoundPage'), 'NotFoundPage');
const ServerErrorPage = lazyNamed(() => import('@/pages/ServerErrorPage'), 'ServerErrorPage');
const OfflinePage = lazyNamed(() => import('@/pages/OfflinePage'), 'OfflinePage');
const ForbiddenPage = lazyNamed(() => import('@/pages/ForbiddenPage'), 'ForbiddenPage');

const DashboardPage = lazyNamed(() => import('@/features/dashboard'), 'DashboardPage');
const LearningPlansPage = lazyNamed(
  () => import('@/pages/learning/LearningPlansPage'),
  'LearningPlansPage',
);
const LearningPlanDetailPage = lazyNamed(
  () => import('@/pages/learning/LearningPlanDetailPage'),
  'LearningPlanDetailPage',
);
const PortfolioPage = lazyNamed(() => import('@/pages/portfolio'), 'PortfolioPage');
const ProjectDetailPage = lazyNamed(() => import('@/pages/portfolio'), 'ProjectDetailPage');
const KnowledgeListPage = lazyNamed(
  () => import('@/pages/knowledge/KnowledgeListPage'),
  'KnowledgeListPage',
);
const KnowledgeDetailPage = lazyNamed(
  () => import('@/pages/knowledge/KnowledgeDetailPage'),
  'KnowledgeDetailPage',
);
const TutorialsHomePage = lazyNamed(
  () => import('@/pages/tutorials'),
  'TutorialsHomePage',
);
const TutorialSplatPage = lazyNamed(
  () => import('@/pages/tutorials'),
  'TutorialSplatPage',
);
const CareerShell = lazyNamed(() => import('@/features/career'), 'CareerShell');
const CareerDashboardPage = lazyNamed(
  () => import('@/pages/career/CareerDashboardPage'),
  'CareerDashboardPage',
);
const CompaniesPage = lazyNamed(() => import('@/pages/career/CompaniesPage'), 'CompaniesPage');
const RecruitersPage = lazyNamed(() => import('@/pages/career/RecruitersPage'), 'RecruitersPage');
const ApplicationsListPage = lazyNamed(
  () => import('@/pages/career/ApplicationsListPage'),
  'ApplicationsListPage',
);
const ApplicationDetailPage = lazyNamed(
  () => import('@/pages/career/ApplicationDetailPage'),
  'ApplicationDetailPage',
);
const AiShell = lazyNamed(() => import('@/features/ai'), 'AiShell');
const AiDashboardPage = lazyNamed(() => import('@/features/ai'), 'AiDashboardPage');
const AskAiPage = lazyNamed(() => import('@/features/ai'), 'AskAiPage');
const AgentAiPage = lazyNamed(() => import('@/features/ai'), 'AgentAiPage');
const AiChatPage = lazyNamed(() => import('@/features/ai'), 'AiChatPage');
const KnowledgeAiPage = lazyNamed(() => import('@/features/ai'), 'KnowledgeAiPage');
const LearningAiPage = lazyNamed(() => import('@/features/ai'), 'LearningAiPage');
const CareerAiPage = lazyNamed(() => import('@/features/ai'), 'CareerAiPage');
const PortfolioAiPage = lazyNamed(() => import('@/features/ai'), 'PortfolioAiPage');

export const appRoutes: RouteObject[] = [
  {
    element: <RootLayout />,
    errorElement: withSuspense(<ServerErrorPage />),
    children: [
      {
        element: <AuthLayout />,
        children: [{ path: '/reset-password', element: withSuspense(<ResetPasswordPage />) }],
      },
      {
        element: <GuestRoute />,
        children: [
          {
            element: <AuthLayout />,
            children: [
              { path: '/login', element: withSuspense(<LoginPage />) },
              { path: '/register', element: withSuspense(<RegisterPage />) },
              { path: '/forgot-user-id', element: withSuspense(<ForgotUserIdPage />) },
              { path: '/forgot-password', element: withSuspense(<ForgotPasswordPage />) },
            ],
          },
        ],
      },
      {
        element: <ProtectedRoute />,
        children: [
          {
            element: <AppLayout />,
            children: [
              { index: true, element: withSuspense(<DashboardPage />) },
              {
                path: 'learning',
                children: [
                  { index: true, element: withSuspense(<LearningPlansPage />) },
                  { path: ':planId', element: withSuspense(<LearningPlanDetailPage />) },
                ],
              },
              {
                path: 'career',
                element: withSuspense(<CareerShell />),
                children: [
                  { index: true, element: withSuspense(<CareerDashboardPage />) },
                  { path: 'applications', element: withSuspense(<ApplicationsListPage />) },
                  {
                    path: 'applications/:applicationId',
                    element: withSuspense(<ApplicationDetailPage />),
                  },
                  { path: 'companies', element: withSuspense(<CompaniesPage />) },
                  { path: 'recruiters', element: withSuspense(<RecruitersPage />) },
                ],
              },
              {
                path: 'portfolio',
                children: [
                  { index: true, element: withSuspense(<PortfolioPage />) },
                  { path: 'projects/:projectId', element: withSuspense(<ProjectDetailPage />) },
                ],
              },
              {
                path: 'knowledge',
                children: [
                  { index: true, element: withSuspense(<KnowledgeListPage />) },
                  { path: ':noteId', element: withSuspense(<KnowledgeDetailPage />) },
                ],
              },
              {
                path: 'tutorials',
                children: [
                  { index: true, element: withSuspense(<TutorialsHomePage />) },
                  { path: '*', element: withSuspense(<TutorialSplatPage />) },
                ],
              },
              {
                path: 'ai',
                element: withSuspense(<AiShell />),
                children: [
                  { index: true, element: withSuspense(<AiDashboardPage />) },
                  { path: 'ask', element: withSuspense(<AskAiPage />) },
                  { path: 'agent', element: withSuspense(<AgentAiPage />) },
                  { path: 'chat', element: withSuspense(<AiChatPage />) },
                  { path: 'knowledge', element: withSuspense(<KnowledgeAiPage />) },
                  { path: 'learning', element: withSuspense(<LearningAiPage />) },
                  { path: 'career', element: withSuspense(<CareerAiPage />) },
                  { path: 'portfolio', element: withSuspense(<PortfolioAiPage />) },
                ],
              },
              { path: 'unauthorized', element: withSuspense(<UnauthorizedPage />) },
              { path: 'forbidden', element: withSuspense(<ForbiddenPage />) },
              { path: 'error', element: withSuspense(<ServerErrorPage />) },
            ],
          },
        ],
      },
      { path: '/offline', element: withSuspense(<OfflinePage />) },
      { path: '/home', element: <Navigate to="/" replace /> },
      { path: '*', element: withSuspense(<NotFoundPage />) },
    ],
  },
];
