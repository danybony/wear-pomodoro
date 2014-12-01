package net.bonysoft.wearpomodoro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PomodoroTimer pomodoroTimer = PomodoroTimer.newInstance(this);
        if (pomodoroTimer.isRunning()) {
            PomodoroReceiver.setUpdate(this);
        } else {
            Intent startIntent = new Intent(PomodoroReceiver.ACTION_START);
            sendBroadcast(startIntent);
        }
        finish();
    }
}
