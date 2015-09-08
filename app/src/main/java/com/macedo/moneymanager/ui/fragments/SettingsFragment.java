package com.macedo.moneymanager.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.macedo.moneymanager.R;

/**
 * Created by Beatriz on 08/09/2015.
 */
public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}