import { ThemeProvider, createTheme } from '@mui/material/styles';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { TemplateDesignerPage } from '@/features/notifications/pages/TemplateDesignerPage';
import type { MessageTemplate } from '@/features/notifications/api/templates.api';

vi.mock('@/features/notifications/api/templates.api', () => ({
  templateApi: {
    list: vi.fn(),
    update: vi.fn(),
    create: vi.fn(),
  },
}));

import { templateApi } from '@/features/notifications/api/templates.api';

const list = vi.mocked(templateApi.list);
const update = vi.mocked(templateApi.update);

const emailTemplate: MessageTemplate = {
  id: 'email-1',
  code: 'PASSWORD_RESET',
  channel: 'EMAIL',
  name: 'Password reset',
  description: 'Reset mail',
  subject: 'ACOS - Reset your password',
  htmlBody:
    '<div style="background:#0B3D2E;color:#fff">Reset your password {{resetUrl}}</div>',
  textBody: 'Reset your password:\n{{resetUrl}}',
  sampleData: '{"resetUrl":"http://localhost:5173/reset-password?token=sample"}',
  enabled: true,
  version: 0,
};

const smsTemplate: MessageTemplate = {
  id: 'sms-1',
  code: 'PASSWORD_RESET',
  channel: 'SMS',
  name: 'Password reset SMS',
  description: 'Reset text',
  subject: null,
  htmlBody: null,
  textBody: 'ACOS password reset: {{resetUrl}}',
  sampleData: '{"resetUrl":"http://localhost:5173/reset-password?token=sample"}',
  enabled: true,
  version: 1,
};

function renderPage() {
  return render(
    <ThemeProvider theme={createTheme()}>
      <TemplateDesignerPage />
    </ThemeProvider>,
  );
}

describe('TemplateDesignerPage', () => {
  beforeEach(() => {
    list.mockReset();
    update.mockReset();
    list.mockResolvedValue([emailTemplate, smsTemplate]);
  });

  it('shows the email design and the SMS design', async () => {
    const user = userEvent.setup();
    renderPage();

    expect(await screen.findByRole('heading', { name: 'Message templates' })).toBeInTheDocument();
    const frame = await screen.findByTitle('Template design');
    expect(frame).toHaveAttribute('srcdoc', expect.stringContaining('Reset your password'));
    expect(frame.getAttribute('srcdoc')).toContain('token=sample');

    await user.click(screen.getByRole('button', { name: /Password reset SMS/ }));
    const sms = await screen.findByRole('region', { name: 'SMS design' });
    expect(sms).toHaveTextContent('http://localhost:5173/reset-password?token=sample');
    expect(screen.queryByTitle('Template design')).not.toBeInTheDocument();
  });

  it('saves an edited template', async () => {
    const user = userEvent.setup();
    update.mockResolvedValue({ ...emailTemplate, name: 'Password reset updated', version: 1 });
    renderPage();

    const name = await screen.findByLabelText('Name');
    fireEvent.change(name, { target: { value: 'Password reset updated' } });
    await user.click(screen.getByRole('button', { name: 'Save template' }));

    await waitFor(() => {
      expect(update).toHaveBeenCalledWith(
        'email-1',
        expect.objectContaining({ name: 'Password reset updated', version: 0 }),
      );
    });
    expect(await screen.findByText('Template saved.')).toBeInTheDocument();
  });
});
