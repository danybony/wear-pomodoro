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
    public static final String ACTION_UPDATE = "net.bonysoft.wearpomodoro.ACTION_UPDATE";
    public static final String ACTION_INTERVAL_END_ALARM = "net.bonysoft.wearpomodoro.ACTION_INTERVAL_END_ALARM";

    public static final int NOTIFICATION_ID = 1;

    private static final int MINUTE_MILLIS = 60000;

    private static final Intent UPDATE_INTENT = new Intent(ACTION_UPDATE);
    private static final Intent INTERVAL_END_ALARM = new Intent(ACTION_INTERVAL_END_ALARM);

    private static final int REQUEST_UPDATE = 1;
    private static final int REQUEST_ELAPSED = 2;
    private static final int REQUEST_FULL_TIME = 3;

    public static void setUpdate(Context context) {
        context.sendBroadcast(UPDATE_INTENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PomodoroTimer timer = PomodoroTimer.newInstance(context);
        if (intent.getAction().equals(ACTION_UPDATE)) {
            // Just update the notification
        } else if (intent.getAction().equals(ACTION_START)) {
            start(context, timer);
        } else if (intent.getAction().equals(ACTION_STOP)) {
            stop(context, timer);
        } else if (intent.getAction().equals(ACTION_INTERVAL_END_ALARM)) {
            nextInterval(context, timer);
            endIntervalAlarm(context, timer);
        }

        updateNotification(context, timer);
    }

    private void updateNotification(Context context, PomodoroTimer timer) {
        NotificationBuilder builder = new NotificationBuilder(context, timer);
        Notification notification = builder.buildNotification();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void stop(Context context, PomodoroTimer pomodoroTimer) {
        pomodoroTimer.stop();
        cancelAlarm(context, REQUEST_UPDATE, UPDATE_INTENT);
        cancelAlarm(context, REQUEST_FULL_TIME, INTERVAL_END_ALARM);
    }

    private void nextInterval(Context context, PomodoroTimer timer) {
        start(context, timer);
    }

    private void start(Context context, PomodoroTimer pomodoroTimer) {
        pomodoroTimer.start();

        setRepeatingAlarm(context, REQUEST_UPDATE, UPDATE_INTENT);
        long elapsedEnd = pomodoroTimer.getStartTime() + pomodoroTimer.getIntervalDurationMinutes() * MINUTE_MILLIS;
        if (elapsedEnd > System.currentTimeMillis()) {
            setAlarm(context, REQUEST_FULL_TIME, INTERVAL_END_ALARM, elapsedEnd);
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

    private void endIntervalAlarm(Context context, PomodoroTimer timer) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        PomodoroStatus currentStatus = timer.getStatus();
        vibrator.vibrate(currentStatus.getVibrationPattern(), -1);
    }
}
