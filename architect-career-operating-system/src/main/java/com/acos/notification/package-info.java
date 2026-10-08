/**
 * Notification module for email and SMS.
 *
 * <p>Auth and other domains call {@link com.acos.notification.api.NotificationPort} only. Provider
 * adapters, database templates, and the template designer live in this package so the module can
 * move into a standalone email and SMS service. This package does not depend on the auth domain.
 */
package com.acos.notification;
