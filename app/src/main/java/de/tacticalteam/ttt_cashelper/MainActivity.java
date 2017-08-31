package de.tacticalteam.ttt_cashelper;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ((TextView) rootView.findViewById(R.id.argumentLabel1)).setText(getString(R.string.angleToTarget));
            ((TextView) rootView.findViewById(R.id.argumentLabel2)).setText(getString(R.string.distanceToTarget));
            if (sectionNumber == 1) {
                ((TextView) rootView.findViewById(R.id.argumentLabel3)).setText(getString(R.string.angleToIP));
                ((TextView) rootView.findViewById(R.id.argumentLabel4)).setText(getString(R.string.distanceToIP));
            } else {
                ((TextView) rootView.findViewById(R.id.argumentLabel3)).setText(getString(R.string.desiredHeading));
                ((TextView) rootView.findViewById(R.id.argumentLabel4)).setText(getString(R.string.desiredDistance));
            }

            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabClear);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((EditText) ((ViewGroup) view.getParent()).findViewById(R.id.argument1)).setText("");
                    ((EditText) ((ViewGroup) view.getParent()).findViewById(R.id.argument2)).setText("");
                    ((EditText) ((ViewGroup) view.getParent()).findViewById(R.id.argument3)).setText("");
                    ((EditText) ((ViewGroup) view.getParent()).findViewById(R.id.argument4)).setText("");
                }
            });

            fab = (FloatingActionButton) rootView.findViewById(R.id.fabCalculate);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View.OnClickListener snackBarOnClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            return;
                        }
                    };
                    final String argument1String = ((EditText) ((ViewGroup) view.getParent()).findViewById(R.id.argument1)).getText().toString();
                    final String argument2String = ((EditText) ((ViewGroup) view.getParent()).findViewById(R.id.argument2)).getText().toString();
                    final String argument3String = ((EditText) ((ViewGroup) view.getParent()).findViewById(R.id.argument3)).getText().toString();
                    final String argument4String = ((EditText) ((ViewGroup) view.getParent()).findViewById(R.id.argument4)).getText().toString();
                    if (TextUtils.isEmpty(argument1String) || TextUtils.isEmpty(argument2String) || TextUtils.isEmpty(argument3String) || TextUtils.isEmpty(argument4String)) {
                        Snackbar.make(view, getString(R.string.errorEmptyString), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.snackBarCloseAction), snackBarOnClickListener).show();
                        return;
                    }
                    final int argument1 = Integer.parseInt(argument1String);
                    final int argument2 = Integer.parseInt(argument2String);
                    final int argument3 = Integer.parseInt(argument3String);
                    final int argument4 = Integer.parseInt(argument4String);
                    if (argument1 > 359 || argument3 > 359) {
                        Snackbar.make(view, getString(R.string.errorAngleTooLarge), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.snackBarCloseAction), snackBarOnClickListener).show();
                        return;
                    }
                    final Pair<Integer, Integer> result;
                    if (sectionNumber == 1) {
                        result = calculate(argument1, argument2, argument3, argument4);
                    } else {
                        result = calculate(argument3 + 180, argument4, argument1 + 180, argument2);
                    }
                    Snackbar.make(view, getString(R.string.resultFor9Liner, result.first, result.second, result.second / 1000d), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.snackBarCloseAction), snackBarOnClickListener).show();
                }
            });

            return rootView;
        }

        private Pair<Integer, Integer> calculate(final int angleToTarget, final int distanceToTarget, final int angleToIP, final int distanceToIP) {
            final double radToTarget = compassToRad(angleToTarget);
            final double radToIP = compassToRad(angleToIP);
            final int distance = (int) Math.round(Math.sqrt(distanceToTarget * distanceToTarget + distanceToIP * distanceToIP - 2 * distanceToTarget * distanceToIP * Math.cos(radToTarget - radToIP)));
            final int angle = radToCompass(Math.atan2(distanceToTarget * Math.sin(radToTarget) - distanceToIP * Math.sin(radToIP), distanceToTarget * Math.cos(radToTarget) - distanceToIP * Math.cos(radToIP)));
            return new Pair<>(angle, distance);
        }

        private double compassToRad(final int degrees) {
            return -Math.PI / 180 * (degrees - 90);
        }

        private int radToCompass(final double rad) {
            int value = (int) Math.round(-180 / Math.PI * rad + 90) % 360;
            return value < 0 ? value + 360 : value;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tabGivenIP);
                case 1:
                    return getString(R.string.tabDesiredHeading);
            }
            return null;
        }
    }
}
