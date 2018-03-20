package edu.utep.cs.cs4330.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class AppPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();

    }

    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
    public static class PrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.myapppreferences, false);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.myapppreferences);

        }
        }
    }


