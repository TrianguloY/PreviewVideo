package com.trianguloy.popupvideo.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
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
    private ArrayAdapter<PackageManager.PackageInfo> adapter;

    private Spinner app_choose;
    private TextView txt_error;

    // ------------------- Initialization -------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        prefs = new Preferences(this);

        txt_error = findViewById(R.id.stp_txt_error);
        app_choose = findViewById(R.id.stp_spn_action);

        // init things
        initAppSpinner();
        initFullScreen();
        initClickable();
        checkAvailability();
    }

    /**
     * Checks if we are available and which app is the default
     */
    private void checkAvailability() {
        boolean error = false;

        // check if we are available
        if (!PackageManager.amIAvailable(this)) {
            // we are not available
            txt_error.setText(R.string.txt_setAsDefault);
            error = true;
        } else {
            // we are available

            // check if other is default
            PackageManager.PackageInfo otherApp = PackageManager.isOtherApp(this);
            if (otherApp != null) {
                // other is default
                txt_error.setText(getString(R.string.txt_opener, otherApp.label));
                error = true;
            }
        }

        if (error) {
            txt_error.append(getString(R.string.txt_instructions));
        }
        findViewById(R.id.stp_ll_error).setVisibility(error ? View.VISIBLE : View.GONE);
        findViewById(R.id.stp_ll_testLink).setVisibility(error ? View.GONE : View.VISIBLE);
    }

    /**
     * Sets the properties of the clickable text
     */
    private void initClickable() {
        TextView txt = findViewById(R.id.stp_txtL_testLink);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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
        // initialize adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);

        // configure
        app_choose.setAdapter(adapter);
        app_choose.setOnItemSelectedListener(this);

        checkAppSpinner();
    }

    private void checkAppSpinner() {

        // remove previous
        adapter.clear();

        // set the none option
        adapter.add(new PackageManager.PackageInfo(null, getString(R.string.spinner_none)));

        // get apps
        List<PackageManager.PackageInfo> otherApps = PackageManager.getOtherApps(
                PackageManager.YOUTUBE_INTENT,
                true,
                this
        );

        // find selection and add
        int selection_index = 0;
        String selection = prefs.getApp();
        for (int i = 0; i < otherApps.size(); i++) {
            PackageManager.PackageInfo otherApp = otherApps.get(i);
            adapter.add(otherApp);
            if (otherApp.packageName.equals(selection)) {
                selection_index = i + 1; // +1 for the none option added above
            }
        }

        if (selection_index == 0) {
            // the selected app wasn't found, remove
            prefs.setApp("");
        }


        // for debug only: set the invalid option
        if (BuildConfig.DEBUG)
            otherApps.add(new PackageManager.PackageInfo("invalid", "invalid"));

        app_choose.setSelection(selection_index);
    }

    private void openSettings(String packageName) {
//        try {
//            startActivity(new Intent("com.android.settings.APP_OPEN_BY_DEFAULT_SETTINGS", Uri.parse("package:" + packageName)));
//        }catch (ActivityNotFoundException e){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
        }
        PackageManager.startActivity(intent, R.string.toast_noSettings, this);
//        }
    }

    // ------------------- onEvents -------------------


    @Override
    protected void onResume() {
        super.onResume();
        checkAvailability();
        checkAppSpinner();
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.stp_btn_error:
                // open settings screen
                openSettings(getPackageName());
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
