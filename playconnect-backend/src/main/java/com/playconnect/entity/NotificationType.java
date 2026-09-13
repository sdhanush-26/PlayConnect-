package com.playconnect.entity;

/**
 * The 5 notification kinds called out in the Day 54 plan. Kept as plain
 * String constants rather than a real Java enum since Notification.type
 * is stored as a flexible VARCHAR (see the comment there) — this class
 * just gives calling code named constants instead of typo-prone raw
 * strings like "JOIN_REQUEST" scattered across services.
 */
public final class NotificationType {
    public static final String MATCH_INVITATION = "MATCH_INVITATION";
    public static final String JOIN_REQUEST = "JOIN_REQUEST";
    public static final String REQUEST_ACCEPTED = "REQUEST_ACCEPTED";
    public static final String MATCH_REMINDER = "MATCH_REMINDER";
    public static final String NEW_MESSAGE = "NEW_MESSAGE";

    private NotificationType() {} // constants-only holder, never instantiated
}