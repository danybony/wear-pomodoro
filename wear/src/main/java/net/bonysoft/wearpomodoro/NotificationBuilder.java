package net.bonysoft.wearpomodoro;

import android.app.Notification;
import android.content.Context;

public class NotificationBuilder {

    private final Context context;
    private final Pomodoro timer;

    public NotificationBuilder(Context context, Pomodoro timer) {
        this.context = context;
        this.timer = timer;
    }

    public Notification buildNotification() {
        return null;
    }
}
