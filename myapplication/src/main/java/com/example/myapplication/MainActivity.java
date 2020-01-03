package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.example.myapplication.fragments.PageOneFragment;
import com.example.myapplication.fragments.PageThreeFragment;
import com.example.myapplication.fragments.PageTwoFragment;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {
    static final String[] LIST_MENU = {"LIST1", "LIST2", "LIST3"} ;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;



    Fragment pgoneFrag;
    Fragment pgtwoFrag;
    Fragment pgthrFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);*/

        setContentView(R.layout.activity_main);




        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.getCurrentItem();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        pgoneFrag = new PageOneFragment();
        pgtwoFrag = new PageTwoFragment();
        pgthrFrag = new PageThreeFragment();
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return pgoneFrag;
            } else if (position == 1) {
                return pgtwoFrag;
            } else if (position == 2) {
                return pgthrFrag;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}

