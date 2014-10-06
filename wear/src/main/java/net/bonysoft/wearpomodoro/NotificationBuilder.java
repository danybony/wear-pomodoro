package net.bonysoft.wearpomodoro;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

public class NotificationBuilder {

    private static final Intent START_INTENT = new Intent(PomodoroReceiver.ACTION_START);
    private static final Intent STOP_INTENT = new Intent(PomodoroReceiver.ACTION_STOP);
    private static final Intent RESET_INTENT = new Intent(PomodoroReceiver.ACTION_RESET);

    private static final int ID_ACTIVITY = 1;
    private static final int ID_START = 2;
    private static final int ID_STOP = 3;
    private static final int ID_RESET = 6;

    private final Context context;
    private final PomodoroTimer pomodoroTimer;

    public NotificationBuilder(Context context, PomodoroTimer timer) {
        this.context = context;
        this.pomodoroTimer = timer;
    }

    public Notification buildNotification() {
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, ID_ACTIVITY, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        Notification.WearableExtender extender = new Notification.WearableExtender();
        extender.setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.pomodoro_background));
        // extender.setDisplayIntent(activityPendingIntent);
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
        PendingIntent resetPendingIntent = PendingIntent.getBroadcast(context, ID_RESET, RESET_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        extender.addAction(new Notification.Action.Builder(R.drawable.ic_action_play, "Start", startPendingIntent).build());
        extender.addAction(new Notification.Action.Builder(R.drawable.ic_action_replay, "Reset", resetPendingIntent).build());
    }

    private void buildRunningActions(Notification.WearableExtender extender) {
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, ID_STOP, STOP_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        extender.addAction(new Notification.Action.Builder(R.drawable.ic_action_stop, "Stop", stopPendingIntent).build());
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

    private int getPriorityForStatus(PomodoroTimer.Status status) {
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

        if (remainingMinutes >= 1) {
            return String.valueOf(remainingMinutes);
        } else {
            return "< 1";
        }
    }

}
