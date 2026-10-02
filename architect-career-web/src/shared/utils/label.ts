/** Converts ENUM_VALUE style strings into Title Case labels. */
export function formatEnumLabel(value?: string | null): string {
  if (!value) return '—';
  return value
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');
}
