
package com.ppsbbs.imewebnetkey.services;

import android.app.Application;
import android.content.Context;
//import android.text.ClipboardManager;
import android.content.ClipboardManager;

public class EnvironmentApplication extends Application {

    private static Context context = null;
    private static ClipboardManager clipboardmanager = null;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
		clipboardmanager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
    }
    public static Context getContext() {
        return context;
    }
    public static ClipboardManager getClipboardManager() {
        return clipboardmanager;
    }
}