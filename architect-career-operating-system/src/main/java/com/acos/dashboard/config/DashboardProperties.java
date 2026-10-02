package com.acos.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Placeholder dashboard metric values used until analytics aggregation is available.
 *
 * @param welcomeMessagePrefix prefix applied before the authenticated user email
 * @param profileCompletion profile completion percentage
 * @param activeLearningPlans active learning plan count
 * @param completedCourses completed course count
 * @param portfolioProjects portfolio project count
 * @param jobApplications job application count
 * @param upcomingInterviews upcoming interview count
 */
@ConfigurationProperties(prefix = "acos.dashboard")
public record DashboardProperties(
    String welcomeMessagePrefix,
    int profileCompletion,
    int activeLearningPlans,
    int completedCourses,
    int portfolioProjects,
    int jobApplications,
    int upcomingInterviews) {}
