package com.trianguloy.popupvideo.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.trianguloy.popupvideo.BuildConfig;
import com.trianguloy.popupvideo.R;
import com.trianguloy.popupvideo.utils.PackageManager;
import com.trianguloy.popupvideo.utils.Preferences;

import java.util.List;

public class SetupActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Preferences prefs;

    // ------------------- Initialization -------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        prefs = new Preferences(this);

        // init things
        initAppSpinner();
        initFullScreen();
        initClickable();
        checkAvailability();
        checkOpener();
    }

    /**
     * Checks which app is the default
     */
    private void checkOpener() {
        PackageManager.PackageInfo otherApp = PackageManager.isOtherApp(this);
        findViewById(R.id.stp_ll_opener).setVisibility(otherApp != null ? View.VISIBLE : View.GONE);
        if (otherApp != null) {
            // set other app
            TextView opener = findViewById(R.id.stp_txt_opener);
            opener.setText(getString(R.string.txt_opener, otherApp.label));
            findViewById(R.id.stp_btn_opener).setTag(otherApp.packageName);
        }
    }

    /**
     * Sets the properties of the clickable text
     */
    private void initClickable() {
        TextView txt = findViewById(R.id.stp_txtL_testLink);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * Sets the visibility of the setAsDefault box
     */
    private void checkAvailability() {
        boolean b = PackageManager.amIAvailable(this);
        findViewById(R.id.stp_ll_setAsDefault).setVisibility(b ? View.GONE : View.VISIBLE);
        findViewById(R.id.stp_ll_testLink).setVisibility(b ? View.VISIBLE : View.GONE);
    }

    /**
     * Inits the fullscreen checkbox
     */
    private void initFullScreen() {
        CheckBox chbox = findViewById(R.id.stp_chkbx_fullscreen);

        // configure
        chbox.setChecked(prefs.getFullScreen());
        chbox.setOnClickListener(this);
    }

    /**
     * Inits the spinner
     */
    private void initAppSpinner() {
        Spinner app_choose = findViewById(R.id.stp_spn_action);

        // get apps
        List<PackageManager.PackageInfo> otherApps = PackageManager.getOtherApps(
                PackageManager.YOUTUBE_INTENT,
                true,
                this
        );

        // find selection
        int selection_index = 0;
        String selection = prefs.getApp();
        for (int i = 0; i < otherApps.size(); i++) {
            PackageManager.PackageInfo otherApp = otherApps.get(i);
            if (otherApp.packageName.equals(selection)) {
                selection_index = i + 1; // +1 for the none option added below
                break;
            }
        }

        if (selection_index == 0) {
            // the selected app wasn't found, remove
            prefs.setApp("");
        }

        // set the none option
        otherApps.add(0, new PackageManager.PackageInfo(null, getString(R.string.spinner_none)));

        // for debug only: set the invalid option
        if (BuildConfig.DEBUG)
            otherApps.add(new PackageManager.PackageInfo("invalid", "invalid"));

        // initialize adapter
        ArrayAdapter<PackageManager.PackageInfo> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, otherApps);

        // configure
        app_choose.setAdapter(adapter);
        app_choose.setSelection(selection_index);
        app_choose.setOnItemSelectedListener(this);
    }

    private void openSettings(String packageName) {
//        try {
//            startActivity(new Intent("com.android.settings.APP_OPEN_BY_DEFAULT_SETTINGS", Uri.parse("package:" + packageName)));
//        }catch (ActivityNotFoundException e){
        PackageManager.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName)), R.string.toast_noSettings, this);
//        }
    }

    // ------------------- onEvents -------------------


    @Override
    protected void onResume() {
        super.onResume();
        checkAvailability();
        checkOpener();
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.stp_btn_setAsDefault:
                // open settings screen
                openSettings(getPackageName());
                break;
            case R.id.stp_btn_opener:
                // open default app settings
                openSettings(view.getTag().toString());
                break;
            case R.id.stp_txtL_testLink:
                // Open youtube link
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.txtL_testLink)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PackageManager.startActivity(intent, R.string.toast_noApps, this);
                break;
        }
    }

    // ------------------- AdapterView.OnItemSelectedListener -------------------

    /**
     * Item selected on the spinner
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        prefs.setApp(((PackageManager.PackageInfo) adapterView.getItemAtPosition(i)).packageName);
    }

    /**
     * Nothing selected on the spinner (should never run)
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        prefs.setApp(null);
    }

    // ------------------- View.OnClickListener -------------------

    /**
     * Checkbox clicked
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.stp_chkbx_fullscreen) {
            prefs.setFullscreen(((CheckBox) view).isChecked());
        }
    }
}
