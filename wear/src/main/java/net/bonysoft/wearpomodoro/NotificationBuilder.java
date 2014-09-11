package net.bonysoft.wearpomodoro;

import android.app.Notification;
import android.content.Context;

public class NotificationBuilder {

    private final Context context;
    private final PomodoroTimer timer;

    public NotificationBuilder(Context context, PomodoroTimer timer) {
        this.context = context;
        this.timer = timer;
    }

    public Notification buildNotification() {
        return null;
    }
}
