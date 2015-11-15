package net.bonysoft.wearpomodoro;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

public class NotificationBuilder {

    private static final Intent START_INTENT = new Intent(PomodoroReceiver.ACTION_START);
    private static final Intent STOP_INTENT = new Intent(PomodoroReceiver.ACTION_STOP);

    private static final int ID_ACTIVITY = 1;
    private static final int ID_START = 2;
    private static final int ID_STOP = 3;

    private final Context context;
    private final PomodoroTimer pomodoroTimer;

    public NotificationBuilder(Context context, PomodoroTimer timer) {
        this.context = context;
        this.pomodoroTimer = timer;
    }

    public Notification buildNotification() {
        Intent activityIntent = new Intent(context, MainActivity.class);
        Notification.Builder builder = new Notification.Builder(context);
        Notification.WearableExtender extender = new Notification.WearableExtender();
        extender.setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.pomodoro_background));
        boolean ongoing = true;
        if (pomodoroTimer.isRunning()) {
            buildRunningActions(extender);
        } else {
            buildStoppedActions(extender);
            ongoing = false;
        }
        builder.setContentTitle(buildNotificationTitle(pomodoroTimer))
                .setSmallIcon(R.drawable.notification_small_icon)
                .setStyle(new Notification.BigTextStyle())
                .setOngoing(ongoing)
                .setPriority(getPriorityForStatus(pomodoroTimer.getStatus()));
        builder.extend(extender);

        return builder.build();
    }

    private void buildStoppedActions(Notification.WearableExtender extender) {
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, ID_START, START_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        extender.addAction(new Notification.Action.Builder(R.drawable.ic_play, context.getString(R.string.action_start), startPendingIntent).build());
    }

    private void buildRunningActions(Notification.WearableExtender extender) {
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, ID_STOP, STOP_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        extender.addAction(new Notification.Action.Builder(R.drawable.ic_stop, context.getString(R.string.action_stop), stopPendingIntent).build());
    }

    private String buildNotificationTitle(PomodoroTimer timer) {
        switch (timer.getStatus()) {
            case WORK:
                return context.getString(R.string.title_work, formatRemainingTime(timer));
            case SMALL_BREAK:
                return context.getString(R.string.title_small_break, formatRemainingTime(timer));
            case LONG_BREAK:
                return context.getString(R.string.title_long_break, formatRemainingTime(timer));
            default:
                return context.getString(R.string.title_stopped);
        }
    }

    private int getPriorityForStatus(PomodoroStatus status) {
        switch (status) {
            case WORK:
                return Notification.PRIORITY_MAX;
            default:
                return Notification.PRIORITY_LOW;
        }
    }

    private String formatRemainingTime(PomodoroTimer timer) {
        int totalMinutes = timer.getIntervalDurationMinutes();
        int elapsedMinutes = timer.getElapsedMinutes();
        int remainingMinutes = totalMinutes - elapsedMinutes;

        if (remainingMinutes > 1) {
            return String.valueOf(remainingMinutes);
        } else {
            return "< 1";
        }
    }

}
