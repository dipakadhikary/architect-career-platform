import { z } from 'zod';

/** Shared Zod schemas for auth forms. */
export const emailSchema = z.string().trim().email('Enter a valid email address');

export const passwordSchema = z
  .string()
  .min(12, 'Password must be at least 12 characters')
  .max(72, 'Password must be at most 72 characters')
  .regex(/[A-Z]/, 'Include at least one uppercase letter')
  .regex(/[a-z]/, 'Include at least one lowercase letter')
  .regex(/[0-9]/, 'Include at least one digit')
  .regex(/[^A-Za-z0-9]/, 'Include at least one special character');

export const loginSchema = z.object({
  email: emailSchema,
  password: z.string().min(1, 'Password is required'),
});

export const recoveryEmailSchema = z.object({
  email: emailSchema,
});

export const registerSchema = z
  .object({
    email: emailSchema,
    firstName: z.string().trim().min(1, 'First name is required').max(100),
    lastName: z.string().trim().min(1, 'Last name is required').max(100),
    password: passwordSchema,
    confirmPassword: z.string().min(1, 'Confirm your password'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  });

export const resetPasswordSchema = z
  .object({
    newPassword: passwordSchema,
    confirmPassword: z.string().min(1, 'Confirm your password'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  });

export type LoginFormValues = z.infer<typeof loginSchema>;
export type RegisterFormValues = z.infer<typeof registerSchema>;
export type RecoveryEmailFormValues = z.infer<typeof recoveryEmailSchema>;
export type ResetPasswordFormValues = z.infer<typeof resetPasswordSchema>;
