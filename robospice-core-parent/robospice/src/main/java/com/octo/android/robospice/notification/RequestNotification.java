package com.octo.android.robospice.notification;

public interface RequestNotification<T> {

    int getNotificationId();

    NotificationFactory<T> getNotificationFactory();
}
