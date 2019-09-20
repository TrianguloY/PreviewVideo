package com.trianguloy.popupvideo.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.trianguloy.popupvideo.R;
import com.trianguloy.popupvideo.utils.PackageManager;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void onButtonClick(View view) {
        switch (view.getId()){
            case R.id.abt_btn_appPage:
                PackageManager.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://triangularapps.blogspot.com/")), R.string.toast_noBrowser, this);
                break;
            case R.id.abt_btn_openStore:
                PackageManager.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())), R.string.toast_noBrowser, this);
                break;
            case R.id.abt_btn_shareStore:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());

                PackageManager.startActivity(Intent.createChooser(share, getString(R.string.abt_shareStore)), R.string.toast_error, this);
                break;
        }
    }
}
