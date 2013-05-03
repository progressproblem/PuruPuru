package com.example.PuruPuru;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.*;

public class Setting extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        CheckBoxPreference useAccelerometer = (CheckBoxPreference)findPreference("useAccelerometer");
        useAccelerometer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summary;
                if ((Boolean) newValue) {
                    summary = getText(R.string.use_accelerometer_summary_on).toString();
                } else {
                    summary = getText(R.string.use_accelerometerm_summary_off).toString();
                }
                preference.setSummary(summary);
                return true;
            }
        });

        CheckBoxPreference useEyeglass = (CheckBoxPreference)findPreference("useEyeglass");
        useEyeglass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summary;
                if ((Boolean) newValue) {
                    summary = getText(R.string.use_eyeglass_summary_on).toString();
                } else {
                    summary = getText(R.string.use_eyeglass_summary_off).toString();
                }
                preference.setSummary(summary);
                return true;
            }
        });

        ListPreference oscillationMode = (ListPreference)findPreference("oscillationMode");
        oscillationMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Resources res = getResources();
                int no = Integer.parseInt((String)newValue);
                TypedArray ob = res.obtainTypedArray(R.array.oscillation_mode_entries);
                preference.setSummary(ob.getText(no));
                return true;
            }
        });
    }

    public static boolean useAccelerometer(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("useAccelerometer", false);
    }

    public static boolean useEyeglass(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("useEyeglass", false);
    }

    public static int oscillationMode(Context con){
        String str = PreferenceManager.getDefaultSharedPreferences(con).getString("oscillationMode", "1");
        return Integer.parseInt(str);
    }

}
