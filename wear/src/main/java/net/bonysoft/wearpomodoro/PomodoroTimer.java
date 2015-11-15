package net.bonysoft.wearpomodoro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PomodoroTimer implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = PomodoroTimer.class.getSimpleName();

    private static final String KEY_START = "net.bonysoft.wearpomodoro.KEY_START";
    private static final String KEY_CURRENT_POMODORO = "net.bonysoft.wearpomodoro.KEY_CURRENT_POMODORO";
    private static final String KEY_CURRENT_STATUS = "net.bonysoft.wearpomodoro.KEY_CURRENT_STATUS";
    private static final String PREFERENCES = "PomodoroTimer";

    public static final long[] IDLE_START_PATTERN = new long[]{0};
    public static final long[] WORK_START_PATTERN = new long[]{0, 300, 200, 300, 800, 300, 200, 300};
    public static final long[] SMALL_BREAK_START_PATTERN = new long[]{0, 1000, 500, 1000, 500, 1000};
    public static final long[] LONG_BREAK_START_PATTERN = new long[]{0, 1000, 500, 1000, 500, 1000};

    private static final int POMODORI_BEFORE_LONG_BREAK = 4;
    private static final int MINUTE_MILLIS = 60000;

    private long intervalStart;

    private int currentPomodoro;
    private PomodoroStatus currentStatus;

    private final SharedPreferences preferences;

    public static PomodoroTimer newInstance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        long start = preferences.getLong(KEY_START, 0);
        int currentPomodoro = preferences.getInt(KEY_CURRENT_POMODORO, 0);
        PomodoroStatus currentStatus = PomodoroStatus.from(preferences.getInt(KEY_CURRENT_STATUS, PomodoroStatus.IDLE.getSerialisedValue()));
        return new PomodoroTimer(preferences, start, currentPomodoro, currentStatus);
    }

    private PomodoroTimer(SharedPreferences preferences, long intervalStart, int currentPomodoro, PomodoroStatus currentStatus) {
        this.preferences = preferences;
        this.intervalStart = intervalStart;
        this.currentPomodoro = currentPomodoro;
        this.currentStatus = currentStatus;
    }

    public void start() {
        intervalStart = System.currentTimeMillis();
        advanceStatus();
        save();
    }

    public void advanceStatus() {
        Log.d(TAG, "Old status: " + currentStatus.toString());
        switch (currentStatus) {
            case IDLE:
            case SMALL_BREAK:
            case LONG_BREAK:
                currentStatus = PomodoroStatus.WORK;
                save();
                break;
            case WORK:
                currentPomodoro++;
                if (currentPomodoro % POMODORI_BEFORE_LONG_BREAK == 0) {
                    currentStatus = PomodoroStatus.LONG_BREAK;
                } else {
                    currentStatus = PomodoroStatus.SMALL_BREAK;
                }
                save();
                break;
            default:
                throw new IllegalStateException("Invalid status " + currentStatus);
        }
        Log.d(TAG, "New status: " + currentStatus.toString());
    }

    public int getIntervalDurationMinutes() {
        return currentStatus.getDurationMinutes();
    }

    public void stop() {
        intervalStart = 0L;
        currentPomodoro = 0;
        currentStatus = PomodoroStatus.IDLE;
        save();
    }

    public long getElapsed() {
        if (isRunning()) {
            return System.currentTimeMillis() - intervalStart;
        }
        return 0;
    }

    public int getCurrentPomodoro() {
        return currentPomodoro;
    }

    public PomodoroStatus getStatus() {
        return currentStatus;
    }

    public boolean isRunning() {
        return intervalStart > 0;
    }

    public int getElapsedMinutes() {
        return (int) ((System.currentTimeMillis() - intervalStart) / MINUTE_MILLIS);
    }

    public long getStartTime() {
        return intervalStart;
    }

    public void save() {
        preferences.edit()
                .putLong(KEY_START, intervalStart)
                .putInt(KEY_CURRENT_POMODORO, currentPomodoro)
                .putInt(KEY_CURRENT_STATUS, currentStatus.getSerialisedValue())
                .apply();
    }

    public void registerForUpdates() {
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void unregisterForUpdates() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_CURRENT_POMODORO)) {
            currentPomodoro = sharedPreferences.getInt(KEY_CURRENT_POMODORO, 0);
        } else if (key.equals(KEY_CURRENT_POMODORO)) {
            currentStatus = PomodoroStatus.from(sharedPreferences.getInt(KEY_CURRENT_STATUS, PomodoroStatus.IDLE.getSerialisedValue()));
        } else if (key.equals(KEY_START)) {
            intervalStart = sharedPreferences.getLong(key, 0L);

        }
    }
}
