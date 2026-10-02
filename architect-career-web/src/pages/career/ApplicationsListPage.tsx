import { useMemo, useState } from 'react';
import { Button, FormControl, InputLabel, MenuItem, Select, TextField } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useNavigate } from 'react-router-dom';
import { moduleConfig } from '@/app/config/module.config';
import {
  ApplicationFormDialog,
  useApplicationMutations,
  useApplicationsSearch,
  useCompanies,
  useRecruiters,
  ALL_APPLICATION_STATUSES,
} from '@/features/career';
import type { ApplicationFormValues } from '@/features/career/schemas/career.schemas';
import type {
  ApplicationSearchFilters,
  ApplicationStatus,
  InterviewRound,
  JobApplicationResponse,
} from '@/features/career/types/career.types';
import {
  DataTable,
  EmptyState,
  ErrorPanel,
  FilterPanel,
  LoadingSpinner,
  PageHeader,
  SearchBar,
  StatusChip,
  type DataTableColumn,
} from '@/shared/components';
import { useDebouncedValue } from '@/shared/hooks/useDebouncedValue';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { formatDate } from '@/shared/utils/date';
import { formatEnumLabel } from '@/shared/utils/label';

const INTERVIEW_ROUNDS: InterviewRound[] = [
  'SCREENING',
  'TECHNICAL',
  'MANAGER',
  'HR',
  'FINAL',
  'OTHER',
];

function mapApplicationFormToRequest(values: ApplicationFormValues) {
  return {
    companyId: values.companyId,
    recruiterId: values.recruiterId || undefined,
    title: values.title,
    jobDescription: values.jobDescription || undefined,
    source: values.source || undefined,
    salaryExpectation:
      values.salaryExpectation === '' ? undefined : Number(values.salaryExpectation),
    currency: values.currency || undefined,
    resumeVersion: values.resumeVersion || undefined,
    appliedOn: values.appliedOn,
    location: values.location || undefined,
    jobUrl: values.jobUrl || undefined,
    notes: values.notes || undefined,
  };
}

const emptyFilters: ApplicationSearchFilters = {
  companyId: '',
  recruiterId: '',
  status: undefined,
  interviewRound: undefined,
  appliedFrom: '',
  appliedTo: '',
  salaryMin: undefined,
  salaryMax: undefined,
  keyword: '',
};

