package com.techambits.beya.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techambits.beya.vars.vars;


import com.techambits.beya.R;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;

public class Configuracion extends Fragment
{

    private String _urlWebService;
    gestionSharedPreferences sharedPreferences;
    private TabLayout tabLayout;

    public vars vars;


    public Configuracion()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences = new gestionSharedPreferences(getActivity());
        vars = new vars();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View inflatedView = inflater.inflate(R.layout.fragment_configuracion, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_person_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_credit_card_white_24dp));
        final ViewPager viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);


        viewPager.setAdapter(new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return inflatedView;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter
    {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs)
        {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    DatosUsuario tab1 = new DatosUsuario();
                    return tab1;
                case 1:
                    DatosTarjeta tab2 = new DatosTarjeta();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount()
        {
            return mNumOfTabs;
        }
    }

}
