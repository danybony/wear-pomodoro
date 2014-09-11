package net.bonysoft.wearpomodoro;

import android.content.Context;
import android.content.SharedPreferences;

public class Pomodoro implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_START = "net.bonysoft.wearpomodoro.KEY_START";
    private static final String KEY_CURRENT_STOPPAGE = "net.bonysoft.wearpomodoro.KEY_CURRENT_STOPPAGE";
    private static final String KEY_TOTAL_STOPPAGES = "net.bonysoft.wearpomodoro.KEY_TOTAL_STOPPAGES";
    private static final String KEY_END = "net.bonysoft.wearpomodoro.KEY_END";
    private static final String KEY_CURRENT_POMODORO = "net.bonysoft.wearpomodoro.KEY_CURRENT_POMODORO";
    private static final String PREFERENCES = "Pomodoro";

    private static final int WORK_INTERVAL_MINUTES = 25;
    private static final int SMALL_BREAK_MINUTES = 5;
    private static final int LONG_BREAK_MINUTES = 15;
    private static final int POMODORI_BEFORE_LONG_BREAK = 4;
    private static final int MINUTE_MILLIS = 60000;

    private long start;
    private long currentStoppage;
    private long totalStoppages;
    private long end;
    private int currentPomodoro;

    private final SharedPreferences preferences;

    public static Pomodoro newInstance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        long start = preferences.getLong(KEY_START, 0);
        long currentStoppage = preferences.getLong(KEY_CURRENT_STOPPAGE, 0);
        long totalStoppages = preferences.getLong(KEY_TOTAL_STOPPAGES, 0);
        long end = preferences.getLong(KEY_END, 0);
        int currentPomodoro = preferences.getInt(KEY_CURRENT_POMODORO, 0);
        return new Pomodoro(preferences, start, currentStoppage, totalStoppages, end, currentPomodoro);
    }

    private Pomodoro(SharedPreferences preferences, long start, long currentStoppage, long totalStoppages, long end, int currentPomodoro) {
        this.preferences = preferences;
        this.start = start;
        this.currentStoppage = currentStoppage;
        this.totalStoppages = totalStoppages;
        this.end = end;
        this.currentPomodoro = currentPomodoro;
    }

    public void start() {
        if (end > 0) {
            start = System.currentTimeMillis() - (end - start);
            end = 0;
        } else {
            start = System.currentTimeMillis();
        }
        save();
    }

    public void stop() {
        if (isPaused()) {
            resume();
        }
        end = System.currentTimeMillis();
        save();
    }

    public void pause() {
        currentStoppage = System.currentTimeMillis();
        save();
    }

    public void resume() {
        totalStoppages += System.currentTimeMillis() - currentStoppage;
        currentStoppage = 0L;
        save();
    }

    public void reset() {
        resetWithoutSave();
        save();
    }

    private void resetWithoutSave() {
        start = 0L;
        currentStoppage = 0L;
        totalStoppages = 0L;
        end = 0L;
    }

    public long getElapsed() {
        if (isRunning()) {
            return System.currentTimeMillis() - start;
        }
        if (end > 0) {
            return end - start;
        }
        return 0;
    }

    public boolean isRunning() {
        return start > 0 && end == 0;
    }

    public boolean isPaused() {
        return currentStoppage > 0;
    }

    public int getElapsedMinutes() {
        return (int) ((System.currentTimeMillis() - start) / MINUTE_MILLIS);
    }

    public long getTotalStoppages() {
        long now = System.currentTimeMillis();
        if (isPaused()) {
            return totalStoppages + (now - currentStoppage);
        }
        return totalStoppages;
    }

    public long getPlayed() {
        return getElapsed() - getTotalStoppages();
    }

    public long getStartTime() {
        return start;
    }

    public void save() {
        preferences.edit()
                .putLong(KEY_START, start)
                .putLong(KEY_CURRENT_STOPPAGE, currentStoppage)
                .putLong(KEY_TOTAL_STOPPAGES, totalStoppages)
                .putLong(KEY_END, end)
                .putInt(KEY_CURRENT_POMODORO, currentPomodoro)
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
            return;
        }

        long value = sharedPreferences.getLong(key, 0L);
        if (key.equals(KEY_START)) {
            start = value;
        } else if (key.equals(KEY_END)) {
            end = value;
        } else if (key.equals(KEY_CURRENT_STOPPAGE)) {
            currentStoppage = value;
        } else if (key.equals(KEY_TOTAL_STOPPAGES)) {
            totalStoppages = value;
        }
    }
}
