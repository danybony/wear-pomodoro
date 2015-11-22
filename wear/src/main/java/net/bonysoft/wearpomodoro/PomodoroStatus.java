package net.bonysoft.wearpomodoro;

import java.util.Arrays;
import java.util.List;

public enum PomodoroStatus {
    IDLE(-1, PomodoroTimer.IDLE_START_PATTERN, 0),
    WORK(0, PomodoroTimer.WORK_START_PATTERN, 25),
    SMALL_BREAK(1, PomodoroTimer.SMALL_BREAK_START_PATTERN, 5),
    LONG_BREAK(2, PomodoroTimer.LONG_BREAK_START_PATTERN, 15);

    private int serialisedValue;
    private long[] vibrationPattern;
    private int defaultDurationMinutes;

    PomodoroStatus(int serialisedValue, long[] vibrationPattern, int defaultDurationMinutes) {
        this.serialisedValue = serialisedValue;
        this.vibrationPattern = vibrationPattern;
        this.defaultDurationMinutes = defaultDurationMinutes;
    }

    public int getSerialisedValue() {
        return serialisedValue;
    }

    public long[] getVibrationPattern() {
        return vibrationPattern;
    }

    public int getDefaultDurationMinutes() {
        return defaultDurationMinutes;
    }

    public static PomodoroStatus from(int serialisedValue) {
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

    public static List<PomodoroStatus> getConfigurableStatuses() {
        return Arrays.asList(
                WORK,
                SMALL_BREAK,
                LONG_BREAK
        );
    }
}
