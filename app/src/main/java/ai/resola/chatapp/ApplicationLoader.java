package ai.resola.chatapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

/**
 * Created by dotuan on 2017/03/30.
 */

public class ApplicationLoader extends Application {
    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            Log.i("ApplicationLoader", "onConfigurationChanged: " + newConfig.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
