package net.bonysoft.wearpomodoro.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import net.bonysoft.wearpomodoro.PomodoroStatus;
import net.bonysoft.wearpomodoro.R;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        SettingsPagerAdapter adapter = new SettingsPagerAdapter(
                viewPager,
                PomodoroStatus.getConfigurableStatuses(),
                getResources()
        );
        viewPager.setAdapter(adapter);
    }

}
