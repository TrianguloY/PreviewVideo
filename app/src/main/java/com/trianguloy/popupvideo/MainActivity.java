package com.trianguloy.popupvideo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.List;

/**
 * Main activity
 */
public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Preferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = new Preferences(this);

        // init spinner
        initAppSpinner();
        initFullScreen();

    }

    /**
     * Inits the fullscreen checkbox
     */
    private void initFullScreen() {
        CheckBox chbox = findViewById(R.id.chkbx_fullscreen);

        // configure
        chbox.setChecked(prefs.getFullScreen());
        chbox.setOnClickListener(this);
    }

    /**
     * Inits the spinner
     */
    private void initAppSpinner() {
        Spinner app_choose = findViewById(R.id.spn_action);

        // get apps
        List<Packages.PackageInfo> otherApps = Packages.getOtherApps(
                Packages.YOUTUBE_INTENT,
                true,
                this
        );

        // find selection
        int selection_index = 0;
        String selection = prefs.getApp();
        for (int i = 0; i < otherApps.size(); i++) {
            Packages.PackageInfo otherApp = otherApps.get(i);
            if (otherApp.packageName.equals(selection)) {
                selection_index = i + 1; // +1 for the none option added below
                break;
            }
        }

        // set the none option
        otherApps.add(0, new Packages.PackageInfo(null, getString(R.string.spinner_none)));

        // initialize adapter
        ArrayAdapter<Packages.PackageInfo> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, otherApps);

        // configure
        app_choose.setAdapter(adapter);
        app_choose.setSelection(selection_index);
        app_choose.setOnItemSelectedListener(this);
    }


    /**
     * Item selected on the spinner
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        prefs.setApp(((Packages.PackageInfo) adapterView.getItemAtPosition(i)).packageName);
    }

    /**
     * Nothing selected on the spinner (should never run)
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        prefs.setApp(null);
    }

    /**
     * Checkbox clicked
     */
    @Override
    public void onClick(View view) {
        prefs.setFullscreen(((CheckBox) view).isChecked());
    }
}
