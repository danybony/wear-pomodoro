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
//        if (pomodoroTimer.isPaused()) {
//            buildPausedActions(extender);
//        } else
        if (pomodoroTimer.isRunning()) {
            buildRunningActions(extender);
        } else {
            buildStoppedActions(extender);
            ongoing = false;
        }
        builder.setContentTitle(buildNotificationTitle(pomodoroTimer))
                .setSmallIcon(R.drawable.notification_small_icon)
                .setStyle(new Notification.BigTextStyle())
                .setOngoing(ongoing);
        builder.setPriority(Notification.PRIORITY_LOW);
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
//        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, ID_PAUSE, PAUSE_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, ID_STOP, STOP_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
//        extender.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent).build());
        extender.addAction(new Notification.Action.Builder(R.drawable.ic_action_stop, "Stop", stopPendingIntent).build());
    }

    private void buildPausedActions(Notification.WearableExtender extender) {
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, ID_STOP, STOP_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent resumePendingIntent = PendingIntent.getBroadcast(context, ID_RESUME, RESUME_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
//        extender.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_play, "Resume", resumePendingIntent).build());
        extender.addAction(new Notification.Action.Builder(R.drawable.ic_action_stop, "Stop", stopPendingIntent).build());
    }

    private String buildNotificationTitle(PomodoroTimer timer) {
//        if (timer.isPaused()) {
//            return context.getString(R.string.title_paused, timer.getElapsedMinutes());
//        } else
        switch (timer.getStatus()) {
            case WORK:
                return context.getString(R.string.title_work, timer.getCurrentPomodoro(), timer.getElapsedMinutes());
            case SMALL_BREAK:
                return context.getString(R.string.title_small_break, timer.getElapsedMinutes());
            case LONG_BREAK:
                return context.getString(R.string.title_long_break, timer.getElapsedMinutes());
            default:
                return context.getString(R.string.title_stopped);
        }
    }

}
