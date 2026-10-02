export type LogLevel = 'debug' | 'info' | 'warn' | 'error';

export interface SdkLoggerOptions {
  level?: LogLevel;
  prefix?: string;
  sink?: (level: LogLevel, message: string, meta?: Record<string, unknown>) => void;
}

const LEVEL_ORDER: Record<LogLevel, number> = {
  debug: 10,
  info: 20,
  warn: 30,
  error: 40,
};

/**
 * Minimal structured logger for SDK wrappers. Does not log secrets or tokens.
 */
export class SdkLogger {
  private readonly level: LogLevel;
  private readonly prefix: string;
  private readonly sink: (level: LogLevel, message: string, meta?: Record<string, unknown>) => void;

  constructor(options: SdkLoggerOptions = {}) {
    this.level = options.level ?? 'info';
    this.prefix = options.prefix ?? 'acos-sdk';
    this.sink =
      options.sink ??
      ((level, message, meta) => {
        const line = `[${this.prefix}] ${message}`;
        if (meta && Object.keys(meta).length > 0) {
          // Avoid dumping potentially sensitive response payloads.
          console[level === 'debug' ? 'debug' : level](line, {
            ...meta,
            // Explicitly omit common sensitive keys if present.
            authorization: undefined,
            accessToken: undefined,
            refreshToken: undefined,
            password: undefined,
          });
        } else {
          console[level === 'debug' ? 'debug' : level](line);
        }
      });
  }

  debug(message: string, meta?: Record<string, unknown>): void {
    this.log('debug', message, meta);
  }

  info(message: string, meta?: Record<string, unknown>): void {
    this.log('info', message, meta);
  }

  warn(message: string, meta?: Record<string, unknown>): void {
    this.log('warn', message, meta);
  }

  error(message: string, meta?: Record<string, unknown>): void {
    this.log('error', message, meta);
  }

  private log(level: LogLevel, message: string, meta?: Record<string, unknown>): void {
    if (LEVEL_ORDER[level] < LEVEL_ORDER[this.level]) {
      return;
    }
    this.sink(level, message, meta);
  }
}
