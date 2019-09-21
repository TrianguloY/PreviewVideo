package com.trianguloy.popupvideo.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.trianguloy.popupvideo.BuildConfig;
import com.trianguloy.popupvideo.R;
import com.trianguloy.popupvideo.utils.PackageManager;

/**
 * Main activity
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view) {
        Class cls;
        switch (view.getId()) {
            case R.id.m_btn_setup:
                cls = SetupActivity.class;
                break;
            case R.id.m_btn_about:
                cls = AboutActivity.class;
                break;
            case R.id.m_img_icon:
                Toast.makeText(this, getString(R.string.app_name) + ", by TrianguloY", Toast.LENGTH_SHORT).show();
                return;
            default:
                Log.d("SWITCH", view.toString());
                if (BuildConfig.DEBUG)
                    Toast.makeText(this, "Unknown view: " + view, Toast.LENGTH_LONG).show();
                return;
        }
        PackageManager.startActivity(new Intent(this, cls), R.string.toast_error, this);
    }
}
