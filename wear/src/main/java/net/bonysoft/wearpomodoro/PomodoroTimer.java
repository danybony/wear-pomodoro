package net.bonysoft.wearpomodoro;

import android.content.Context;
import android.content.SharedPreferences;

public class PomodoroTimer implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static enum Status {
        IDLE(-1),
        WORK(0),
        SMALL_BREAK(1),
        LONG_BREAK(2);

        private int serialisedValue;

        Status(int serialisedValue) {
            this.serialisedValue = serialisedValue;
        }

        public int getSerialisedValue() {
            return serialisedValue;
        }

        public static Status from(int serialisedValue) {
            switch (serialisedValue) {
                case 0:
                    return WORK;
                case 1:
                    return SMALL_BREAK;
                case 2:
                    return LONG_BREAK;
                default:
                    return IDLE;
            }
        }
    }

    private static final String KEY_START = "net.bonysoft.wearpomodoro.KEY_START";
    private static final String KEY_CURRENT_POMODORO = "net.bonysoft.wearpomodoro.KEY_CURRENT_POMODORO";
    private static final String KEY_CURRENT_STATUS = "net.bonysoft.wearpomodoro.KEY_CURRENT_STATUS";
    private static final String PREFERENCES = "PomodoroTimer";

    private static final int WORK_INTERVAL_MINUTES = 25;
    private static final int SMALL_BREAK_MINUTES = 5;
    private static final int LONG_BREAK_MINUTES = 15;
    private static final int POMODORI_BEFORE_LONG_BREAK = 4;
    private static final int MINUTE_MILLIS = 60000;

    private long start;
    private int currentPomodoro;
    private Status currentStatus;

    private final SharedPreferences preferences;

    public static PomodoroTimer newInstance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        long start = preferences.getLong(KEY_START, 0);
        int currentPomodoro = preferences.getInt(KEY_CURRENT_POMODORO, 0);
        Status currentStatus = Status.from(preferences.getInt(KEY_CURRENT_STATUS, Status.IDLE.getSerialisedValue()));
        return new PomodoroTimer(preferences, start, currentPomodoro, currentStatus);
    }

    private PomodoroTimer(SharedPreferences preferences, long start, int currentPomodoro, Status currentStatus) {
        this.preferences = preferences;
        this.start = start;
        this.currentPomodoro = currentPomodoro;
        this.currentStatus = currentStatus;
    }

    public void start() {
        start = System.currentTimeMillis();
        save();
    }

    public void advanceStatus() {
        switch (currentStatus) {
            case IDLE:
            case SMALL_BREAK:
            case LONG_BREAK:
                currentStatus = Status.WORK;
                save();
                break;
            case WORK:
                currentPomodoro++;
                if (currentPomodoro % POMODORI_BEFORE_LONG_BREAK == 0) {
                    currentStatus = Status.LONG_BREAK;
                } else {
                    currentStatus = Status.SMALL_BREAK;
                }
                save();
            default:
                throw new IllegalStateException("Invalid status " + currentStatus);
        }
    }

    public int getIntervalDurationMinutes() {
        switch (currentStatus) {
            case SMALL_BREAK:
                return SMALL_BREAK_MINUTES;
            case LONG_BREAK:
                return LONG_BREAK_MINUTES;
            case WORK:
                return WORK_INTERVAL_MINUTES;
            default:
                throw new IllegalStateException("Invalid status " + currentStatus);
        }
    }

    public void stop() {
        reset();
        save();
    }

    public void pause() {
//        currentStoppage = System.currentTimeMillis();
        save();
    }

    public void resume() {
//        totalStoppages += System.currentTimeMillis() - currentStoppage;
//        currentStoppage = 0L;
        save();
    }

    public void reset() {
        resetWithoutSave();
        save();
    }

    private void resetWithoutSave() {
        start = 0L;
        currentPomodoro = 0;
        currentStatus = Status.IDLE;
    }

    public long getElapsed() {
        if (isRunning()) {
            return System.currentTimeMillis() - start;
        }
        return 0;
    }

    public boolean isRunning() {
        return start > 0;
    }

    public int getElapsedMinutes() {
        return (int) ((System.currentTimeMillis() - start) / MINUTE_MILLIS);
    }

    public long getStartTime() {
        return start;
    }

    public void save() {
        preferences.edit()
                .putLong(KEY_START, start)
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
            currentStatus = Status.from(sharedPreferences.getInt(KEY_CURRENT_STATUS, Status.IDLE.getSerialisedValue()));
        } else if (key.equals(KEY_START)) {
            start = sharedPreferences.getLong(key, 0L);

        }
    }
}
