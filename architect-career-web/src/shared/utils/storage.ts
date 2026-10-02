/**
 * Thin typed wrapper around localStorage with JSON serialization.
 */
export const storage = {
  getString(key: string): string | null {
    try {
      return localStorage.getItem(key);
    } catch {
      return null;
    }
  },

  setString(key: string, value: string): void {
    try {
      localStorage.setItem(key, value);
    } catch {
      // Storage may be unavailable (private mode / quota).
    }
  },

  getJson<T>(key: string): T | null {
    const raw = storage.getString(key);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as T;
    } catch {
      return null;
    }
  },

  setJson(key: string, value: unknown): void {
    storage.setString(key, JSON.stringify(value));
  },

  remove(key: string): void {
    try {
      localStorage.removeItem(key);
    } catch {
      // ignore
    }
  },

  removeMany(keys: readonly string[]): void {
    keys.forEach((key) => storage.remove(key));
  },
};
