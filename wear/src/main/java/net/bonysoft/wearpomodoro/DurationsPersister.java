package net.bonysoft.wearpomodoro;

import android.content.SharedPreferences;

class DurationsPersister {

    private final SharedPreferences preferences;

    public DurationsPersister(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void storeDurationFor(PomodoroStatus status, int duration) {
        preferences.edit()
                .putInt(status.name(), duration)
                .apply();
    }

    public int getDurationFor(PomodoroStatus status) {
        int defaultValue = status.getDefaultDurationMinutes();
        return preferences.getInt(status.name(), defaultValue);
    }
}
