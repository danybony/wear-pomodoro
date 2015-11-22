package net.bonysoft.wearpomodoro.settings;

import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import net.bonysoft.wearpomodoro.PomodoroStatus;
import net.bonysoft.wearpomodoro.R;

public class SettingsPagerAdapter extends PagerAdapter {

    private final ViewPager viewPager;
    private final Resources resources;
    private final List<PomodoroStatus> configurableStatuses = new ArrayList<>();

    public SettingsPagerAdapter(ViewPager viewPager, List<PomodoroStatus> configurableStatuses, Resources resources) {
        this.viewPager = viewPager;
        this.resources = resources;
        this.configurableStatuses.addAll(configurableStatuses);
    }

    @Override
    public int getCount() {
        return configurableStatuses.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PomodoroStatus status = configurableStatuses.get(position);
        View view = View.inflate(container.getContext(), R.layout.duration_settings_layout, null);
        TextView title = (TextView) view.findViewById(R.id.status_title);
        title.setText(status.name());

        viewPager.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        viewPager.removeView((View) object);
    }
}