export function ApplicationsListPage() {
  const navigate = useNavigate();
  const notification = useNotification();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState<number>(moduleConfig.career.defaultPageSize);
  const [filters, setFilters] = useState<ApplicationSearchFilters>(emptyFilters);
  const [keywordInput, setKeywordInput] = useState('');
  const debouncedKeyword = useDebouncedValue(keywordInput, 300);

  const [dialogOpen, setDialogOpen] = useState(false);
  const { create } = useApplicationMutations();
  const companiesQuery = useCompanies();
  const recruitersQuery = useRecruiters();

  const searchParams = useMemo(
    () => ({
      page,
      size: pageSize,
      companyId: filters.companyId || undefined,
      recruiterId: filters.recruiterId || undefined,
      status: filters.status,
      interviewRound: filters.interviewRound,
      appliedFrom: filters.appliedFrom || undefined,
      appliedTo: filters.appliedTo || undefined,
      salaryMin: filters.salaryMin,
      salaryMax: filters.salaryMax,
      keyword: debouncedKeyword || undefined,
    }),
    [page, pageSize, filters, debouncedKeyword],
  );

  const { data, isLoading, isError, error, refetch } = useApplicationsSearch(searchParams);

  const companies = companiesQuery.data ?? [];
  const recruiters = recruitersQuery.data ?? [];

  const handleCreate = async (values: ApplicationFormValues) => {
    try {
      const created = await create.mutateAsync(mapApplicationFormToRequest(values));
      notification.success('Application created');
      setDialogOpen(false);
      navigate(`/career/applications/${created.id}`);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to create application'));
    }
  };

  const columns: DataTableColumn<JobApplicationResponse>[] = [
    { id: 'title', label: 'Title', sortable: true, render: (row) => row.title },
    { id: 'company', label: 'Company', render: (row) => row.company.name },
    {
      id: 'recruiter',
      label: 'Recruiter',
      render: (row) => row.recruiter?.fullName ?? '—',
    },
    {
      id: 'status',
      label: 'Status',
      render: (row) => <StatusChip status={row.status} />,
    },
    { id: 'appliedOn', label: 'Applied', render: (row) => formatDate(row.appliedOn) },
    { id: 'location', label: 'Location', render: (row) => row.location ?? '—' },
  ];

  const hasActiveFilters =
    Boolean(filters.companyId) ||
    Boolean(filters.recruiterId) ||
    Boolean(filters.status) ||
    Boolean(filters.interviewRound) ||
    Boolean(filters.appliedFrom) ||
    Boolean(filters.appliedTo) ||
    filters.salaryMin !== undefined ||
    filters.salaryMax !== undefined ||
    Boolean(debouncedKeyword);

  return (
    <>
      <PageHeader
        title="Applications"
        description="Search and manage job applications across your pipeline."
        actions={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setDialogOpen(true)}
            disabled={companies.length === 0}
          >
            New Application
          </Button>
        }
      />

      <SearchBar
        value={keywordInput}
        onChange={setKeywordInput}
        placeholder="Search by title, company, notes…"
      />

      <FilterPanel
        onClear={() => {
          setFilters(emptyFilters);
          setKeywordInput('');
          setPage(0);
        }}
      >
        <FormControl fullWidth size="small">
          <InputLabel id="filter-company-label">Company</InputLabel>
          <Select
            labelId="filter-company-label"
            label="Company"
            value={filters.companyId ?? ''}
            onChange={(event) => {
              setFilters((prev) => ({ ...prev, companyId: event.target.value }));
              setPage(0);
            }}
          >
            <MenuItem value="">All</MenuItem>
            {companies.map((company) => (
              <MenuItem key={company.id} value={company.id}>
                {company.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel id="filter-recruiter-label">Recruiter</InputLabel>
          <Select
            labelId="filter-recruiter-label"
            label="Recruiter"
            value={filters.recruiterId ?? ''}
            onChange={(event) => {
              setFilters((prev) => ({ ...prev, recruiterId: event.target.value }));
              setPage(0);
            }}
          >
            <MenuItem value="">All</MenuItem>
            {recruiters.map((recruiter) => (
              <MenuItem key={recruiter.id} value={recruiter.id}>
                {recruiter.fullName}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel id="filter-status-label">Status</InputLabel>
          <Select
            labelId="filter-status-label"
            label="Status"
            value={filters.status ?? ''}
            onChange={(event) => {
              setFilters((prev) => ({
                ...prev,
                status: (event.target.value as ApplicationStatus) || undefined,
              }));
              setPage(0);
            }}
          >
            <MenuItem value="">All</MenuItem>
            {ALL_APPLICATION_STATUSES.map((status) => (
              <MenuItem key={status} value={status}>
                {formatEnumLabel(status)}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel id="filter-round-label">Interview Round</InputLabel>
          <Select
            labelId="filter-round-label"
            label="Interview Round"
            value={filters.interviewRound ?? ''}
            onChange={(event) => {
              setFilters((prev) => ({
                ...prev,
                interviewRound: (event.target.value as InterviewRound) || undefined,
              }));
              setPage(0);
            }}
          >
            <MenuItem value="">All</MenuItem>
            {INTERVIEW_ROUNDS.map((round) => (
              <MenuItem key={round} value={round}>
                {formatEnumLabel(round)}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField
          label="Applied From"
          type="date"
          size="small"
          fullWidth
          value={filters.appliedFrom ?? ''}
          onChange={(event) => {
            setFilters((prev) => ({ ...prev, appliedFrom: event.target.value }));
            setPage(0);
          }}
          slotProps={{ inputLabel: { shrink: true } }}
        />

        <TextField
          label="Applied To"
          type="date"
          size="small"
          fullWidth
          value={filters.appliedTo ?? ''}
          onChange={(event) => {
            setFilters((prev) => ({ ...prev, appliedTo: event.target.value }));
            setPage(0);
          }}
          slotProps={{ inputLabel: { shrink: true } }}
        />

        <TextField
          label="Min Salary"
          type="number"
          size="small"
          fullWidth
          value={filters.salaryMin ?? ''}
          onChange={(event) => {
            setFilters((prev) => ({
              ...prev,
              salaryMin: event.target.value ? Number(event.target.value) : undefined,
            }));
            setPage(0);
          }}
        />

        <TextField
          label="Max Salary"
          type="number"
          size="small"
          fullWidth
          value={filters.salaryMax ?? ''}
          onChange={(event) => {
            setFilters((prev) => ({
              ...prev,
              salaryMax: event.target.value ? Number(event.target.value) : undefined,
            }));
            setPage(0);
          }}
        />
      </FilterPanel>

      {isLoading ? <LoadingSpinner label="Loading applications…" /> : null}

      {isError ? (
        <ErrorPanel
          message={getErrorMessage(error, 'Failed to load applications')}
          onRetry={() => void refetch()}
        />
      ) : null}

      {!isLoading && !isError && (data?.content.length ?? 0) === 0 ? (
        <EmptyState
          title={hasActiveFilters ? 'No matching applications' : 'No applications yet'}
          description={
            hasActiveFilters
              ? 'Try adjusting your filters or search terms.'
              : 'Create your first application to start tracking your job search.'
          }
          actionLabel={hasActiveFilters ? undefined : 'New Application'}
          onAction={hasActiveFilters ? undefined : () => setDialogOpen(true)}
        />
      ) : null}

      {!isLoading && !isError && (data?.content.length ?? 0) > 0 ? (
        <DataTable
          columns={columns}
          rows={data?.content ?? []}
          rowKey={(row) => row.id}
          page={page}
          pageSize={pageSize}
          totalElements={data?.totalElements ?? 0}
          onPageChange={setPage}
          onPageSizeChange={(size) => {
            setPageSize(size);
            setPage(0);
          }}
          onRowClick={(row) => navigate(`/career/applications/${row.id}`)}
        />
      ) : null}

      <ApplicationFormDialog
        open={dialogOpen}
        companies={companies}
        recruiters={recruiters}
        loading={create.isPending}
        onClose={() => setDialogOpen(false)}
        onSubmit={(values) => void handleCreate(values)}
      />
    </>
  );
}
