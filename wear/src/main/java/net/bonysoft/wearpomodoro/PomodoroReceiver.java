package net.bonysoft.wearpomodoro;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;

public class PomodoroReceiver extends BroadcastReceiver {
    public static final String ACTION_START = "net.bonysoft.wearpomodoro.ACTION_START";
    public static final String ACTION_STOP = "net.bonysoft.wearpomodoro.ACTION_STOP";
    public static final String ACTION_RESET = "net.bonysoft.wearpomodoro.ACTION_RESET";
    public static final String ACTION_UPDATE = "net.bonysoft.wearpomodoro.ACTION_UPDATE";
    public static final String ACTION_ELAPSED_ALARM = "net.bonysoft.wearpomodoro.ACTION_ELAPSED_ALARM";
    public static final String ACTION_FULL_TIME_ALARM = "net.bonysoft.wearpomodoro.ACTION_FULL_TIME_ALARM";

    public static final int NOTIFICATION_ID = 1;

    private static final long[] ELAPSED_PATTERN = {0, 500, 250, 500, 250, 500};
    private static final long[] FULL_TIME_PATTERN = {0, 1000, 500, 1000, 500, 1000};

    private static final int MINUTE_MILLIS = 60000;

    private static final Intent UPDATE_INTENT = new Intent(ACTION_UPDATE);
    private static final Intent ELAPSED_ALARM = new Intent(ACTION_ELAPSED_ALARM);
    private static final Intent FULL_TIME_ALARM = new Intent(ACTION_FULL_TIME_ALARM);

    private static final int REQUEST_UPDATE = 1;
    private static final int REQUEST_ELAPSED = 2;
    private static final int REQUEST_FULL_TIME = 3;

    public static void setUpdate(Context context) {
        context.sendBroadcast(UPDATE_INTENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PomodoroTimer timer = PomodoroTimer.newInstance(context);
        boolean shouldUpdate = false;
        if (intent.getAction().equals(ACTION_UPDATE)) {
            shouldUpdate = true;
        } else if (intent.getAction().equals(ACTION_START)) {
            start(context, timer);
            shouldUpdate = true;
        } else if (intent.getAction().equals(ACTION_STOP)) {
            stop(context, timer);
            shouldUpdate = true;
        } else if (intent.getAction().equals(ACTION_RESET)) {
            reset(timer);
        } else if (intent.getAction().equals(ACTION_ELAPSED_ALARM)) {
            elapsedAlarm(context);
        } else if (intent.getAction().equals(ACTION_FULL_TIME_ALARM)) {
            fullTimeAlarm(context);
        }

        if (shouldUpdate) {
            updateNotification(context, timer);
        }
    }

    private void updateNotification(Context context, PomodoroTimer timer) {
        NotificationBuilder builder = new NotificationBuilder(context, timer);
        Notification notification = builder.buildNotification();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void reset(PomodoroTimer pomodoroTimer) {
        pomodoroTimer.reset();
    }

    private void stop(Context context, PomodoroTimer pomodoroTimer) {
        pomodoroTimer.stop();
        cancelAlarm(context, REQUEST_UPDATE, UPDATE_INTENT);
        cancelAlarm(context, REQUEST_ELAPSED, ELAPSED_ALARM);
        cancelAlarm(context, REQUEST_FULL_TIME, FULL_TIME_ALARM);
    }

    private void start(Context context, PomodoroTimer pomodoroTimer) {
        pomodoroTimer.start();

        setRepeatingAlarm(context, REQUEST_UPDATE, UPDATE_INTENT);
        long elapsedEnd = pomodoroTimer.getStartTime() + pomodoroTimer.getIntervalDurationMinutes() * MINUTE_MILLIS;
        if (elapsedEnd > System.currentTimeMillis()) {
            setAlarm(context, REQUEST_FULL_TIME, FULL_TIME_ALARM, elapsedEnd);
        }
    }

    private void setRepeatingAlarm(Context context, int requestCode, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MINUTE_MILLIS, pendingIntent);
    }

    private boolean isAlarmSet(Context context, int requestCode, Intent intent) {
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private void setAlarm(Context context, int requestCode, Intent intent, long time) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    private void cancelAlarm(Context context, int requestCode, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private void elapsedAlarm(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(ELAPSED_PATTERN, -1);
    }

    private void fullTimeAlarm(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(FULL_TIME_PATTERN, -1);
    }
}
