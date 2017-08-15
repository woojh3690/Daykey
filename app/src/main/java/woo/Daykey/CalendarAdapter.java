package woo.Daykey;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

class CalendarAdapter extends FragmentPagerAdapter {
    private FmCalendar[] fragList;

    CalendarAdapter(FragmentManager fm, FmCalendar[] fragLists) {
        super(fm);
        this.fragList = fragLists;
    }

    @Override
    public Fragment getItem(int position) {
        return fragList[position];
    }

    @Override
    public int getCount() {
        return fragList.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
