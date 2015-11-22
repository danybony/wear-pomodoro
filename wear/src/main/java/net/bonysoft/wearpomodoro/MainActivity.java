package net.bonysoft.wearpomodoro;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import net.bonysoft.wearpomodoro.settings.SettingsActivity;

public class MainActivity extends Activity implements
        DelayedConfirmationView.DelayedConfirmationListener {

    private static final long CONFIRMATION_DELAY = TimeUnit.SECONDS.toMillis(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DelayedConfirmationView delayedView = (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
        delayedView.setListener(this);

        PomodoroTimer pomodoroTimer = PomodoroTimer.newInstance(this);
        if (pomodoroTimer.isRunning()) {
            PomodoroReceiver.setUpdate(this);
        } else {
            updateConfigurationSummary();
            delayedView.setTotalTimeMs(CONFIRMATION_DELAY);
            delayedView.start();
        }
    }

    private void updateConfigurationSummary() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        DurationsPersister durationsPersister = new DurationsPersister(preferences);
        int workDuration = durationsPersister.getDurationFor(PomodoroStatus.WORK);
        int smallBreakDuration = durationsPersister.getDurationFor(PomodoroStatus.SMALL_BREAK);
        TextView summaryText = (TextView) findViewById(R.id.summary);
        summaryText.setText(getString(R.string.summary_message, workDuration, smallBreakDuration));
    }

    @Override
    public void onTimerFinished(View view) {
        Intent startIntent = new Intent(PomodoroReceiver.ACTION_START);
        sendBroadcast(startIntent);
        showConfirmation();
        finish();
    }

    private void showConfirmation() {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(
                ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION
        );
        intent.putExtra(
                ConfirmationActivity.EXTRA_MESSAGE,
                getString(R.string.confirm_start)
        );
        startActivity(intent);
        finish();
    }

    @Override
    public void onTimerSelected(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        finish();
    }
}
